import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { UsuarioService } from './usuario.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  isAuth = false;
  token: string = '';

  URL_AUTH = environment.BASE_URL_AUTH;

  constructor(public http: HttpClient, private usuarioService: UsuarioService) {}

  postLogin(user: any) {
    return this.http.post(this.URL_AUTH, user, { observe: 'response' });
  }

  login(cedula: string, contrasenia: string) {
    const formData = {
      cedula: cedula,
      contrasenia: contrasenia,
    };

    return new Promise((resolve) => {
      this.postLogin(formData).subscribe({
        next: (res: any) => {
          if (res && res.headers) {
            const token = res.headers.get('Authorization');
            if (token) {
              sessionStorage.setItem('token', token);
              this.token = token;
              this.isAuth = true;
              const data = { resCode: 0 };
              this.guardarUsuarioActual(res.body);
              if (this.getUsuarioRoles().includes('PROFESIONAL')) {
                this.usuarioService
                  .obtenerProfesionalPorUsuario(this.getUsuarioActualId())
                  .subscribe(
                    (data: any) => {
                      sessionStorage.setItem(
                        'profesional',
                        JSON.stringify(data)
                      );
                    },
                    (error) => {
                      console.error(
                        'Error fetching professional details:',
                        error
                      );
                    }
                  );
              }

              console.log(this.getUsuarioRoles());
              resolve(data);
            } else {
              const data = {
                resCode: -1,
                error: 'No token found in response headers',
              };
              resolve(data);
            }
          } else {
            const data = { resCode: -1, error: 'No headers in response' };
            resolve(data);
          }
        },
        error: (err) => {
          let e;
          if (err.status === 400) {
            e = err.error.error_description;
          } else if (err.status == 403) {
            e = 'Credenciales incorrectas, intente de nuevo';
          } else {
            e =
              'Las credenciales son incorrectas o el servicio no está disponible por el momento, intente de nuevo más tarde';
          }
          const data = { resCode: -1, error: e };
          resolve(data);
        },
      });
    });
  }

  guardarUsuarioActual(usuario: any) {
    sessionStorage.setItem('usuario', JSON.stringify(usuario));
  }

  logout() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('usuario');
    if(sessionStorage.getItem('profesional')) {
      sessionStorage.removeItem('profesional');
    }
    this.isAuth = false;
  }

  isAuthenticated(): boolean {
    if(sessionStorage.getItem('token')&&sessionStorage.getItem('usuario')) {
      return true;
    }else {
     return false;
    }
  }

  getToken() {
    return this.token;
  }

  getUsuarioActual() {
    return JSON.parse(sessionStorage.getItem('usuario') || '{}');
  }

  getUsuarioActualId() {
    return this.getUsuarioActual().idUsuario;
  }

  getUsuarioRoles() {
    const roles = this.getUsuarioActual().usuarioRoles.map(
      (usuarioRol: any) => usuarioRol.rol.nombre
    );
    return roles;
  }
}
