export interface Coordinador {
  idUsuario: number;
  cedula: string;
  estado: string;
  nombres: string;
  apellidos: string;
  email: string;
  celular: string;
  roles: any[];
  areas: any[];
  idCoordinador: number;
  coorEstado: string;
  contrasenia?: string;
}
