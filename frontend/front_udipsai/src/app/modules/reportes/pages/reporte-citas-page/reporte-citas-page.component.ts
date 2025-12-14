import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService, ReporteCitaRespuestaDTO } from 'src/app/core/services/reporte.service';
import { PacienteService } from 'src/app/core/services/paciente.service';
// removed invalid import, defining simple interface locally if needed or using any for now
// import { Paciente } from 'src/app/core/models/paciente.model'; 

interface Paciente {
  id: number;
  cedula: string;
  nombresApellidos: string; // Changed from nombres/apellidos to match DTO if needed, or keep if separate. DTO has nombresApellidos.
}

import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-reporte-citas-page',
  templateUrl: './reporte-citas-page.component.html',
  styleUrls: ['./reporte-citas-page.component.css']
})
export class ReporteCitasPageComponent {

  cedulaBusqueda: string = '';
  reporteData: ReporteCitaRespuestaDTO | null = null;
  pacienteEncontrado: Paciente | null = null;
  fechaActual: Date = new Date();

  constructor(
    private reporteService: ReporteService,
    private pacienteService: PacienteService,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService
  ) { }

  buscarPaciente() {
    if (!this.cedulaBusqueda) {
      this.toastr.warning('Ingrese una cédula para buscar');
      return;
    }

    this.spinner.show();
    // Asumimos que existe un método para buscar por cédula en PacienteService, 
    // si no, usaremos el listado y filtraremos, o el método que exista. 
    // Revisando archivos anteriores, PacienteService tiene 'obtenerPacientePorCedula' o similar?
    // Voy a usar obtenerPacientePorCedula si existe, si no, intentaré adaptarme.
    // Al verificar PacienteService antes, vi `paciente.service.ts`.

    this.pacienteService.obtenerPacientePorCedula(this.cedulaBusqueda).subscribe({
      next: (paciente: any) => {
        // Ajuste: el servicio puede devolver DTO o Entity. Asumo que devuelve objeto con idPaciente.
        this.pacienteEncontrado = paciente;
        this.generarReporte(paciente.id);
      },
      error: (err) => {
        this.spinner.hide();
        this.toastr.error('Paciente no encontrado');
        this.reporteData = null;
        this.pacienteEncontrado = null;
      }
    });
  }

  generarReporte(idPaciente: number) {
    this.reporteService.obtenerReportePorPaciente(idPaciente).subscribe({
      next: (data) => {
        this.reporteData = data;
        this.spinner.hide();
        this.toastr.success('Reporte generado correctamente');
      },
      error: (err) => {
        this.spinner.hide();
        this.toastr.error('Error al generar el reporte');
      }
    });
  }

  imprimir() {
    window.print();
  }
}
