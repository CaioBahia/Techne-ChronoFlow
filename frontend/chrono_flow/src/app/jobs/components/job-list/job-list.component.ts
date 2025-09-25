import { Component, OnInit, inject, HostListener, ElementRef, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Job, JobStatusUpdate } from '@shared/models/job.model';
import { JobService } from '../../services/job.service';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobDetailsComponent } from '../job-details/job-details.component';
import { ThemeService } from '@core/theme.service';
import { ArquivoRetornoDialogComponent } from '../arquivo-retorno-dialog/arquivo-retorno-dialog.component';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { SseService } from '../../services/sse.service';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, JobFormComponent, JobDetailsComponent, ArquivoRetornoDialogComponent],
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('flash', [
      // Estado inicial (sem estilo extra)
      state('normal', style({})),
      // Estado para quando a atualização acontece
      state('updated', style({ backgroundColor: '#fffbe0' })), // Um amarelo bem claro
      // Transição: quando o estado muda para 'updated', anima para o amarelo e depois volta ao normal
      transition('* => updated', [
        animate('300ms ease-out', style({ backgroundColor: '#fffbe0' })),
        animate('700ms ease-in', style({ backgroundColor: 'transparent' }))
      ])
    ])
  ]
})
export class JobListComponent implements OnInit, OnDestroy {
  jobs: Job[] = [];
  showJobForm = false;
  jobToEdit: Job | null = null;
  showJobDetails = false;
  jobToShowDetails: Job | null = null;
  openDropdownJobId: number | null = null;
  showArquivoRetornoDialog = false;
  selectedJobForArquivoRetorno: Job | null = null;
  isLoading = false;
  saveError: string | null = null;
  private sseSubscription: Subscription | undefined;

  themeService = inject(ThemeService);

  constructor(private jobService: JobService, private sseService: SseService, private elementRef: ElementRef) { }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.openDropdownJobId = null;
    }
  }

  ngOnInit(): void {
    this.loadJobs();
    this.subscribeToJobUpdates();
  }

  ngOnDestroy(): void {
    if (this.sseSubscription) {
      this.sseSubscription.unsubscribe();
    }
  }

  loadJobs(): void {
    this.isLoading = true;  
    this.jobService.getJobs().subscribe({
      next: jobs => {
        this.jobs = jobs;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading jobs:', err);
        this.isLoading = false;
      }
    });
  }

  private subscribeToJobUpdates(): void {
    this.sseSubscription = this.sseService.getJobUpdates().subscribe({
      next: (update: JobStatusUpdate) => {
        const index = this.jobs.findIndex(j => j.id === update.id);
        console.log('SSE: Job update received', update);
        if (index !== -1) {
          // Cria um novo array para garantir a detecção de mudanças do Angular
          const updatedJobs = [...this.jobs];
          // Mescla o job existente com a atualização formatada, garantindo que a data correta seja usada.
          updatedJobs[index] = { 
            ...this.jobs[index], // Pega o job existente (com nome, empresa, etc.)
            ...update   // Sobrescreve status, ultimaExecucao e a proximaExecucao.
          };
          // Adiciona uma propriedade temporária para controlar a animação
          (updatedJobs[index] as any).animationState = 'updated';
          this.jobs = updatedJobs;
        }
      },
      error: (err) => console.error('Error subscribing to job updates:', err)
    });
  }

  toggleDropdown(event: MouseEvent, jobId: number): void {
    event.stopPropagation();
    this.openDropdownJobId = this.openDropdownJobId === jobId ? null : jobId;
  }

  openJobForm(job: Job | null = null): void {
    this.jobToEdit = job;
    this.showJobForm = true;
    this.openDropdownJobId = null;
    this.saveError = null;
  }

  closeJobForm(): void {
    this.showJobForm = false;
    this.jobToEdit = null;
    this.isLoading = false;
  }

  saveJob(job: Job): void {
    this.isLoading = true;
    this.saveError = null;

    this.jobService.saveJob(job).subscribe({
      next: (savedJob: Job) => {
        const index = this.jobs.findIndex(j => j.id === savedJob.id);
        if (index !== -1) {
          // Atualiza o job existente
          // Cria um novo array para garantir que a detecção de mudanças do Angular seja acionada
          const updatedJobs = [...this.jobs];
          updatedJobs[index] = savedJob;
          this.jobs = updatedJobs;
        } else {
          // Adiciona um novo job no início da lista
          // O método unshift já modifica o array, mas para consistência, recriamos a referência.
          this.jobs = [savedJob, ...this.jobs];
        }
        this.isLoading = false;
        this.closeJobForm();
      },
      error: (err: HttpErrorResponse) => {
        this.saveError = err.error?.error || 'Ocorreu um erro desconhecido.';
        this.isLoading = false;
      }
    });
  }

  editJob(job: Job): void {
    this.openJobForm(job);
  }

  showDetails(job: Job): void {
    this.jobToShowDetails = job;
    this.showJobDetails = true;
    this.openDropdownJobId = null;
  }

  closeDetails(): void {
    this.showJobDetails = false;
    this.jobToShowDetails = null;
  }

  deleteJob(job: Job): void {
    this.openDropdownJobId = null;
    if (confirm(`Tem certeza que deseja excluir o job "${job.nome}"?`)) {
      this.isLoading = true;
      this.jobService.deleteJob(job.id).subscribe({
        next: () => {
          // Remove o job da lista local em vez de recarregar tudo
          this.jobs = this.jobs.filter(j => j.id !== job.id);
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error deleting job:', err);
          this.isLoading = false;
        }
      });
    }
  }

  openArquivoRetornoDialog(job: Job): void {
    this.selectedJobForArquivoRetorno = job;
    this.showArquivoRetornoDialog = true;
  }

  closeArquivoRetornoDialog(): void {
    this.showArquivoRetornoDialog = false;
    this.selectedJobForArquivoRetorno = null;
  }
}
