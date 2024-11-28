import {Component, OnInit} from '@angular/core';
import {NavbarComponent} from "../navbar/navbar.component";
import {HttpClient} from "@angular/common/http";
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

    this.http.post<PostResponse[]>('http://localhost:8080/api/post/drafts', { author })
      .subscribe({
        next: (data) => {
          this.drafts = data;
          console.log(this.drafts);
          console.log(this.drafts[1].lastEditedDate)
        },
        error: (err) => {
          console.error('Error fetching drafts:', err);
        }
      });
  }

  publish(id: number): void {
    this.http.post('http://localhost:8080/api/post/publishDraft', id).subscribe({
      next: () => {
        this.getDrafts()
      },
      error: (error) => {
        console.error('Error publishing draft:', error);
        alert('Er is een fout opgetreden bij het publishen van de draft.');
      }
    });
  }

  edit(id: number): void {
    this.router.navigate(['/edit-draft', id]);
  }
}
