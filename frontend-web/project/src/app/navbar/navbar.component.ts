import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import {NgForOf, NgIf} from "@angular/common";
import {Notification} from "../models/notification";
import {PostService} from "../services/post.service";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    NgIf,
    NgForOf
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  currentUser: any;
  notifications: Notification[] = [];

  constructor(
    private postService: PostService,
    public authService: AuthService,
    private router: Router
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getNotifications();
  }

  getNotifications(): void {
    this.postService.getNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
      },
      error: (err) => {
        console.error('Error getting notifications:', err);
      }
    });
  }

  deleteNotification(notificationId: number): void {
    this.postService.deleteNotification(notificationId).subscribe({
      next: () => {
        this.getNotifications();
      },
      error: (err) => {
        console.error('Error deleting notification:', err);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  toDrafts() {
    this.router.navigate(['/drafts']);
  }

  toCreatePost() {
    this.router.navigate(['/create-post']);
  }

  toPosts() {
    this.router.navigate(['/posts']);
  }

  toReviews() {
    this.router.navigate(['/reviews']);
  }
}
