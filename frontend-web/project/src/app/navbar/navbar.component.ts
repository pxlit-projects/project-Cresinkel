import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  currentUser: any;

  constructor(public authService: AuthService, private router: Router) {
    this.currentUser = this.authService.currentUserValue;
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
}
