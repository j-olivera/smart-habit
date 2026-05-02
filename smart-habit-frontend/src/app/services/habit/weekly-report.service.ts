import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface WeeklyReportResponse {
  id: number;
  userId: number;
  weekStart: string;
  weekEnd: string;
  aiContent: string;
  generatedAt: string;
}

export interface WeeklyReportSummary {
  id: number;
  weekStart: string;
  weekEnd: string;
  generatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class WeeklyReportService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/reports`;

  getReports(): Observable<WeeklyReportSummary[]> {
    return this.http.get<WeeklyReportSummary[]>(this.apiUrl);
  }

  getReportById(id: number): Observable<WeeklyReportResponse> {
    return this.http.get<WeeklyReportResponse>(`${this.apiUrl}/${id}`);
  }

  generateReport(): Observable<WeeklyReportResponse> {
    return this.http.post<WeeklyReportResponse>(`${this.apiUrl}/weekly/generate`, {});
  }
}
