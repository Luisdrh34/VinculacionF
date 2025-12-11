import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AreaDTO } from '../models/AreaDTO';

@Injectable({
  providedIn: 'root',
})
export class AreaService {
  private baseUrl = 'http://localhost:8080/api/areas'; // URL base del backend

  constructor(private http: HttpClient) { }

  // Obtener todas las áreas activas
  obtenerAreas(): Observable<AreaDTO[]> {
    return this.http.get<AreaDTO[]>(`${this.baseUrl}`);
  }

  // Obtener un área por su ID
  obtenerAreaPorId(id: number): Observable<AreaDTO> {
    return this.http.get<AreaDTO>(`${this.baseUrl}/${id}`);
  }

  // Obtener un área por su nombre
  obtenerAreaPorNombre(nombre: string): Observable<AreaDTO> {
    return this.http.get<AreaDTO>(`${this.baseUrl}/${nombre}`);
  }

  // Registrar un área
  registrarArea(nombre: string): Observable<AreaDTO> {
    return this.http.post<AreaDTO>(`${this.baseUrl}`, nombre, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    });
  }

  // Actualizar un área
  actualizarArea(id: number, nombre: string): Observable<AreaDTO> {
    return this.http.put<AreaDTO>(`${this.baseUrl}/${id}`, nombre, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    });
  }

  // Eliminar un área
  eliminarArea(id: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/${id}`);
  }

  // Activar un área
  activarArea(id: number): Observable<string> {
    return this.http.patch<string>(`${this.baseUrl}/activar/${id}`, null);
  }

  // Desactivar un área
  desactivarArea(id: number): Observable<string> {
    return this.http.patch<string>(`${this.baseUrl}/desactivar/${id}`, null);
  }

  // Bloquear un área
  bloquearArea(id: number): Observable<string> {
    return this.http.patch<string>(`${this.baseUrl}/bloquear/${id}`, null);
  }
}
