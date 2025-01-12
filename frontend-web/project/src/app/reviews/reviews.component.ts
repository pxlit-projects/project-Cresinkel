import { Component, OnInit } from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {CommonModule, DatePipe, NgForOf, NgIf} from "@angular/common";
import {AuthService} from "../auth.service";
import {FormsModule} from "@angular/forms";
import {ReviewResponse} from "../models/review.response";
import {ReviewUpdateRequest} from "../models/review.update.request";
import {ReviewService} from "../services/review.service";

@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [
    NavbarComponent,
    NgForOf,
    NgIf,
    FormsModule,
    CommonModule
  ],
  templateUrl: './reviews.component.html',
  styleUrl: './reviews.component.css',
  providers: [DatePipe]
})
export class ReviewsComponent implements OnInit {
  reviews: ReviewResponse[] = [];
  currentUser: any;
  rejectionReason: string = '';

  constructor(
    private reviewService: ReviewService,
    private authService: AuthService,
    private datePipe: DatePipe
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getReviews();
  }

  getReviews(): void {
    this.reviewService.getReviews().subscribe({
      next: (data) => {
        this.reviews = data.map(review => ({
          ...review,
          publicationDate: this.formatDate(review.publicationDate)
        }));

        this.reviews.sort((a, b) => {
          const dateA = new Date(a.publicationDate).getTime();
          const dateB = new Date(b.publicationDate).getTime();
          return dateB - dateA; // Newest first
        });
      },
      error: (err) => {
        console.error('Error fetching drafts:', err);
      }
    });
  }

  formatDate(date: string): string {
    return this.datePipe.transform(date, 'dd MMMM yyyy HH:mm') || date;
  }

  sendIn(accepted: boolean, postId: number): void {
    const requestBody: ReviewUpdateRequest = {
      accepted: accepted,
      rejectionReason: this.rejectionReason
    };

    this.reviewService.updateReviewStatus(postId, requestBody).subscribe({
      next: () => {
        this.getReviews();
      },
      error: (err) => {
        console.error('Error sending in review:', err);
      }
    });
  }
}
