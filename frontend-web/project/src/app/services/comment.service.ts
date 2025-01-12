import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommentResponse } from '../models/comment.response';
import { AuthService } from '../auth.service';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private apiUrl = environment.commentApiUrl;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHttpHeaders(): HttpHeaders {
    const currentUser = this.authService.currentUserValue;
    return new HttpHeaders({
      'Role': currentUser.role
    });
  }

  getCommentsForPost(postId: number): Observable<CommentResponse[]> {
    return this.http.get<CommentResponse[]>(`${this.apiUrl}/${postId}`, { headers: this.getHttpHeaders() });
  }

  addComment(postId: number, description: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}`, {
      description,
      author: this.authService.currentUserValue.username,
      postId
    }, { headers: this.getHttpHeaders() });
  }

  updateComment(commentId: number, description: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${this.authService.currentUserValue.username}`, {
      commentId,
      description
    }, { headers: this.getHttpHeaders() });
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${commentId}/${this.authService.currentUserValue.username}`, { headers: this.getHttpHeaders() });
  }
}
