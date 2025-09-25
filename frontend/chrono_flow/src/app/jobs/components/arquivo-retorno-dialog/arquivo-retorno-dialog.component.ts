import { Component, OnInit, Output, EventEmitter, Input, inject, ViewChild, TemplateRef, AfterViewInit, OnDestroy } from '@angular/core';
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
export class ArquivoRetornoDialogComponent implements OnInit, OnDestroy {
  @Output() close = new EventEmitter<void>();
  @Input() job: Job | null = null;

  @ViewChild('detailsTemplate', { static: true }) detailsTemplate!: TemplateRef<any>;

  arquivosRetorno: ArquivoRetorno[] = [];
  selectedArquivo: ArquivoRetorno | null = null;
  isMobile = window.innerWidth < 768;

  themeService = inject(ThemeService);

  constructor(private arquivoRetornoService: ArquivoRetornoService) { }

  ngOnInit(): void {
    if (this.job && this.job.id) {
      this.arquivoRetornoService.getArquivosRetornoByJobId(this.job.id).subscribe(data => {
        this.arquivosRetorno = data;
      });
    } else {
      console.warn('Job or Job ID not provided to ArquivoRetornoDialogComponent.');
    }
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.onResize);
  }

  private onResize = (): void => {
    this.isMobile = window.innerWidth < 768;
  }

  selectArquivo(arquivo: ArquivoRetorno): void {
    this.selectedArquivo = this.selectedArquivo === arquivo ? null : arquivo;
  }

  onClose(): void {
    this.close.emit();
  }
}
