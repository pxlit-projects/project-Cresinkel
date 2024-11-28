import { Component, OnInit } from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {NgForOf, NgIf} from "@angular/common";
import {PostResponse} from "../models/post.response";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [
    NavbarComponent,
    NgForOf,
    NgIf
  ],
  templateUrl: './posts.component.html',
  styleUrl: './posts.component.css'
})
export class PostsComponent implements OnInit {
  posts: PostResponse[] = [];
  currentUser: any;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getPosts();
  }

  getPosts(): void {
    const author = this.currentUser.username;

    this.http.get<PostResponse[]>('http://localhost:8080/api/post/posts')
      .subscribe({
        next: (data) => {
          this.posts = data;
          console.log(this.posts);
        },
        error: (err) => {
          console.error('Error fetching drafts:', err);
        }
      });
  }
}
