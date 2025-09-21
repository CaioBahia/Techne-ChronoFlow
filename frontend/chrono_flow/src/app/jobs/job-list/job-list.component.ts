import { Component, OnInit, inject, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Job } from '../../shared/models/job.model';
import { JobService } from '../job.service';
import { JobFormComponent } from '../job-form/job-form.component';
import { JobDetailsComponent } from '../job-details/job-details.component';
import { ThemeService } from '../../core/theme.service';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, JobFormComponent, JobDetailsComponent],
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

  themeService = inject(ThemeService);

  constructor(private jobService: JobService, private elementRef: ElementRef) { }

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
    this.jobService.getJobs().subscribe(jobs => {
      this.jobs = jobs;
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
  }

  closeJobForm(): void {
    this.showJobForm = false;
    this.jobToEdit = null;
  }

  saveJob(job: Job): void {
    this.jobService.saveJob(job);
    this.loadJobs();
    this.closeJobForm();
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
      this.jobService.deleteJob(job.id);
      this.loadJobs();
    }
  }
}
