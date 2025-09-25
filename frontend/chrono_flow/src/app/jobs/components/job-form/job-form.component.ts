import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Job } from '@shared/models/job.model';
import { Empresa, EmpresaMapping } from '@shared/enums/empresa.enum';

@Component({
  selector: 'app-job-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './job-form.component.html',
  styleUrls: ['./job-form.component.css']
})
export class JobFormComponent implements OnInit {
  @Input() job: Job | null = null;
  @Input() isLoading: boolean = false;
  @Input() saveError: string | null = null;
  @Output() save = new EventEmitter<Job>();
  @Output() close = new EventEmitter<void>();

  jobForm: FormGroup<{
    id: FormControl<number | null>;
    nome: FormControl<string | null>;
    cronExpression: FormControl<string | null>;
    empresa: FormControl<Empresa | '' | null>;
    status: FormControl<string | null>;
  }>;

  empresaOptions = Object.values(Empresa);
  empresaMapping = EmpresaMapping;

  constructor(private fb: FormBuilder) {
    this.jobForm = this.fb.group({
      id: new FormControl<number | null>(null),
      nome: new FormControl('', Validators.required),
      cronExpression: new FormControl('', Validators.required),
      empresa: new FormControl<Empresa | ''>('', Validators.required),
      status: new FormControl('AGENDADO', Validators.required)
    });
  }

  ngOnInit(): void {
    if (this.job) {
      this.jobForm.patchValue(this.job);
    }
  }

  onSubmit(): void {
    this.saveError = null; 
    if (this.jobForm.valid) {
      const jobToSave: Job = {
        ...this.job, // Começa com os valores do job original (se houver)
        ...this.jobForm.getRawValue(), // Sobrescreve com os valores do formulário
      } as Job;

      this.save.emit(jobToSave);
    }
  }

  onClose(): void {
    this.close.emit();
  }
}
