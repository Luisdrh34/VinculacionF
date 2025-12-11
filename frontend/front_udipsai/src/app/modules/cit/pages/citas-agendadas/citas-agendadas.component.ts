import { Component, OnInit } from '@angular/core';
import { Cita } from '../../interfaces/cita';
import { CitaService } from 'src/app/core/services/cita.service';
import { ToastrService } from 'ngx-toastr';
import { AreaService } from 'src/app/core/services/area.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-citas-agendadas',
  templateUrl: './citas-agendadas.component.html',
  styleUrls: ['./citas-agendadas.component.scss'],
})
export class CitasAgendadasComponent implements OnInit {
  citasRespaldo: any[] = [];
  citasFiltrados: any[] = [];
  citas: any[] = [];
  loading: boolean = false;
  bloquearHoras: boolean = true;
  filtroCitas: string = '';

  // Propiedades de paginación
  totalRecords: number = 0;
  rows: number = 10; // Número de filas por página
  rowsOptions = [5, 10, 15, 20]; // Opciones para el dropdown de número de filas
  currentPage: number = 0; // Página actual

  displayModal: boolean = false;
  selectedCita: any | null = null;
  especialidadCita: string = '';
  horaCita: string = '';
  fechaCita: Date | null = null;
  especialidades: { label: string; value: string }[] = [
    { label: 'Psicología Educativa', value: 'Psicología Educativa' },
    { label: 'Clínica', value: 'Clínica' },
    { label: 'Ponoaudiología', value: 'Ponoaudiología' },
    { label: 'Recuperación Pedagógica', value: 'Recuperación Pedagógica' },
    { label: 'Estimulación Temprana', value: 'Estimulación Temprana' },
    { label: 'Odontologia', value: 'Odontologia' },
    { label: 'Trabajo Social', value: 'Trabajo Social' },
  ];

  horasDisponibles: string[] = [
    '08:00',
    '09:00',
    '10:00',
    '11:00',
    '13:00',
    '14:00',
    '15:00',
    '16:00',
    '17:00',
  ];

  constructor(
    private citaService: CitaService,
    private toastr: ToastrService,
    private areaService: AreaService,
    private toas: ToastrService,
    private datePipe: DatePipe
  ) {
    this.loadCitas();
  }

  loadCitas(event?: any) {
    if (this.filtroCitas != '') {
      this.citaService
        .obtenerTodasLasCitasFiltro(this.filtroCitas)
        .subscribe((data) => {
          this.citas = data.content;
          this.totalRecords = data.totalElements;
        });
    } else {
      if (event) {
        this.currentPage = event.first / event.rows;
        this.rows = event.rows;
      }

      this.citaService
        .obtenerCitas(this.currentPage, this.rows)
        .subscribe((data) => {
          this.citas = data.content;
          this.totalRecords = data.totalElements;
        });
    }
  }

  loadCitasLazy(event: any) {
    this.loadCitas(event);
  }

  loadAreas() {
    this.areaService.obtenerAreas().subscribe((data) => {
      this.especialidades = data.map((area) => ({
        label: area.nombre,
        value: area.nombre,
      }));
    });
  }

  ngOnInit(): void {
    this.citasRespaldo = this.citas;
  }

  agendarCita(cita: any) {
    this.selectedCita = cita;
    this.selectedCita.fecha = this.convertirFecha(cita.fecha);
    this.displayModal = true;

    console.log(this.selectedCita);
  }

  convertirFecha(fechaString: any): Date {
    if (typeof fechaString === 'string') {
      const [day, month, year] = fechaString.split('-').map(Number);
      return new Date(year, month - 1, day);
    } else if (fechaString instanceof Date) {
      return fechaString; // Si ya es un objeto Date, devolverlo directamente
    } else {
      throw new Error('Formato de fecha no válido');
    }
  }

