import { Component, OnInit } from '@angular/core';
import { Paciente } from '../../interfaces/paciente';
import { UsuarioService } from 'src/app/core/services/usuario.service';
import { PacienteService } from 'src/app/core/services/paciente.service';
import { CitaService } from 'src/app/core/services/cita.service';
import { AreaService } from 'src/app/core/services/area.service';
import { Profesional } from 'src/app/modules/usu/interfaces/Profesional';
import { AreaDTO } from 'src/app/core/models/AreaDTO';
import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-agendamiento-citas',
  templateUrl: './agendamiento-citas.component.html',
  styleUrls: ['./agendamiento-citas.component.scss'],
})
export class AgendamientoCitasComponent implements OnInit {
  pacienteFilter = '';
  loading: boolean = false;
  pacientes: any[] = [];
  displayCertificado: boolean = false;
  certificadoPaciente: any | null = null;

  displayModal: boolean = false;
  displayHistorial: boolean = false;
  selectedPaciente: any | null = null;
  fechaCita: Date | null = null;

  // Logic for multiselection
  horaCita: string[] = [];
  horasVisuales: any[] = [];

  especialidadCita: number | null = null;
  especialistaCita: number | null = null;

  especialidades: any[] = [];
  especialistas: any[] = [];
  horasDisponibles: string[] = [];
  minDate: Date = new Date();

  citas: any[] = [];
  historialCitas: any[] = [];

  // Pagination
  totalRecords: number = 0;
  rows: number = 10;
  currentPage: number = 0;

  // Pagination History
  totalRecordsHistorial: number = 0;
  rowsHistorial: number = 10;
  currentPageHistorial: number = 0;

  constructor(
    private usuarioService: UsuarioService,
    private pacienteService: PacienteService,
    private citaService: CitaService,
    private areaService: AreaService,
    private toas: ToastrService,
    private datePipe: DatePipe
  ) {
    this.pacienteService.obtenerPacientes().subscribe((response) => {
      this.pacientes = response.content;
    });
    this.loadAreas();
  }

  ngOnInit(): void { }

  loadPacientes(event?: any) {
    this.loading = true;
    if (this.pacienteFilter != '') {
      this.pacienteService
        .obtenerPacientesPorFiltro(this.pacienteFilter)
        .subscribe((response) => {
          this.pacientes = response.content;
          this.totalRecords = response.totalElements;
          this.loading = false;
        });
    } else {
      if (event) {
        this.currentPage = event.first / event.rows;
        this.rows = event.rows;
      }
      this.pacienteService
        .obtenerPacientes(this.currentPage, this.rows)
        .subscribe((data) => {
          this.pacientes = data.content;
          this.totalRecords = data.totalElements;
          this.loading = false;
        });
    }
  }

  loadPasantesLazy(event: any) {
    this.loadPacientes(event);
  }

  loadAreas() {
    this.areaService.obtenerAreas().subscribe((response) => {
      this.especialidades = response.map((area: AreaDTO) => ({
        label: area.nombre,
        value: area.idArea,
      }));
    });
  }

  cargarEspecialistas(event: any) {
    this.usuarioService
      .obtenerProfesionalesPorArea(event.value, 0, 10000000)
      .subscribe((response) => {
        this.especialistas = response.content.map(
          (profesional: Profesional) => ({
            label: `${profesional.nombres} ${profesional.apellidos}`,
            value: profesional.idProfesional,
          })
        );
      });
  }

  agendarCita(paciente: Paciente): void {
    this.selectedPaciente = paciente;
    this.fechaCita = null;
    this.horaCita = [];
    this.horasVisuales = [];
    this.especialidadCita = null;
    this.especialistaCita = null;
    this.displayModal = true;
  }

