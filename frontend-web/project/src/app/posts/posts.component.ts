import { Component, OnInit } from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {CommonModule, DatePipe, NgForOf, NgIf} from "@angular/common";
import {PostResponse} from "../models/post.response";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../auth.service";
import {Router} from "@angular/router";
import {FormsModule} from "@angular/forms";

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

  getPosts(): void {
    const author = this.currentUser.username;

    this.http.get<PostResponse[]>('http://localhost:8080/api/post/posts')
      .subscribe({
        next: (data) => {
          this.posts = data.map(post => ({
            ...post,
            publicationDate: this.formatDate(post.publicationDate)
          }));

          this.posts.sort((a, b) => {
            const dateA = new Date(a.publicationDate).getTime();
            const dateB = new Date(b.publicationDate).getTime();
            return dateB - dateA; // Newest first
          });

          this.filteredPosts = [...this.posts];
          console.log(this.posts);
        },
        error: (err) => {
          console.error('Error fetching drafts:', err);
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
