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
import { Cita } from '../../interfaces/cita';

@Component({
  selector: 'app-agendamiento-citas',
  templateUrl: './agendamiento-citas.component.html',
  styleUrls: ['./agendamiento-citas.component.scss'],
})
export class AgendamientoCitasComponent implements OnInit {
  pacienteFilter = '';
  pacientesRespaldo: any[] = [];
  pacientesFiltrados: any[] = [];
  loading: boolean = false;
  pacientes: any[] = [];
  displayCertificado: boolean = false;
  certificadoPaciente: any | null = null;

  displayModal: boolean = false;
  displayHistorial: boolean = false;
  selectedPaciente: any | null = null;
  fechaCita: Date | null = null;
  horaCita: string = '';
  especialidadCita: number | null = null;
  especialistaCita: number | null = null;

  especialidades: any[] = [];

  especialistas: any[] = [];

  horasDisponibles: string[] = [];

  minDate: Date = new Date();

  citas: {
    fecha: Date;
    hora: string;
    especialidad: string;
    especialista: string;
    numeroFicha: string;
  }[] = [];
  historialCitas: any[] = [];

  // Propiedades de paginación
  totalRecords: number = 0;
  rows: number = 10; // Número de filas por página
  rowsOptions = [5, 10, 15, 20]; // Opciones para el dropdown de número de filas
  currentPage: number = 0; // Página actual

    // Propiedades de paginación Historial
    totalRecordsHistorial: number = 0;
    rowsHistorial: number = 10; // Número de filas por página
    rowsOptionsHistorial = [5, 10, 15, 20]; // Opciones para el dropdown de número de filas
    currentPageHistorial: number = 0; // Página actual


  constructor(
    private usuarioService: UsuarioService,
    private pacienteService: PacienteService,
    private citaService: CitaService,
    private areaService: AreaService,
    private toas: ToastrService,
    private datePipe: DatePipe
  ) {
    // Obtener pacientes
    this.pacienteService.obtenerPacientes().subscribe((response) => {
      this.pacientes = response.content;
    });
    // Obtener Areas
    this.loadAreas();
  }

  ngOnInit(): void {
    this.pacientesRespaldo = [...this.pacientes];
    this.pacientesFiltrados = [...this.pacientes];
  }

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
    this.loading = false;
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

  filterPacientes(event: Event): void {
    this.pacienteService
      .obtenerPacientesPorFiltro((event.target as HTMLInputElement).value)
      .subscribe((response) => {
        this.pacientes = response.content;
      });
  }

  agendarCita(paciente: Paciente): void {
    this.selectedPaciente = paciente;
    this.fechaCita = null;
    this.horaCita = '';
    this.especialidadCita = null;
    this.especialistaCita = null;
    this.displayModal = true;
  }

  loadCitasHistorial(event: any): void {
    if (event) {
      this.currentPage = event.first / event.rows;
      this.rows = event.rows;
    }
    this.citaService
      .obtenerCitasPorPaciente(this.selectedPaciente.id, {
        page: this.currentPage,
        size: this.rows,
      })
      .subscribe((response) => {
        this.historialCitas = response.content;
        this.totalRecordsHistorial = response.totalElements;
        this.displayHistorial = true;
      });

  }

  mostrarHistorialCitas(paciente: any,event?:any): void {
    this.selectedPaciente = paciente;
    const pageable = {
      page: this.currentPageHistorial,
      size: this.rowsHistorial,
    };
    this.citaService.obtenerCitasPorPaciente(paciente.id, pageable).subscribe(
      (response) => {
        this.historialCitas = response.content;
        this.totalRecordsHistorial = response.totalElements;
        this.displayHistorial = true;
      },
      (error) => {
        this.toas.error('Error al obtener el historial de citas');
      }
    );
  }

