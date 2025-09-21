import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Job } from '../shared/models/job.model';
import { Empresa } from '../shared/enums/empresa.enum';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private mockJobs: Job[] = [
    {
      id: 1,
      nome: 'Nome do Job para Empresa A',
      cronExpression: '0 * * * * ?',
      status: 'AGENDADO',
      ultimaExecucao: null,
      proximaExecucao: '2025-09-21T15:00:00',
      empresa: Empresa.EMPRESA_A
    },
    {
      id: 2,
      nome: 'Job para B com erro',
      cronExpression: '0 0/5 * * * ?',
      status: 'FALHA',
      ultimaExecucao: '2025-09-21T14:55:00',
      proximaExecucao: '2025-09-21T15:00:00',
      empresa: Empresa.EMPRESA_B
    },
    {
      id: 3,
      nome: 'Job executando na C',
      cronExpression: '0 0 12 * * ?',
      status: 'EXECUTANDO',
      ultimaExecucao: '2025-09-21T12:00:00',
      proximaExecucao: '2025-09-22T12:00:00',
      empresa: Empresa.EMPRESA_C
    }
  ];

  constructor() { }

  getJobs(): Observable<Job[]> {
    return of(this.mockJobs);
  }

  saveJob(job: Job): void {
    if (job.id) {
      // Update
      const index = this.mockJobs.findIndex(j => j.id === job.id);
      if (index !== -1) {
        this.mockJobs[index] = { ...this.mockJobs[index], ...job };
      }
    } else {
      // Create
      const newJob: Job = {
        ...job,
        id: this.mockJobs.length + 1, // Simple id generation
        status: 'AGENDADO',
        ultimaExecucao: null,
        proximaExecucao: new Date().toISOString() // Placeholder
      };
      this.mockJobs.push(newJob);
    }
  }

  deleteJob(id: number): void {
    const index = this.mockJobs.findIndex(j => j.id === id);
    if (index !== -1) {
      this.mockJobs.splice(index, 1);
    }
  }
}
