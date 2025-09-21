import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Job } from '../../shared/models/job.model';
import { Empresa, EmpresaMapping } from '../../shared/enums/empresa.enum';

@Component({
  selector: 'app-job-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './job-form.component.html',
  styleUrls: ['./job-form.component.css']
})
export class JobFormComponent implements OnInit {
  @Input() job: Job | null = null;
  @Output() save = new EventEmitter<Job>();
  @Output() close = new EventEmitter<void>();

  jobForm: FormGroup;
  empresaOptions = Object.values(Empresa);
  empresaMapping = EmpresaMapping;

  constructor(private fb: FormBuilder) {
    this.jobForm = this.fb.group({
      id: [null],
      nome: ['', Validators.required],
      cronExpression: ['', Validators.required],
      empresa: ['', Validators.required],
      status: ['AGENDADO', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.job) {
      this.jobForm.patchValue(this.job);
    }
  }

  onSubmit(): void {
    if (this.jobForm.valid) {
      this.save.emit(this.jobForm.value);
    }
  }

  onClose(): void {
    this.close.emit();
  }
}
