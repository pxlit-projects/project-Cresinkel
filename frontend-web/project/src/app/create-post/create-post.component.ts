import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormsModule } from "@angular/forms";
import { NavbarComponent } from "../navbar/navbar.component";
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.css'
})
export class CreatePostComponent {
  title: string = '';
  description: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {}

  onCreatePost(isDraft: boolean = false) {
    const currentUser = this.authService.currentUserValue;

    if (currentUser.role !== 'redacteur') {
      alert('Je hebt geen toestemming om posts aan te maken!');
      return;
    }

    const postData = {
      title: this.title,
      description: this.description,
      author: currentUser.username,
      isDraft: isDraft
    };

    console.log(postData);

    this.http.post('http://localhost:8080/api/post', postData).subscribe({
      next: () => {
        if (isDraft) {
          this.router.navigate(['/drafts']);
        } else {
          this.router.navigate(['/']);
        }

      },
      error: (error) => {
        console.error('Error creating post:', error);
        alert('Er is een fout opgetreden bij het aanmaken van de post.');
      }
    });
  }
}
