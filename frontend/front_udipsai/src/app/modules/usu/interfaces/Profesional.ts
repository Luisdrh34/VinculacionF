export interface Profesional {
  idUsuario: number;
  cedula: string;
  profEstado: string;
  nombres: string;
  apellidos: string;
  email: string;
  celular: string;
  roles: any[];
  areas: any[];
  idProfesional: number;
  especialidad?: string;
  contrasenia?: string;
}
