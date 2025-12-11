import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from 'src/app/principal/models/Page';

@Injectable({
  providedIn: 'root',
})
export class CitaService {
  private URL_CITAS = 'http://localhost:8080/api/citas'; // Adjust the URL as necessary

  constructor(private http: HttpClient) { }

  obtenerCitas(page: number = 0, size: number = 5): Observable<Page<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_CITAS}`, { params });
  }

  obtenerCita(id: number): Observable<any> {
    return this.http.get<any>(`${this.URL_CITAS}/${id}`);
  }

  obtenerCitasPorFiltros(
    filtro: string,
    idCita?: any,
    fichaPaciente?: number,
    fecha?: string,
    page: number = 0,
    size: number = 10
  ): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('filtro', filtro.toString());

    if (idCita) {
      params = params.set('idCita', idCita.toString());
    }
    if (fichaPaciente) {
      params = params.set('fichaPaciente', fichaPaciente.toString());
    }
    if (fecha) {
      params = params.set('fecha', fecha);
    }

    return this.http.get<Page<any>>(`${this.URL_CITAS}/filtros`, { params });
  }

  registrarCita(cita: any): Observable<any> {
    return this.http.post<any>(`${this.URL_CITAS}`, cita);
  }

  obtenerCitasPendientes(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_CITAS}/pendientes`, {
      params,
    });
  }

  obtenerCitasFinalizadas(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_CITAS}/finalizadas`, {
      params,
    });
  }

  obtenerCitasCanceladas(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_CITAS}/canceladas`, {
      params,
    });
  }

  encontrarHorasLibresProfesional(
    profesionalId: number,
    fecha: string
  ): Observable<any> {
    return this.http.get<any>(
      `${this.URL_CITAS}/horas-libres/${profesionalId}/`,
      {
        params: { fecha },
      }
    );
  }

  reagendarCita(id: number, cita: any): Observable<any> {
    return this.http.put<any>(`${this.URL_CITAS}/reagendar/${id}`, cita);
  }

  finalizarCita(id: number): Observable<any> {
    return this.http.patch<any>(`${this.URL_CITAS}/finalizar/${id}`, {});
  }

  cancelarCita(id: number): Observable<any> {
    return this.http.patch<any>(`${this.URL_CITAS}/cancelar/${id}`, {});
  }

  faltaJustificada(id: number): Observable<any> {
    return this.http.patch<any>(
      `${this.URL_CITAS}/falta-justificada/${id}`,
      {}
    );
  }

  faltaInjustificada(id: number): Observable<any> {
    return this.http.patch<any>(
      `${this.URL_CITAS}/falta-injustificada/${id}`,
      {}
    );
  }

  //Filtros

  //Obtener citas por filtro convinado

  obtenerTodasLasCitasFiltro(
    filtro: string,
    page: number = 0,
    size: number = 20
  ): Observable<any> {
    let params = new HttpParams()
      .set('filtro', filtro)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.URL_CITAS}/filtro`, { params });
  }

  obtenerCitasPorProfesional(
    id: number,
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get(`${this.URL_CITAS}/profesional/${id}`, { params });
  }

  obtenerCitasPorArea(
    id: number,
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get(`${this.URL_CITAS}/area/${id}`, { params });
  }

  obtenerCitasPorAreas(
    areas: number[],
    page: number = 0,
    size: number = 5
  ): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('areas', areas.join(','));
    return this.http.get(`${this.URL_CITAS}/areas`, { params });
  }

  obtenerCitasPorPaciente(
    id: number,
    pageable: any
  ): Observable<Page<any>> {
    const url = `${this.URL_CITAS}/paciente/${id}`;
    let params = new HttpParams();
    params = params.append('page', String(pageable.page));
    params = params.append('size', String(pageable.size));
    return this.http.get<Page<any>>(url, { params });
  }
}
