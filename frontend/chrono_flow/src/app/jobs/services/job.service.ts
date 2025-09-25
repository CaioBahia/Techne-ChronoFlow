import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Job } from '@shared/models/job.model';
import { Empresa } from '@shared/enums/empresa.enum';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getJobs(): Observable<Job[]> {
    return this.http.get<Job[]>(`${this.apiUrl}/jobs`);
  }

  saveJob(job: Job): Observable<Job> {
    if (job.id) {
      return this.http.put<Job>(`${this.apiUrl}/jobs/${job.id}`, job);
    } else {
      return this.http.post<Job>(`${this.apiUrl}/jobs`, job);
    }
  }

  deleteJob(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/jobs/${id}`);
  }
}
