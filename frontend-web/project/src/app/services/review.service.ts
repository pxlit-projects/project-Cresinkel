import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReviewResponse } from '../models/review.response';
import { AuthService } from '../auth.service';
import { ReviewUpdateRequest } from "../models/review.update.request";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private apiUrl = environment.reviewApiUrl;

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

  getReviews(): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(this.apiUrl, { headers: this.getHttpHeaders() });
  }

  updateReviewStatus(postId: number, requestBody: ReviewUpdateRequest): Observable<ReviewResponse> {
    return this.http.put<ReviewResponse>(`${this.apiUrl}/${postId}`, requestBody, { headers: this.getHttpHeaders() });
  }
}
