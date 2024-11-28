import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NavbarComponent} from "../navbar/navbar.component";
import {PostResponse} from "../models/post.response";
import {AuthService} from "../auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";

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

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      this.getDraft(this.id);
    });
  }

  getDraft(id: number): void {
    this.http.post<PostResponse>('http://localhost:8080/api/post/draft', { id })
      .subscribe({
        next: (data) => {
          this.title = data.title;
          this.description = data.description;
          console.log(data);
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

    console.log(postData);

    this.http.post('http://localhost:8080/api/post/editDraft', postData).subscribe({
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
