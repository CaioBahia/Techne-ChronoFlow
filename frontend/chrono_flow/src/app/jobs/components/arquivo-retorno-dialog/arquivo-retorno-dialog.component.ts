import { Component, OnInit, Output, EventEmitter, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArquivoRetornoService } from '../../services/arquivo-retorno.service';
import { ArquivoRetorno, Transacao } from '@shared/models/arquivo-retorno.model';
import { Job } from '@shared/models/job.model';
import { ThemeService } from '@core/theme.service';

@Component({
  selector: 'app-arquivo-retorno-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './arquivo-retorno-dialog.component.html',
  styleUrls: ['./arquivo-retorno-dialog.component.css']
})
export class ArquivoRetornoDialogComponent implements OnInit {
  @Output() close = new EventEmitter<void>();
  @Input() job: Job | null = null;

  arquivosRetorno: ArquivoRetorno[] = [];
  selectedArquivo: ArquivoRetorno | null = null;

  themeService = inject(ThemeService);

  constructor(private arquivoRetornoService: ArquivoRetornoService) { }

  ngOnInit(): void {
    if (this.job && this.job.id) {
      this.arquivoRetornoService.getArquivosRetornoByJobId(this.job.id).subscribe(data => {
        this.arquivosRetorno = data;
      });
    } else {
      // Handle case where job or job.id is not available, maybe fetch all or show error
      console.warn('Job or Job ID not provided to ArquivoRetornoDialogComponent.');
      // Optionally, fetch all if that's a desired fallback
      // this.arquivoRetornoService.getArquivosRetorno().subscribe(data => {
      //   this.arquivosRetorno = data;
      // });
    }
  }

  selectArquivo(arquivo: ArquivoRetorno): void {
    this.selectedArquivo = this.selectedArquivo === arquivo ? null : arquivo;
  }

  onClose(): void {
    this.close.emit();
  }
}
