import { Component, OnInit, NgZone, inject, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Job } from '@shared/models/job.model';
import { JobService } from '../../services/job.service';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobDetailsComponent } from '../job-details/job-details.component';
import { ThemeService } from '@core/theme.service';
import { ArquivoRetornoDialogComponent } from '../arquivo-retorno-dialog/arquivo-retorno-dialog.component';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, JobFormComponent, JobDetailsComponent, ArquivoRetornoDialogComponent],
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css']
})
export class JobListComponent implements OnInit {
  jobs: Job[] = [];
  showJobForm = false;
  jobToEdit: Job | null = null;
  showJobDetails = false;
  jobToShowDetails: Job | null = null;
  openDropdownJobId: number | null = null;
  showArquivoRetornoDialog = false;
  selectedJobForArquivoRetorno: Job | null = null;
  isLoading = false;
  saveError: string | null = null; // To hold the error message

  themeService = inject(ThemeService);

  constructor(private jobService: JobService, private elementRef: ElementRef, private zone: NgZone) { }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.openDropdownJobId = null;
    }
  }

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.isLoading = true;
    this.jobService.getJobs().subscribe({
      next: jobs => {
        this.jobs = jobs.map(job => {
          if (job.proximaExecucao && typeof job.proximaExecucao === 'string') {
            return {
              ...job,
              proximaExecucao: new Date(job.proximaExecucao)
            };
          }
          return job;
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading jobs:', err);
        this.isLoading = false;
      }
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
        this.zone.run(() => {
          const index = this.jobs.findIndex(j => j.id === savedJob.id);
          if (index !== -1) {
            this.jobs[index] = savedJob;
          } else {
            this.jobs.unshift(savedJob);
          }
          this.isLoading = false;
          this.closeJobForm();
        });
      },
      error: (err: HttpErrorResponse) => {
        this.zone.run(() => {
          this.saveError = err.error?.error || 'Ocorreu um erro desconhecido.';
          this.isLoading = false;
        });
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
          this.loadJobs();
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
