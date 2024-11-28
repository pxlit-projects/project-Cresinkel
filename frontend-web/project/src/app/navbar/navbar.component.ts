import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  constructor(public authService: AuthService, private router: Router) {}

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