  guardarCita(): void {
    if (
      this.selectedPaciente &&
      this.fechaCita &&
      this.horaCita &&
      this.especialidadCita &&
      this.especialistaCita
    ) {
      const nuevaCita = {
        fecha: this.datePipe.transform(this.fechaCita, 'dd-MM-yyyy'),
        hora: this.horaCita,
        areaId: this.especialidadCita,
        profesionalId: this.especialistaCita,
        fichaPaciente: this.selectedPaciente.id,
      };
      console.log(nuevaCita);
      this.citaService.registrarCita(nuevaCita).subscribe(
        (response) => {
          this.toas.success('Cita agendada correctamente');
          this.displayModal = false;
          // Actualizar el historial de citas inmediatamente
          this.mostrarHistorialCitas(this.selectedPaciente);
        },
        (error) => {
          this.toas.error('Error al agendar la cita');
        }
      );
    } else {
      if (!this.selectedPaciente) {
        this.toas.error('Seleccione un paciente');
      }
      if (!this.fechaCita) {
        this.toas.error('Seleccione una fecha');
      }
      if (!this.horaCita) {
        this.toas.error('Seleccione una hora');
      }
      if (!this.especialidadCita) {
        this.toas.error('Seleccione una especialidad');
      }
      if (!this.especialistaCita) {
        this.toas.error('Seleccione un especialista');
      }
    }
  }

  buscarHorasDisponibles(): void {
    if (this.especialistaCita && this.fechaCita) {
      const fechaFormateada = this.datePipe.transform(
        this.fechaCita,
        'dd-MM-yyyy'
      );
      this.citaService
        .encontrarHorasLibresProfesional(
          this.especialistaCita,
          fechaFormateada!
        )
        .subscribe((response) => {
          this.horasDisponibles = response;
        });
    } else {
      if (!this.especialistaCita) {
        this.toas.error('Seleccione un especialista');
      }
      if (!this.fechaCita) {
        this.toas.error('Seleccione una fecha');
      }
    }
  }

  imprimirHistorial(): void {
    window.print();
  }

  onRowsChange(event: any) {
    this.rows = event.value;
    this.loadPasantesLazy({
      first: 0,
      rows: this.rows,
      page: this.currentPage,
    });
  }

  generarCertificadoAtencion(cita: any): void {
    console.log(cita)
    console.log(this.selectedPaciente)
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

  onFechaConsultaSelect(): void {
    if (
      this.certificadoPaciente.fechaConsulta &&
      this.certificadoPaciente.horaDesde
    ) {
      this.autocompletarCertificado();
    }
  }

  onHoraDesdeSelect(): void {
    if (
      this.certificadoPaciente.fechaConsulta &&
      this.certificadoPaciente.horaDesde
    ) {
      this.autocompletarCertificado();
    }
  }

  autocompletarCertificado(): void {
    const cita = this.citas.find(
      (cita) =>
        cita.numeroFicha === this.certificadoPaciente.numeroFicha &&
        new Date(cita.fecha).toDateString() ===
          new Date(this.certificadoPaciente.fechaConsulta).toDateString() &&
        cita.hora === this.certificadoPaciente.horaDesde
    );

    if (cita) {
      this.certificadoPaciente.area = cita.especialidad;
      this.certificadoPaciente.horaHasta = this.calcularHoraFin(cita.hora);
    } else {
      this.certificadoPaciente.area = '';
      this.certificadoPaciente.horaHasta = '';
      alert('No hubo cita a esa hora.');
    }
  }

  calcularHoraFin(horaInicio: string): string {
    const [hours, minutes] = horaInicio.split(':').map(Number);
    const endTime = new Date();
    endTime.setHours(hours + 1);
    endTime.setMinutes(minutes);
    return `${endTime.getHours().toString().padStart(2, '0')}:${endTime
      .getMinutes()
      .toString()
      .padStart(2, '0')}`;
  }

  imprimirCertificado(): void {
    window.print();
  }

  onRowsChangeHistorial(event: any) {
    this.rowsHistorial = event.value;
    this.loadCitasHistorial({
      first: 0,
      rows: this.rowsHistorial,
    });
  }
}
