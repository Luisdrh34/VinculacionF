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
        // Group consecutive appointments before assigning
        if (data && data.citas) {
          data.citas = this.groupConsecutiveAppointments(data.citas);
        }
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

  // Same logic as in AgendamientoCitasComponent
  groupConsecutiveAppointments(citas: any[]): any[] {
    if (!citas || citas.length === 0) return [];

    const grouped: any[] = [];
    let currentGroup: any = null;

    // Helper to normalize time strings for comparison (e.g. "09:00:00" -> "09:00")
    // Assuming format is HH:mm or HH:mm:ss. If just HH:mm, substring 0,5 works too.
    const normalizeTime = (t: string) => t && t.length >= 5 ? t.substring(0, 5) : t;

    for (const cita of citas) {
      if (!currentGroup) {
        // Ensure properties exist. 
        // Based on the HTML: cita.fecha, cita.hora, cita.profesional, cita.area
        // We will need to assume 'hora' is the start time. 
        // AND we need an END time to check consecutiveness. 
        // If the report DTO doesn't have end time, we might assume 1 hour duration?
        // Let's check the keys in the loop below.
        currentGroup = { ...cita, horaFinCalculada: this.calculateEndTime(cita.hora) };
        continue;
      }

      const sameDay = cita.fecha === currentGroup.fecha;
      const sameProf = cita.profesional === currentGroup.profesional;
      const sameArea = cita.area === currentGroup.area;

      // Check consecutiveness
      // If we don't have explicit end time in DTO, we assume 1 hour slots.
      // currentGroup.hora is Start.
      // We need to see if cita.hora == currentGroup.hora + 1 hour.

      const currentEndTime = currentGroup.horaFinCalculada;
      const nextStartTime = normalizeTime(cita.hora);

      if (sameDay && sameProf && sameArea && currentEndTime === nextStartTime) {
        // Extend
        currentGroup.horaFinCalculada = this.calculateEndTime(cita.hora);
        // Update the display 'hora' to be a range? 
        // We will format it properly at the end or update dynamically.
        // Let's store the raw start time in a separate field if needed, or just keep 'hora'.
      } else {
        // Push formatted group
        this.formatGroupTime(currentGroup);
        grouped.push(currentGroup);
        currentGroup = { ...cita, horaFinCalculada: this.calculateEndTime(cita.hora) };
      }
    }

    if (currentGroup) {
      this.formatGroupTime(currentGroup);
      grouped.push(currentGroup);
    }

    return grouped;
  }

  calculateEndTime(startTime: string): string {
    if (!startTime) return '';
    const normalized = startTime.length >= 5 ? startTime.substring(0, 5) : startTime;
    const [hour, top] = normalized.split(':').map(Number);
    const endHour = hour + 1;
    return `${endHour.toString().padStart(2, '0')}:${top.toString().padStart(2, '0')}`;
  }

  formatGroupTime(group: any) {
    // If we extended it, group.horaFinCalculada is the end time of the last merged slot.
    // group.hora is the start time.
    const start = group.hora.length >= 5 ? group.hora.substring(0, 5) : group.hora;
    // If it originally had a single slot, calculateEndTime gave us start+1h.
    // So regardless, we can format as Start - End.
    group.hora = `${start} - ${group.horaFinCalculada}`;
  }

  imprimir() {
    window.print();
  }

  validateNumberInput(event: KeyboardEvent): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }
}
