import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NavbarComponent} from "../navbar/navbar.component";
import {PostResponse} from "../models/post.response";
import {AuthService} from "../auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Component({
  selector: 'app-edit-draft',
  standalone: true,
  imports: [
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './edit-draft.component.html',
  styleUrl: './edit-draft.component.css'
})
export class EditDraftComponent implements OnInit {
  title: string = '';
  description: string = '';
  id: number = 0;
  currentUser: any;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient
  ) {
    this.currentUser = this.authService.currentUserValue;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      this.getDraft(this.id);
    });
  }

  getDraft(id: number): void {
    const headers = new HttpHeaders({
      'Role': this.currentUser.role
    });

    this.http.post<PostResponse>('http://localhost:8081/api/post/draft', { id }, { headers })
      .subscribe({
        next: (data) => {
          this.title = data.title;
          this.description = data.description;
        },
        error: (err) => {
          console.error('Error fetching drafts:', err);
        }
      });
  }

  saveDraft(): void {
    const postData = {
      id: this.id,
      title: this.title,
      description: this.description
    };

    const headers = new HttpHeaders({
      'Role': this.currentUser.role
    });

    this.http.post('http://localhost:8081/api/post/editDraft', postData, { headers }).subscribe({
      next: () => {
        this.router.navigate(['/drafts']);
      },
      error: (error) => {
        console.error('Error editing draft:', error);
        alert('Er is een fout opgetreden bij het editen van de draft.');
      }
    });
  }
}
