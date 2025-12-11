export interface Secretaria {
  idUsuario: number;
  cedula: string;
  estado: string;
  nombres: string;
  apellidos: string;
  email: string;
  celular: string;
  roles: any[];
  areas: any[];
  idSecretaria: number;
  secEstado: string;
  contrasenia?: string;
}
