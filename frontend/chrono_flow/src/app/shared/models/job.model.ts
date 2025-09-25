import { Empresa } from "@shared/enums/empresa.enum";

export interface Job {
  id: number;
  nome: string;
  cronExpression: string;
  status: 'AGENDADO' | 'EXECUTANDO' | 'CONCLUIDO' | 'FALHA';
  ultimaExecucao: Date | string | null;
  proximaExecucao: Date | string | null;
  empresa: Empresa;
}
