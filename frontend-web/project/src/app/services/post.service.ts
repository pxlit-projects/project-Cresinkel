import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostResponse } from '../models/post.response';
import { AuthService } from '../auth.service';
import { Notification } from "../models/notification";
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private postApiUrl = environment.postApiUrl;
  private notificationsApiUrl = environment.notificationUrl;

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

  getPosts(): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${this.postApiUrl}/posts`, { headers: this.getHttpHeaders() });
  }

  createPost(postData: any): Observable<PostResponse> {
    return this.http.post<PostResponse>(this.postApiUrl, postData, { headers: this.getHttpHeaders() });
  }

  getDraft(id: number): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${this.postApiUrl}/draft`, { id }, { headers: this.getHttpHeaders() });
  }

  saveDraft(postData: { id: number, title: string, description: string }): Observable<void> {
    return this.http.post<void>(`${this.postApiUrl}/editDraft`, postData, { headers: this.getHttpHeaders() });
  }

  getDrafts(author: string): Observable<PostResponse[]> {
    return this.http.post<PostResponse[]>(`${this.postApiUrl}/drafts`, { author }, { headers: this.getHttpHeaders() });
  }

  sendInDraft(id: number): Observable<void> {
    return this.http.post<void>(`${this.postApiUrl}/sendInDraft`, id, { headers: this.getHttpHeaders() });
  }

  getNotifications(): Observable<Notification[]> {
    const author = this.authService.currentUserValue.username;
    return this.http.get<Notification[]>(`${this.notificationsApiUrl}?author=${author}`, { headers: this.getHttpHeaders() });
  }

  deleteNotification(notificationId: number): Observable<void> {
    return this.http.delete<void>(`${this.notificationsApiUrl}/${notificationId}`, { headers: this.getHttpHeaders() });
  }
}
