import { Component, OnInit } from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {CommonModule, DatePipe, NgForOf, NgIf} from "@angular/common";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthService} from "../auth.service";
import {Router} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {ReviewResponse} from "../models/review.response";
import {ReviewUpdateRequest} from "../models/review.update.request";

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
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    private datePipe: DatePipe
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getReviews();
  }

  private getHttpHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Role': this.currentUser.role
    });
  }

  getReviews(): void {
    this.http.get<ReviewResponse[]>('http://localhost:8082/api/review', { headers: this.getHttpHeaders() })
      .subscribe({
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

    this.http.put<ReviewResponse>(
      `http://localhost:8082/api/review/${postId}`,
      requestBody,
      { headers: this.getHttpHeaders() }
    ).subscribe({
        next: (data) => {
          this.getReviews();
        },
        error: (err) => {
          console.error('Error sending in review:', err);
        }
      });
  }
}
