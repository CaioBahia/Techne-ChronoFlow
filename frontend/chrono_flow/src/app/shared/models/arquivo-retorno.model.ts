export interface Transacao {
  idTransacao: string;
  valor: number;
  tipo: 'PAGAMENTO' | 'RECEBIMENTO';
}

export interface ConteudoArquivoRetorno {
  nomeEmpresa: string;
  lote: string;
  dataGeracao: string;
  transacoes: Transacao[];
  erro: string | null;
}

export interface ArquivoRetorno {
  id: number;
  nomeArquivo: string;
  conteudo: ConteudoArquivoRetorno;
  dataProcessamento: string;
  status: 'PROCESSADO' | 'FALHA';
}
