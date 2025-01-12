import { Component, OnInit } from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {CommonModule, NgForOf, NgIf} from "@angular/common";
import {PostResponse} from "../models/post.response";
import {FormsModule} from "@angular/forms";
import {CommentResponse} from "../models/comment.response";
import {PostService} from "../services/post.service";
import {CommentService} from "../services/comment.service";

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
  styleUrl: './posts.component.css'
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
    private postService: PostService,
    private commentService: CommentService
  ) {}

  ngOnInit(): void {
    this.getPosts();
  }

  getPosts(): void {
    this.postService.getPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.posts.sort((a, b) => new Date(b.publicationDate).getTime() - new Date(a.publicationDate).getTime());
        this.filteredPosts = [...this.posts];

        this.posts.forEach(post => this.getCommentsForPost(post.id));
      },
      error: () => {
        this.posts = [];
        this.filteredPosts = [];
      }
    });
  }

  getCommentsForPost(postId: number): void {
    this.commentService.getCommentsForPost(postId).subscribe(comments => {
      this.comments[postId] = comments;
    });
  }

  addComment(postId: number): void {
    const comment = this.newComment[postId]?.trim();
    if (comment) {
      this.commentService.addComment(postId, comment).subscribe(() => {
        this.getCommentsForPost(postId);
        this.newComment[postId] = '';
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
      this.commentService.updateComment(commentId, updatedDescription).subscribe(() => {
        const comment = this.comments[postId].find(c => c.commentId === commentId);
        if (comment) {
          comment.description = updatedDescription;
        }
        this.toggleEditMode(commentId);
      });
    }
  }

  deleteComment(commentId: number, postId: number): void {
    this.commentService.deleteComment(commentId).subscribe(() => {
      this.getCommentsForPost(postId);
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
}
