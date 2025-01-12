import {Component, OnInit} from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthService} from "../auth.service";
import {NgForOf, NgIf} from "@angular/common";
import {Router} from "@angular/router";
import {PostResponse} from "../models/post.response";

@Component({
  selector: 'app-drafts',
  standalone: true,
  imports: [
    NavbarComponent,
    NgForOf,
    NgIf
  ],
  templateUrl: './drafts.component.html',
  styleUrl: './drafts.component.css'
})
export class DraftsComponent implements OnInit {
  drafts: PostResponse[] = [];
  currentUser: any;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.getDrafts();
  }

  getDrafts(): void {
    const author = this.currentUser.username;
    const headers = new HttpHeaders({
      'Role': this.currentUser.role
    });

    this.http.post<PostResponse[]>('http://localhost:8081/api/post/drafts', { author }, { headers })
      .subscribe({
        next: (data) => {
          this.drafts = data;
        },
        error: (err) => {
          console.error('Error fetching drafts:', err);
        }
      });
  }

  sendIn(id: number): void {
    const headers = new HttpHeaders({
      'Role': this.currentUser.role
    });

    this.http.post('http://localhost:8081/api/post/sendInDraft', id, { headers }).subscribe({
      next: () => {
        this.getDrafts()
      },
      error: (error) => {
        console.error('Error sending in draft:', error);
        alert('Er is een fout opgetreden bij het inzenden van de draft.');
      }
    });
  }

  edit(id: number): void {
    this.router.navigate(['/edit-draft', id]);
  }
}
