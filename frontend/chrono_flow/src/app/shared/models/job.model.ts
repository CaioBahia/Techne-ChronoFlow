import { Empresa } from "../enums/empresa.enum";

export interface Job {
  id: number;
  nome: string;
  cronExpression: string;
  status: 'AGENDADO' | 'EXECUTANDO' | 'CONCLUIDO' | 'FALHA';
  ultimaExecucao: string | null;
  proximaExecucao: string | null;
  empresa: Empresa;
}
