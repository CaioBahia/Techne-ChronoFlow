import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ArquivoRetorno } from '@shared/models/arquivo-retorno.model';
import { HttpClient } from '@angular/common/http'; 
import { environment } from '@env/environment'; 

@Injectable({
  providedIn: 'root'
})
export class ArquivoRetornoService {

  private apiUrl = environment.apiUrl; 

  constructor(private http: HttpClient) { } 

  getArquivosRetorno(): Observable<ArquivoRetorno[]> {
    return this.http.get<ArquivoRetorno[]>(`${this.apiUrl}/arquivos`);
  }

  getArquivosRetornoByJobId(jobId: number): Observable<ArquivoRetorno[]> {
    return this.http.get<ArquivoRetorno[]>(`${this.apiUrl}/jobs/${jobId}/arquivos`);
  }

  getArquivoRetornoById(id: number): Observable<ArquivoRetorno> {
    return this.http.get<ArquivoRetorno>(`${this.apiUrl}/arquivos/${id}`);
  }
}
