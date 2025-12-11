import { Component } from '@angular/core';
import { AreaDTO } from 'src/app/core/models/AreaDTO';
import { AreaService } from 'src/app/core/services/area.service';
import { UsuarioService } from 'src/app/core/services/usuario.service';
import { Profesional } from '../../interfaces/Profesional';
import { ToastrService } from 'ngx-toastr';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-gestion-profesionales',
  templateUrl: './gestion-profesionales.component.html',
  styleUrls: ['./gestion-profesionales.component.scss'],
})
export class GestionProfesionalesComponent {
  doctores: Profesional[] = [];
  filteredDoctores: Profesional[] = [];
  searchTerm: string = '';
  displayDetalles: boolean = false;
  selectedDoctor: Profesional | null = null;
  displayEditModal: boolean = false;
  editDoctor: Profesional = this.inicializarProfesional();
  bloquearBotones: boolean = false;
  displayAgregarModal: boolean = false;
  areas: AreaDTO[] = [];
  // Propiedades de paginación
  totalRecords: number = 0;
  rows: number = 10; // Número de filas por página
  rowsOptions = [5, 10, 15, 20]; // Opciones para el dropdown de número de filas
  currentPage: number = 0; // Página actual

  especialidades: { label: string; value: string }[] = [
    { label: 'Psicología Educativa', value: 'Psicología Educativa' },
    { label: 'Clínica', value: 'Clínica' },
    { label: 'Ponoaudiología', value: 'Ponoaudiología' },
    { label: 'Recuperación Pedagógica', value: 'Recuperación Pedagógica' },
    { label: 'Estimulación Temprana', value: 'Estimulación Temprana' },
    { label: 'Odontologia', value: 'Odontologia' },
    { label: 'Trabajo Social', value: 'Trabajo Social' },
    { label: 'Cardiología', value: 'Cardiología' },
    { label: 'Dermatología', value: 'Dermatología' },
    { label: 'Pediatría', value: 'Pediatría' },
  ];
  nuevoDoctor: Profesional = this.inicializarProfesional();
  formSubmitted: boolean = false;

  constructor(
    private usuarioService: UsuarioService,
    private areaService: AreaService,
    private toastService: ToastrService,
    private confirmationService: ConfirmationService
  ) {
    this.loadProfesionales();
    areaService.obtenerAreas().subscribe((data) => {
      this.areas = data;
    });
  }

  ngOnInit() {
    this.filteredDoctores = this.doctores;
  }

  inicializarProfesional(): Profesional {
    return {
      idUsuario: 0,
      cedula: '',
      profEstado: 'A',
      nombres: '',
      apellidos: '',
      email: '',
      celular: '',
      roles: [],
      areas: [],
      idProfesional: 0,
      especialidad: '',
      contrasenia: '',
    };
  }

  loadProfesionales(event?: any) {
    if (this.searchTerm != '') {
      this.usuarioService
        .obtenerProfesionalesPorFiltro(this.searchTerm, 0, 5)
        .subscribe((data) => {
          this.doctores = data.content;
          this.totalRecords = data.totalElements;
        });
    } else {
      if (event) {
        this.currentPage = event.first / event.rows;
        this.rows = event.rows;
      }

      this.usuarioService
        .obtenerProfesionales(this.currentPage, this.rows)
        .subscribe((data) => {
          this.doctores = data.content;
          this.totalRecords = data.totalElements;
        });
    }
  }

  loadProfesionalesLazy(event: any) {
    this.loadProfesionales(event);
  }

  filterDoctores() {
    if (this.searchTerm) {
      this.filteredDoctores = this.doctores.filter((doctor) =>
        Object.values(doctor).some((value: any) =>
          value.toString().toLowerCase().includes(this.searchTerm.toLowerCase())
        )
      );
    } else {
      this.filteredDoctores = this.doctores;
    }
  }

  agregarDoctor() {
    this.displayAgregarModal = true;
  }
  guardarNuevoDoctor() {
    this.formSubmitted = true;
    if (this.validarFormulario()) {
      // Creating a new professional object by copying properties from nuevoDoctor
      const nuevoProfesional: Profesional = {
        ...this.nuevoDoctor,
        // Joining all areas into a single text string for the especialidad property
        especialidad: this.nuevoDoctor.areas
          .map((area) => area.nombre)
          .join(', '),
        contrasenia: this.nuevoDoctor.cedula,
      };

      this.usuarioService.registrarProfesional(nuevoProfesional).subscribe(
        (data) => {
          this.loadProfesionales();
          this.displayAgregarModal = false;
          this.toastService.success('Profesional agregado exitosamente');
          // Adding the new professional to the doctors list
        },
        (error) => {
          console.error(error);
          this.toastService.error('Error al agregar profesional');
        }
      );
    }
  }

