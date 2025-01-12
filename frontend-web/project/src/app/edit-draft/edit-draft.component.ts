import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NavbarComponent} from "../navbar/navbar.component";
import {AuthService} from "../auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {PostService} from "../services/post.service";

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
    private postService: PostService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
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
    this.postService.getDraft(id).subscribe({
      next: (data) => {
        this.title = data.title;
        this.description = data.description;
      },
      error: (err) => {
        console.error('Error fetching draft:', err);
      }
    });
  }

  saveDraft(): void {
    const postData = {
      id: this.id,
      title: this.title,
      description: this.description
    };

    this.postService.saveDraft(postData).subscribe({
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
