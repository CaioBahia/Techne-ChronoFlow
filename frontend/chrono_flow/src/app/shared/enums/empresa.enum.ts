export enum Empresa {
  EMPRESA_A = 'EMPRESA_A',
  EMPRESA_B = 'EMPRESA_B',
  EMPRESA_C = 'EMPRESA_C'
}

export const EmpresaMapping: Record<Empresa, string> = {
  [Empresa.EMPRESA_A]: 'Empresa A',
  [Empresa.EMPRESA_B]: 'Empresa B',
  [Empresa.EMPRESA_C]: 'Empresa C',
};
