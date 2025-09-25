import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ArquivoRetorno } from '@shared/models/arquivo-retorno.model';
import { HttpClient } from '@angular/common/http'; // Added this import
import { environment } from '@env/environment'; // Added this import

@Injectable({
  providedIn: 'root'
})
export class ArquivoRetornoService {

  private apiUrl = environment.apiUrl; // Using API URL from environment

  constructor(private http: HttpClient) { } // Injected HttpClient

  getArquivosRetorno(): Observable<ArquivoRetorno[]> {
    // Assuming a general endpoint for all return files if needed
    return this.http.get<ArquivoRetorno[]>(`${this.apiUrl}/arquivos`);
  }

  getArquivosRetornoByJobId(jobId: number): Observable<ArquivoRetorno[]> {
    return this.http.get<ArquivoRetorno[]>(`${this.apiUrl}/jobs/${jobId}/arquivos`);
  }

  getArquivoRetornoById(id: number): Observable<ArquivoRetorno> {
    return this.http.get<ArquivoRetorno>(`${this.apiUrl}/arquivos/${id}`);
  }
}
