import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface ReporteCitaDTO {
    fecha: string;
    hora: string;
    profesional: string;
    area: string;
}

export interface ReporteCitaRespuestaDTO {
    pacienteNombreCompleto: string;
    citas: ReporteCitaDTO[];
}

@Injectable({
    providedIn: 'root'
})
export class ReporteService {

    private apiUrl = `${environment.apiUrl}/citas/reporte`;

    constructor(private http: HttpClient) { }

    obtenerReportePorPaciente(idPaciente: number): Observable<ReporteCitaRespuestaDTO> {
        return this.http.get<ReporteCitaRespuestaDTO>(`${this.apiUrl}/paciente/${idPaciente}`);
    }
}
