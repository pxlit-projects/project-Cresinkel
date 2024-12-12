import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import {NgForOf, NgIf} from "@angular/common";
import {PostResponse} from "../models/post.response";
import {Notification} from "../models/notification";
import {HttpClient} from "@angular/common/http";

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
    private http: HttpClient,
    public authService: AuthService,
    private router: Router
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getNotifications();
  }

  getNotifications(): void {
    const author = this.currentUser.username;
    this.http.get<Notification[]>(`http://localhost:8081/api/notifications?author=${author}`)
      .subscribe({
        next: (data) => {
          this.notifications = data;
        },
        error: (err) => {
          console.error('Error getting notifications:', err);
        }
      });
  }

  deleteNotification(notificationId: number): void {
    this.http.delete(`http://localhost:8081/api/notifications/${notificationId}`)
      .subscribe({
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