  loadCitasHistorial(event: any): void {
    if (event) {
      this.currentPageHistorial = event.first / event.rows;
      this.rowsHistorial = event.rows;
    }
    this.citaService
      .obtenerCitasPorPaciente(this.selectedPaciente.id, {
        page: this.currentPageHistorial,
        size: this.rowsHistorial,
      })
      .subscribe((response) => {
        this.historialCitas = response.content;
        this.totalRecordsHistorial = response.totalElements;
        this.displayHistorial = true;
      });
  }

  mostrarHistorialCitas(paciente: any): void {
    this.selectedPaciente = paciente;
    this.loadCitasHistorial(null);
  }

  guardarCita(): void {
    if (
      this.selectedPaciente &&
      this.fechaCita &&
      this.horaCita.length > 0 &&
      this.especialidadCita &&
      this.especialistaCita
    ) {
      const fechaStr = this.datePipe.transform(this.fechaCita, 'dd-MM-yyyy');

      const citasObservables = this.horaCita.map(hora => {
        const nuevaCita = {
          fecha: fechaStr,
          hora: hora,
          areaId: this.especialidadCita,
          profesionalId: this.especialistaCita,
          fichaPaciente: this.selectedPaciente.id,
        };
        return this.citaService.registrarCita(nuevaCita);
      });

      forkJoin(citasObservables).subscribe(
        (responses) => {
          this.toas.success(`${responses.length} cita(s) agendada(s) correctamente`);
          this.displayModal = false;
          this.mostrarHistorialCitas(this.selectedPaciente);
        },
        (error) => {
          this.toas.error('Error al agendar las citas. Verifique disponibilidad.');
        }
      );
    } else {
      if (!this.selectedPaciente) this.toas.error('Seleccione un paciente');
      else if (!this.fechaCita) this.toas.error('Seleccione una fecha');
      else if (!this.especialidadCita) this.toas.error('Seleccione una especialidad');
      else if (!this.especialistaCita) this.toas.error('Seleccione un especialista');
      else if (this.horaCita.length === 0) this.toas.error('Seleccione al menos una hora');
    }
  }

  buscarHorasDisponibles(): void {
    if (this.especialistaCita && this.fechaCita) {
      const fechaFormateada = this.datePipe.transform(this.fechaCita, 'dd-MM-yyyy');
      this.citaService
        .encontrarHorasLibresProfesional(this.especialistaCita, fechaFormateada!)
        .subscribe((response) => {
          this.horasDisponibles = response;
          this.generarHorasVisuales();
        });
    } else {
      if (!this.especialistaCita) this.toas.error('Seleccione un especialista');
      if (!this.fechaCita) this.toas.error('Seleccione una fecha');
    }
  }

  generarHorasVisuales() {
    const startHour = 8;
    const endHour = 18;
    this.horasVisuales = [];

    // Create hourly slots
    for (let i = startHour; i <= endHour; i++) {
      const hourStr = `${i.toString().padStart(2, '0')}:00`;
      const isAvailable = this.horasDisponibles.includes(hourStr);
      this.horasVisuales.push({
        value: hourStr,
        label: hourStr,
        disabled: !isAvailable,
        selected: this.horaCita.includes(hourStr)
      });
    }
  }

  toggleHora(hora: any) {
    if (hora.disabled) return;

    const index = this.horaCita.indexOf(hora.value);
    if (index > -1) {
      this.horaCita.splice(index, 1);
      hora.selected = false;
    } else {
      this.horaCita.push(hora.value);
      hora.selected = true;
    }
  }

  imprimirHistorial(): void {
    window.print();
  }

  generarCertificadoAtencion(cita: any): void {
    this.certificadoPaciente = {
      nombre: this.selectedPaciente.nombresApellidos,
      numeroFicha: cita.fichaPaciente,
      area: cita.nombreArea,
      fechaConsulta: cita.fecha,
      horaDesde: cita.horainicio,
      horaHasta: cita.horafin,
    };
    this.displayCertificado = true;
  }

  imprimirCertificado(): void {
    window.print();
  }
}