  cancelarAgregar() {
    this.displayAgregarModal = false;
    this.resetFormulario();
  }

  editarDoctor(doctor: Profesional) {
    this.editDoctor = { ...doctor };
    this.displayEditModal = true;
  }

  guardarDoctorEditado() {
    this.formSubmitted = true;

    if (this.validarFormularioEdicion()) {
      this.editDoctor.especialidad = this.editDoctor.areas
        .map((area) => area.nombre)
        .join(', ');
      this.usuarioService
        .actualizarProfesional(this.editDoctor.idProfesional, this.editDoctor)
        .subscribe(
          (data) => {
            this.loadProfesionales();
            this.displayEditModal = false;
            this.formSubmitted = false;
            this.toastService.success('Profesional actualizado exitosamente');
          },
          (error) => {
            console.error(error);
            this.toastService.error('Error al actualizar profesional');
          }
        );
    }
  }

  eliminarDoctor(doctor: Profesional) {
    this.confirmationService.confirm({
      message: '¿Estás seguro de que deseas eliminar este elemento?',
      header: 'Confirmar Eliminación',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.usuarioService.eliminarProfesional(doctor.idProfesional).subscribe(
          (data) => {
            this.toastService.success('Profesional eliminado exitosamente');
            this.loadProfesionales();
          },
          (error) => {
            console.error(error);
          }
        );
      },
      reject: () => {
        // Acción en caso de cancelar la eliminación (opcional)
      },
    });
  }

  activarProfesional(profesional: Profesional) {
    this.usuarioService.activarProfesional(profesional.cedula).subscribe(
      // console.log(data);
      (data: any) => {
        profesional.profEstado = 'A';
        this.loadProfesionales();
        this.toastService.success(
          `Profesional ${profesional.nombres.toUpperCase()} ${profesional.apellidos.toUpperCase()} desbloqueado correctamente`
        );
      },
      (error: any) => {
        this.toastService.error('Error al desbloquear el profesional');
      }
    );
  }

  bloquearProfesional(profesional: Profesional) {
    this.usuarioService.bloquearProfesional(profesional.cedula).subscribe(
      // console.log(data);
      (data: any) => {
        profesional.profEstado = 'B';
        this.loadProfesionales();
        this.toastService.success(
          `Profesional ${profesional.nombres.toUpperCase()} ${profesional.apellidos.toUpperCase()} bloqueado correctamente`
        );
      },
      (error: any) => {
        this.toastService.error('Error al bloquear el profesional');
      }
    );
  }

  verDetalles(doctor: Profesional) {
    console.log(doctor);
    this.selectedDoctor = doctor;
    this.displayDetalles = true;
  }

  validarFormulario(): boolean {
    return (
      this.nuevoDoctor.nombres.trim() !== '' &&
      this.nuevoDoctor.areas.length >= 1 &&
      this.correoValido(this.nuevoDoctor.email) &&
      this.telefonoValido(this.nuevoDoctor.celular)
    );
  }

  validarFormularioEdicion(): boolean {
    return (
      this.editDoctor.nombres.trim() !== '' &&
      this.editDoctor.areas.length >= 1 &&
      this.correoValido(this.editDoctor.email) &&
      this.telefonoValido(this.editDoctor.celular)
    );
  }

  correoValido(correo: string): boolean {
    const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return regex.test(correo);
  }

  telefonoValido(telefono: string): boolean {
    return telefono.startsWith('09') && telefono.length === 10;
  }

  textoValido(texto: string): boolean {
    return !/\d/.test(texto);
  }

  validarTexto(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\d/g, '');
  }

  validarTelefono(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
  }

  resetFormulario() {
    this.nuevoDoctor = this.inicializarProfesional();
    this.formSubmitted = false;
  }

  onRowsChange(event: any) {
    this.rows = event.value;
    this.loadProfesionalesLazy({
      first: 0,
      rows: this.rows,
      page: this.currentPage,
    });
  }
}
