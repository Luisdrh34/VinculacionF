export interface Pasante {
  idUsuario: number;
  cedula: string;
  estado: string;
  nombres: string;
  apellidos: string;
  email: string;
  celular: string;
  roles: any[];
  areas: any[];
  idPasante: number;
  pasEstado: string;
  carrera: string;
  contrasenia?: string;
}
