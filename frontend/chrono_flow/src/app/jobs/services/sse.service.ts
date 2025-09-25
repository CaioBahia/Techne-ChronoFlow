import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JobStatusUpdate } from '../../shared/models/job.model';

@Injectable({
  providedIn: 'root',
})
export class SseService {
  constructor(private _zone: NgZone) {}

  getJobUpdates(): Observable<JobStatusUpdate> {
    return new Observable<JobStatusUpdate>((observer) => {
      const eventSource = new EventSource(`${environment.apiUrl}/sse/jobs`);

      eventSource.addEventListener('job-update', (event: MessageEvent) => {
        const jobData = JSON.parse(event.data);
        // Usamos NgZone para garantir que a detecção de mudanças do Angular seja acionada
        this._zone.run(() => {
          observer.next(jobData);
        });
      });

      eventSource.onerror = (error) => {
        this._zone.run(() => observer.error(error));
      };

      // Retorna uma função para fechar a conexão quando o Observable for "unsubscribed"
      return () => eventSource.close();
    });
  }
}