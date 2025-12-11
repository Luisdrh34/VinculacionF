import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Profesional } from 'src/app/modules/usu/interfaces/Profesional';
import { Page } from 'src/app/principal/models/Page';
import { Secretaria } from '../models/Secretaria';
import { Coordinador } from '../models/Cordinador';
import { Usuario } from '../models/Usuario';
import { CambiarContraseniaDTO } from '../models/CambiarContraseniaDTO';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private URL_PASANTES = 'http://localhost:8080/api/pasantes';
  private URL_PROFESIONALES = 'http://localhost:8080/api/profesionales';
  private URL_SECRETARIAS = 'http://localhost:8080/api/secretarias';
  private URL_COORDINADORES = 'http://localhost:8080/api/coordinadores';
  private URL_BASE_API = 'http://localhost:8080/api';
  constructor(private http: HttpClient) { }

  //PASANTES

  obtenerPasantes(page: number = 0, size: number = 5): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_PASANTES}`, { params });
  }

  obtenerPasantesPorFiltro(
    filtro: string,
    page: number,
    size: number
  ): Observable<Page<any>> {
    const params = new HttpParams()
      .set('filtro', filtro)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<any>>(`${this.URL_PASANTES}/filtro`, {
      params,
    });
  }
  obtenerPasante(cedula: string): Observable<any> {
    return this.http.get<any>(`${this.URL_PASANTES}/${cedula}`);
  }

  obtenerPasantesTodos(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_PASANTES}/todos`, { params });
  }

  obtenerPasantesInactivos(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_PASANTES}/inactivos`, {
      params,
    });
  }

  obtenerPasantesBloqueados(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_PASANTES}/bloqueados`, {
      params,
    });
  }

  obtenerPasantesEliminados(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_PASANTES}/eliminados`, {
      params,
    });
  }

  registrarPasante(pasante: any): Observable<any> {
    return this.http.post<any>(this.URL_PASANTES, pasante);
  }

  actualizarPasante(cedula: string, pasante: any): Observable<any> {
    return this.http.put<any>(`${this.URL_PASANTES}/${cedula}`, pasante);
  }

  eliminarPasante(id: any): Observable<string> {
    return this.http.delete<string>(`${this.URL_PASANTES}/${id}`);
  }

  activarPasante(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_PASANTES}/activar/${cedula}`,
      {}
    );
  }

  desactivarPasante(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_PASANTES}/desactivar/${cedula}`,
      {}
    );
  }

  bloquearPasante(id: any): Observable<string> {
    return this.http.patch<string>(`${this.URL_PASANTES}/bloquear/${id}`, {});
  }

  // PROFECIONALES
  // Ajusta la URL base según sea necesario

  obtenerProfesionales(
    page: number = 0,
    size: number = 5
  ): Observable<Page<Profesional>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Profesional>>(this.URL_PROFESIONALES, { params });
  }

  obtenerProfesionalesPorFiltro(
    filtro: string,
    page: number,
    size: number
  ): Observable<Page<Profesional>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filtro) {
      params = params.set('filtro', filtro);
    }

    return this.http.get<Page<Profesional>>(
      `${this.URL_PROFESIONALES}/filtro`,
      {
        params,
      }
    );
  }
  obtenerProfesional(cedula: string): Observable<Profesional> {
    return this.http.get<Profesional>(`${this.URL_PROFESIONALES}/${cedula}`);
  }

  obtenerProfesionalesTodos(
    page: number,
    size: number
  ): Observable<Page<Profesional>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Profesional>>(`${this.URL_PROFESIONALES}/todos`, {
      params,
    });
  }

  obtenerProfesionalesInactivos(
    page: number,
    size: number
  ): Observable<Page<Profesional>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Profesional>>(
      `${this.URL_PROFESIONALES}/inactivos`,
      {
        params,
      }
    );
  }

  obtenerProfesionalesBloqueados(
    page: number,
    size: number
  ): Observable<Page<Profesional>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Profesional>>(
      `${this.URL_PROFESIONALES}/bloqueados`,
      {
        params,
      }
    );
  }

  obtenerProfesionalesEliminados(
    page: number,
    size: number
  ): Observable<Page<Profesional>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Profesional>>(
      `${this.URL_PROFESIONALES}/eliminados`,
      {
        params,
      }
    );
  }

  obtenerProfesionalesPorArea(
    area: number,
    page: number,
    size: number
  ): Observable<Page<any>> {
    const params = new HttpParams()
      .set('area', area.toString())
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<any>>(`${this.URL_PROFESIONALES}/area/${area}`, {
      params,
    });
  }

  obtenerProfesionalPorUsuario(idUsuario: number): Observable<any> {
    return this.http.get<any>(`${this.URL_PROFESIONALES}/usuario/${idUsuario}`);
  }

  registrarProfesional(profesional: Profesional): Observable<Profesional> {
    return this.http.post<Profesional>(this.URL_PROFESIONALES, profesional);
  }

  actualizarProfesional(
    id: any,
    profesional: Profesional
  ): Observable<Profesional> {
    return this.http.put<Profesional>(
      `${this.URL_PROFESIONALES}/${id}`,
      profesional
    );
  }

  eliminarProfesional(id: any): Observable<string> {
    return this.http.delete<string>(`${this.URL_PROFESIONALES}/${id}`);
  }

  activarProfesional(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_PROFESIONALES}/activar/${cedula}`,
      null
    );
  }

  desactivarProfesional(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_PROFESIONALES}/desactivar/${cedula}`,
      null
    );
  }

  bloquearProfesional(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_PROFESIONALES}/bloquear/${cedula}`,
      null
    );
  }

  // SECRETARIAS

  obtenerSecretarias(page: number = 0, size: number = 5): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_SECRETARIAS}`, { params });
  }

  obtenerSecretariasPorFiltro(
    filtro: string,
    page: number,
    size: number
  ): Observable<Page<Secretaria>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filtro) {
      params = params.set('filtro', filtro);
    }

    return this.http.get<Page<Secretaria>>(`${this.URL_SECRETARIAS}/filtro`, {
      params,
    });
  }
  obtenerSecretaria(cedula: string): Observable<Secretaria> {
    return this.http.get<Secretaria>(`${this.URL_SECRETARIAS}/${cedula}`);
  }

  obtenerSecretariasTodas(page: number = 0, size: number = 5): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_SECRETARIAS}/todas`, { params });
  }

  obtenerSecretariasInactivas(
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_SECRETARIAS}/inactivas`, { params });
  }

  obtenerSecretariasBloqueadas(
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_SECRETARIAS}/bloqueadas`, { params });
  }

  obtenerSecretariasEliminadas(
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_SECRETARIAS}/eliminadas`, { params });
  }

  registrarSecretaria(secretaria: Secretaria): Observable<Secretaria> {
    return this.http.post<Secretaria>(this.URL_SECRETARIAS, secretaria);
  }

  actualizarSecretaria(
    cedula: string,
    secretaria: Secretaria
  ): Observable<Secretaria> {
    return this.http.put<Secretaria>(
      `${this.URL_SECRETARIAS}/${cedula}`,
      secretaria
    );
  }

  eliminarSecretaria(cedula: string): Observable<string> {
    return this.http.delete<string>(`${this.URL_SECRETARIAS}/${cedula}`);
  }

  activarSecretaria(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_SECRETARIAS}/activar/${cedula}`,
      {}
    );
  }

  desactivarSecretaria(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_SECRETARIAS}/desactivar/${cedula}`,
      {}
    );
  }

  bloquearSecretaria(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_SECRETARIAS}/bloquear/${cedula}`,
      {}
    );
  }

  //CORDINADORES
  obtenerCoordinadores(page: number = 0, size: number = 5): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_COORDINADORES}`, { params });
  }

  obtenerCoordinadoresPorFiltro(
    filtro: string,
    page: number,
    size: number
  ): Observable<Page<any>> {
    const params = new HttpParams()
      .set('filtro', filtro)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<any>>(`${this.URL_COORDINADORES}/filtro`, {
      params,
    });
  }

  obtenerCoordinador(cedula: string): Observable<Coordinador> {
    return this.http.get<Coordinador>(`${this.URL_COORDINADORES}/${cedula}`);
  }

  obtenerCoordinadoresTodos(
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_COORDINADORES}/todos`, { params });
  }

  obtenerCoordinadoresInactivos(
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_COORDINADORES}/inactivos`, {
      params,
    });
  }

  obtenerCoordinadoresBloqueados(
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_COORDINADORES}/bloqueados`, {
      params,
    });
  }

  obtenerCoordinadoresEliminados(
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(`${this.URL_COORDINADORES}/eliminados`, {
      params,
    });
  }

  registrarCoordinador(coordinador: Coordinador): Observable<Coordinador> {
    return this.http.post<Coordinador>(this.URL_COORDINADORES, coordinador);
  }

  actualizarCoordinador(
    cedula: string,
    coordinador: Coordinador
  ): Observable<Coordinador> {
    return this.http.put<Coordinador>(
      `${this.URL_COORDINADORES}/${cedula}`,
      coordinador
    );
  }

  eliminarCoordinador(cedula: string): Observable<string> {
    return this.http.delete<string>(`${this.URL_COORDINADORES}/${cedula}`);
  }

  activarCoordinador(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_COORDINADORES}/activar/${cedula}`,
      {}
    );
  }

  desactivarCoordinador(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_COORDINADORES}/desactivar/${cedula}`,
      {}
    );
  }

  bloquearCoordinador(cedula: string): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_COORDINADORES}/bloquear/${cedula}`,
      {}
    );
  }

  //USUARIOS

  // Obtener todos los Usuarios activos
  obtenerUsuarios(
    page: number = 0,
    size: number = 5
  ): Observable<Page<Usuario>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Usuario>>(`${this.URL_BASE_API}/usuarios`, {
      params,
    });
  }

  // Obtener todos los Usuarios (A, I, B, N)
  obtenerUsuariosTodos(
    page: number = 0,
    size: number = 5
  ): Observable<Page<Usuario>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Usuario>>(`${this.URL_BASE_API}/usuarios/todos`, {
      params,
    });
  }

  // Obtener un Usuario específico
  obtenerUsuario(cedula: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.URL_BASE_API}/usuarios/${cedula}`);
  }

  // Obtener todos los Usuarios inactivos
  obtenerUsuariosInactivos(
    page: number = 0,
    size: number = 5
  ): Observable<Page<Usuario>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Usuario>>(
      `${this.URL_BASE_API}/usuarios/inactivos`,
      { params }
    );
  }

  // Obtener todos los Usuarios bloqueados
  obtenerUsuariosBloqueados(
    page: number = 0,
    size: number = 5
  ): Observable<Page<Usuario>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Usuario>>(
      `${this.URL_BASE_API}/usuarios/bloqueados`,
      { params }
    );
  }

  // Obtener todos los Usuarios eliminados
  obtenerUsuariosEliminados(
    page: number = 0,
    size: number = 5
  ): Observable<Page<Usuario>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Usuario>>(
      `${this.URL_BASE_API}/usuarios/eliminados`,
      { params }
    );
  }

  // Eliminar un Usuario
  eliminarUsuario(cedula: string): Observable<void> {
    return this.http.delete<void>(`${this.URL_BASE_API}/usuarios/${cedula}`);
  }

  // Habilitar un Usuario
  habilitarUsuario(cedula: string): Observable<void> {
    return this.http.patch<void>(
      `${this.URL_BASE_API}/usuarios/habilitar/${cedula}`,
      {}
    );
  }

  // Deshabilitar un Usuario
  deshabilitarUsuario(cedula: string): Observable<void> {
    return this.http.patch<void>(
      `${this.URL_BASE_API}/usuarios/deshabilitar/${cedula}`,
      {}
    );
  }

  // Bloquear un Usuario
  bloquearUsuario(cedula: string): Observable<void> {
    return this.http.patch<void>(
      `${this.URL_BASE_API}/usuarios/bloquear/${cedula}`,
      {}
    );
  }
  // Editar un usuario
  editarUsuario(id: any, usuario: Usuario): Observable<string> {
    return this.http.put<any>(`${this.URL_BASE_API}/usuarios/${id}`, usuario);
  }

  // Cambiar contraseña de un Usuario
  cambiarContrasenia(
    cedula: string,
    peticion: CambiarContraseniaDTO
  ): Observable<string> {
    return this.http.patch<string>(
      `${this.URL_BASE_API}/usuarios/cambiarContrasenia/${cedula}`,
      peticion
    );
  }
}
