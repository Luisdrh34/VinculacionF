import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from 'src/app/principal/models/Page';

@Injectable({
  providedIn: 'root',
})
export class PacienteService {
  private URL_PACIENTES = 'http://localhost:8080/api/pacientes'; // Adjust the URL as necessary

  constructor(private http: HttpClient) { }

  obtenerPacientes(
    page: number = 0,
    size: number = 5
  ): Observable<Page<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<any>>(`${this.URL_PACIENTES}`, { params });
  }

  obtenerPacientesPorFiltro(
    filtro: String,
    page: number = 0,
    size: number = 20
  ): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('filtro', filtro.toString());

    return this.http.get<Page<any>>(`${this.URL_PACIENTES}/filtro`, {
      params,
    });
  }

  obtenerPaciente(id: number): Observable<any> {
    return this.http.get<any>(`${this.URL_PACIENTES}/${id}`);
  }

  obtenerPacientePorCedula(cedula: string): Observable<any> {
    // Assuming the backend supports filtering by cedula via the filter endpoint effectively returning the single patient
    // Or we might need to filter client-side if the API doesn't support direct cedula lookup.
    // For now, let's try using the existing 'filtro' endpoint which likely searches by name/cedula
    return new Observable(observer => {
      this.obtenerPacientesPorFiltro(cedula, 0, 1).subscribe({
        next: (page: any) => {
          if (page.content && page.content.length > 0) {
            // Check exact match if possible or return the first one
            observer.next(page.content[0]);
            observer.complete();
          } else {
            observer.error('Paciente no encontrado');
          }
        },
        error: (err) => observer.error(err)
      });
    });
  }

}