  filterCitas(event: any) {
    const query = event.target.value.toLowerCase();
    if (query) {
      this.citasFiltrados = this.citas.filter(
        (paciente) =>
          paciente.nombrePaciente.toLowerCase().includes(query) ||
          paciente.motivo.toLowerCase().includes(query) ||
          paciente.id.toString().includes(query) ||
          paciente.fechaCita.toString().includes(query) ||
          paciente.horaCita.toString().includes(query)
      );
      this.citas = this.citasFiltrados;
    } else {
      this.citas = this.citasRespaldo;
    }
  }

  cancelarCita(citaId: number) {
    this.loading = true;
    this.citaService.cancelarCita(citaId).subscribe({
      next: (data) => {
        this.toastr.success('Cita cancelada con éxito.');
        this.loading = false;
        this.loadCitas(); // Actualizar la lista de citas después de la cancelación
      },
      error: (error) => {
        this.toastr.error(
          'Error: ' + error.error?.message || 'Error desconocido.'
        );
        this.loading = false;
      },
    });
  }

  faltaJustificada(citaId: number) {
    this.loading = true;
    this.citaService.faltaJustificada(citaId).subscribe({
      next: (data) => {
        this.toastr.success('Falta a la cita justificada.');
        this.loading = false;
        this.loadCitas(); // Actualizar la lista de citas después de la falta justificada
      },
      error: (error) => {
        this.toastr.error(
          'Error: ' + error.error?.message || 'Error desconocido.'
        );
        this.loading = false;
      },
    });
  }

  faltaInjustificada(citaId: number) {
    this.loading = true;
    this.citaService.faltaInjustificada(citaId).subscribe({
      next: (data) => {
        this.toastr.success('Falta a la cita injustificada.');
        this.loading = false;
        this.loadCitas(); // Actualizar la lista de citas después de la falta injustificada
      },
      error: (error) => {
        this.toastr.error(
          'Error: ' + error.error?.message || 'Error desconocido.'
        );
        this.loading = false;
      },
    });
  }

  buscarHorasDisponibles(): void {
    if (this.selectedCita.profesionalId && this.selectedCita.fecha) {
      const fechaFormateada = this.datePipe.transform(
        this.selectedCita.fecha,
        'dd-MM-yyyy'
      );
      this.citaService
        .encontrarHorasLibresProfesional(
          this.selectedCita.profesionalId,
          fechaFormateada!
        )
        .subscribe((response) => {
          this.horasDisponibles = response;
          this.bloquearHoras = false;
        });
    } else {
      if (!this.selectedCita.profesionalId) {
        this.toas.error('Seleccione un especialista');
      }
      if (!this.fechaCita) {
        this.toas.error('Seleccione una fecha');
      }
    }
  }
  reagendarCita() {
    console.log(this.selectedCita);
    if (
      this.selectedCita.area.idArea &&
      this.selectedCita.horaInicio &&
      this.selectedCita.fecha &&
      this.selectedCita.idCita
    ) {
      this.selectedCita.fecha = this.datePipe.transform(
        this.selectedCita.fecha,
        'dd-MM-yyyy'
      );
      this.selectedCita.hora = this.selectedCita.horaInicio;
      this.selectedCita.areaId = this.selectedCita.area.idArea;
      this.citaService
        .reagendarCita(this.selectedCita.idCita, this.selectedCita)
        .subscribe(
          (data) => {
            this.toastr.success('Cita reagendada con éxito.');
            this.displayModal = false;
            this.loadCitas();
          },
          (error) => {
            const errorMessage =
              error.error?.message || 'Ocurrió un error al reagendar la cita.';
            this.toastr.error('Error: ' + errorMessage);
          }
        );
    } else {
      this.toastr.error('Por favor, seleccione una cita.');
    }
  }

  onRowsChange(event: any) {
    this.rows = event.value;
    this.loadCitasLazy({
      first: 0,
      rows: this.rows,
      page: this.currentPage,
    });
  }
}
