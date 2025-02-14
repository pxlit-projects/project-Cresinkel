import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormsModule } from "@angular/forms";
import { NavbarComponent } from "../navbar/navbar.component";
import {PostService} from "../services/post.service";

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
    private postService: PostService,
    private authService: AuthService,
    private router: Router,
  ) {}

  onCreatePost(isDraft: boolean = false): void {
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

    this.postService.createPost(postData).subscribe({
      next: () => {
        if (isDraft) {
          this.router.navigate(['/drafts']);
        } else {
          this.router.navigate(['/posts']);
        }
      },
      error: (error) => {
        console.error('Error creating post:', error);
        alert('Er is een fout opgetreden bij het aanmaken van de post.');
      }
    });
  }
}
