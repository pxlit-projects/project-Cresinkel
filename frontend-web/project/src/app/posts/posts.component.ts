import { Component, OnInit } from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {CommonModule, DatePipe, NgForOf, NgIf} from "@angular/common";
import {PostResponse} from "../models/post.response";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthService} from "../auth.service";
import {Router} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommentResponse} from "../models/comment.response";

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [
    NavbarComponent,
    NgForOf,
    NgIf,
    FormsModule,
    CommonModule
  ],
  templateUrl: './posts.component.html',
  styleUrl: './posts.component.css',
  providers: [DatePipe]
})
export class PostsComponent implements OnInit {
  posts: PostResponse[] = [];
  filteredPosts: PostResponse[] = [];
  currentUser: any;
  comments: { [postId: number]: CommentResponse[] } = {};
  newComment: { [postId: number]: string } = {};
  editMode: { [commentId: number]: boolean } = {};
  editCommentText: { [commentId: number]: string } = {};

  // Filters
  dateFilter: string = '';
  authorFilter: string = '';
  descriptionFilter: string = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    private datePipe: DatePipe
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getPosts();
  }

  private getHttpHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Role': this.currentUser.role
    });
  }

  getPosts(): void {
    this.http.get<PostResponse[]>('http://localhost:8081/api/post/posts')
      .subscribe({
        next: (data) => {
          this.posts = data.map(post => ({
            ...post,
            publicationDate: this.formatDate(post.publicationDate)
          }));

          this.posts.sort((a, b) => {
            const dateA = new Date(a.publicationDate).getTime();
            const dateB = new Date(b.publicationDate).getTime();
            return dateB - dateA;
          });

          this.posts.forEach(post => this.getCommentsForPost(post.id));

          this.filteredPosts = [...this.posts];
        },
        error: (err) => {
          console.error('Error fetching drafts:', err);
        }
      });
  }

  getCommentsForPost(postId: number): void {
    this.http.get<CommentResponse[]>(`http://localhost:8083/api/comment/${postId}`)
      .subscribe({
        next: (data) => {
          this.comments[postId] = data;
        },
        error: (err) => {
          console.error(`Error fetching comments for post ${postId}:`, err);
          this.comments[postId] = [];
        }
      });
  }

  addComment(postId: number): void {
    const comment = this.newComment[postId]?.trim();
    if (comment) {
      this.http.post(`http://localhost:8083/api/comment`, {
        description: comment,
        author: this.currentUser.username,
        postId: postId
      }, { headers: this.getHttpHeaders() })
        .subscribe({
          next: () => {
            if (!this.comments[postId]) {
              this.comments[postId] = [];
            }

            this.getCommentsForPost(postId);
            this.newComment[postId] = '';
          },
          error: (err) => {
            console.error(`Error adding comment to post ${postId}:`, err);
          }
        });
    }
  }

  toggleEditMode(commentId: number): void {
    this.editMode[commentId] = !this.editMode[commentId];
    if (this.editMode[commentId]) {
      const comment = Object.values(this.comments).flat().find(c => c.commentId === commentId);
      this.editCommentText[commentId] = comment?.description || '';
    }
  }

  saveComment(commentId: number, postId: number): void {
    const updatedDescription = this.editCommentText[commentId]?.trim();
    if (updatedDescription) {
      this.http.put(`http://localhost:8083/api/comment/${this.currentUser.username}`, {
        description: updatedDescription,
        commentId: commentId
      }, { headers: this.getHttpHeaders() })
        .subscribe({
          next: () => {
            const comment = this.comments[postId].find(c => c.commentId === commentId);
            if (comment) {
              comment.description = updatedDescription;
            }
            this.toggleEditMode(commentId);
          },
          error: (err) => {
            console.error(`Error updating comment ${commentId}:`, err);
          }
        });
    }
  }

  deleteComment(commentId: number, postId: number): void {
    this.http.delete(`http://localhost:8083/api/comment/${commentId}/${this.currentUser.username}`, { headers: this.getHttpHeaders() })
      .subscribe({
        next: () => {
          this.getCommentsForPost(postId);
        },
        error: (err) => {
          console.error(`Error deleting comment ${commentId}:`, err);
        }
      });
  }

  filterPosts(): void {
    this.filteredPosts = this.posts.filter(post => {
      return (
        (this.dateFilter ? post.publicationDate.includes(this.dateFilter) : true) &&
        (this.authorFilter ? post.author.toLowerCase().includes(this.authorFilter.toLowerCase()) : true) &&
        (this.descriptionFilter ? post.description.toLowerCase().includes(this.descriptionFilter.toLowerCase()) : true)
      );
    });
  }

  formatDate(date: string): string {
    return this.datePipe.transform(date, 'dd MMMM yyyy HH:mm') || date;
  }
}
