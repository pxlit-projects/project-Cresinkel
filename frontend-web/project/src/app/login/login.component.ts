import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username: string = '';
  role: string = 'redacteur'; // Stel standaard in op redacteur

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.login(this.username, this.role);
    this.router.navigate(['/create-post']);
  }
}
