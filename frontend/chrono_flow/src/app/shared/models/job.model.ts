import { Empresa } from "@shared/enums/empresa.enum";

export interface Job {
  id: number;
  nome: string;
  cronExpression: string;
  empresa: Empresa;
  status: string;
  ultimaExecucao: string | null;
  proximaExecucao: string | null;
}

export interface JobStatusUpdate {
  id: number;
  status: string;
  proximaExecucao: string | null;
  ultimaExecucao: string | null;
}