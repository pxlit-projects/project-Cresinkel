import {Component, OnInit} from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {AuthService} from "../auth.service";
import {NgForOf, NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {PostResponse} from "../models/post.response";
import {PostService} from "../services/post.service";

@Component({
  selector: 'app-drafts',
  standalone: true,
  imports: [
    NavbarComponent,
    NgForOf,
    NgIf
  ],
  templateUrl: './drafts.component.html',
  styleUrl: './drafts.component.css'
})
export class DraftsComponent implements OnInit {
  drafts: PostResponse[] = [];
  currentUser: any;

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private router: Router,
    ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getDrafts();
  }

  getDrafts(): void {
    const author = this.currentUser.username;
    this.postService.getDrafts(author).subscribe({
      next: (data) => {
        this.drafts = data;
      },
      error: (err) => {
        console.error('Error fetching drafts:', err);
      }
    });
  }

  sendIn(id: number): void {
    this.postService.sendInDraft(id).subscribe({
      next: () => {
        this.getDrafts();
      },
      error: (error) => {
        console.error('Error sending in draft:', error);
        alert('Er is een fout opgetreden bij het inzenden van de draft.');
      }
    });
  }

  edit(id: number): void {
    this.router.navigate(['/edit-draft', id]);
  }
}
