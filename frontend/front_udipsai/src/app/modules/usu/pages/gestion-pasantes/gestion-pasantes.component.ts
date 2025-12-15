import { Component, OnInit } from '@angular/core';
import { UsuarioService } from 'src/app/core/services/usuario.service';
import { Pasante } from '../../interfaces/Pasante';
import { AreaService } from 'src/app/core/services/area.service';
import { AreaDTO } from 'src/app/core/models/AreaDTO';
import { ToastrService } from 'ngx-toastr';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-gestion-pasantes',
  templateUrl: './gestion-pasantes.component.html',
  styleUrls: ['./gestion-pasantes.component.scss'],
})
export class GestionPasantesComponent implements OnInit {
  pasantes: Pasante[] = [];
  filteredPasantes: Pasante[] = [];
  areas: AreaDTO[] = [];
  searchTerm: string = '';
  displayDetalles: boolean = false;
  displayAgregar: boolean = false;
  selectedPasante: Pasante | null = null;
  editandoPasante: boolean = false;
  pacienteEditando: Pasante = {
    idUsuario: 0,
    cedula: '',
    estado: '',
    nombres: '',
    apellidos: '',
    email: '',
    celular: '',
    roles: [],
    areas: [],
    idPasante: 0,
    pasEstado: 'A',
    carrera: '',
  };

  nuevoPasante: Pasante = {
    idUsuario: 0,
    cedula: '',
    estado: '',
    nombres: '',
    apellidos: '',
    email: '',
    celular: '',
    roles: [],
    areas: [],
    idPasante: 0,
    pasEstado: 'A',
    carrera: '',
  };

  // Propiedades de paginación
  // Propiedades de paginación
  totalRecords: number = 0;
  rows: number = 10; // Número de filas por página
  rowsOptions = [5, 10, 15, 20]; // Opciones para el dropdown de número de filas
  currentPage: number = 0; // Página actual

  constructor(
    private usuarioService: UsuarioService,
    private areaService: AreaService,
    private toastService: ToastrService,
    private confirmationService: ConfirmationService
  ) {
    usuarioService.obtenerPasantes().subscribe((data) => {
      this.pasantes = data.content;
      console.log(this.pasantes);
      this.filterPasantes();
    });
    areaService.obtenerAreas().subscribe((data) => {
      this.areas = data;
    });
  }

  ngOnInit() {
    this.filteredPasantes = this.pasantes;
  }

  loadPasantes(event?: any) {
    if (this.searchTerm != '') {
      this.usuarioService
        .obtenerPasantesPorFiltro(this.searchTerm, 0, 5)
        .subscribe((data) => {
          this.pasantes = data.content;
          this.totalRecords = data.totalElements;
        });
    } else {
      if (event) {
        this.currentPage = event.first / event.rows;
        this.rows = event.rows;
      }

      this.usuarioService
        .obtenerPasantes(this.currentPage, this.rows)
        .subscribe((data) => {
          this.pasantes = data.content;
          this.totalRecords = data.totalElements;
          this.filterPasantes();
        });
    }
  }

  loadPasantesLazy(event: any) {
    this.loadPasantes(event);
  }

  filterPasantes() {
    if (this.searchTerm) {
      this.filteredPasantes = this.pasantes.filter((pasante) =>
        Object.values(pasante).some((value: any) =>
          value.toString().toLowerCase().includes(this.searchTerm.toLowerCase())
        )
      );
    } else {
      this.filteredPasantes = this.pasantes;
    }
  }

  mostrarDialogoAgregar() {
    this.nuevoPasante = {
      idUsuario: 0,
      cedula: '',
      estado: '',
      nombres: '',
      apellidos: '',
      email: '',
      celular: '',
      roles: [],
      areas: [],
      idPasante: 0,
      pasEstado: 'A',
      carrera: '',
    }; // Reset the form
    this.displayAgregar = true;
  }

  guardarPasante() {
    if (this.nuevoPasante) {
      const newPasante: Pasante = {
        ...this.nuevoPasante,
      };
      this.pasantes.push(newPasante);
      this.filterPasantes();
      this.displayAgregar = false;
    }
  }

  submitted: boolean = false;

  agregarPasante() {
    this.submitted = true;
    if (this.validarFormulario()) {
      this.nuevoPasante.contrasenia = this.nuevoPasante.cedula;

      this.usuarioService
        .registrarPasante(this.nuevoPasante)
        .subscribe((data) => {
          this.toastService.success('Pasante agregado exitosamente');
          this.loadPasantes();
          this.displayAgregar = false;
          this.submitted = false;
        }, (error) => {
          this.toastService.error('Error al agregar pasante');
        });
    } else {
      this.toastService.error('Por favor complete todos los campos requeridos correctamente.');
    }
  }

  editarPasante() {
    this.submitted = true;
    if (this.validarFormularioEdicion()) {
      this.usuarioService
        .actualizarPasante(this.pacienteEditando.cedula, this.pacienteEditando)
        .subscribe((data) => {
          this.toastService.success('Pasante actualizado exitosamente');
          this.loadPasantes();
          this.editandoPasante = false;
          this.submitted = false;
        }, (error) => {
          this.toastService.error('Error al actualizar pasante');
        });
    } else {
      this.toastService.error('Por favor complete todos los campos requeridos correctamente.');
    }
  }

  validarFormulario(): boolean {
    return (
      this.nuevoPasante.cedula.trim() !== '' &&
      this.nuevoPasante.cedula.length === 10 &&
      this.nuevoPasante.nombres.trim() !== '' &&
      this.nuevoPasante.apellidos.trim() !== '' &&
      this.nuevoPasante.carrera.trim() !== '' &&
      this.nuevoPasante.areas && this.nuevoPasante.areas.length >= 1 &&
      this.correoValido(this.nuevoPasante.email) &&
      this.telefonoValido(this.nuevoPasante.celular)
    );
  }

  validarFormularioEdicion(): boolean {
    return (
      this.pacienteEditando.nombres.trim() !== '' &&
      this.pacienteEditando.apellidos.trim() !== '' &&
      this.pacienteEditando.carrera.trim() !== '' &&
      this.pacienteEditando.areas && this.pacienteEditando.areas.length >= 1 &&
      this.correoValido(this.pacienteEditando.email) &&
      this.telefonoValido(this.pacienteEditando.celular)
    );
  }

  correoValido(correo: string): boolean {
    const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return regex.test(correo);
  }

  telefonoValido(telefono: string): boolean {
    return !!telefono && telefono.startsWith('09') && telefono.length === 10;
  }

  validarTexto(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\d/g, '');
  }

  validarNumero(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
  }

  iniciarEdicion(pasante: Pasante) {
    this.editandoPasante = true;
    this.pacienteEditando = pasante;
    console.log(pasante);
  }

  eliminarPasante(pasante: Pasante) {
    this.confirmationService.confirm({
      message: '¿Está seguro de que desea eliminar este pasante?',
      header: 'Confirmación de eliminación',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.usuarioService
          .eliminarPasante(pasante.idPasante)
          .subscribe((data) => {
            this.toastService.success('Pasante eliminado exitosamente');
            this.loadPasantes();
          });
      },
    });
  }

  bloquearPasante(pasante: Pasante) {
    this.usuarioService.bloquearPasante(pasante.idPasante).subscribe(
      // console.log(data);
      (data: any) => {
        pasante.pasEstado = 'B';
        this.loadPasantes();
        this.toastService.success(
          `Pasante ${pasante.nombres.toUpperCase()} ${pasante.apellidos.toUpperCase()} bloqueado correctamente`
        );
      },
      (error: any) => {
        this.toastService.error('Error al bloquear el pasante');
      }
    );
  }

  activarPasante(pasante: Pasante) {
    this.usuarioService.activarPasante(pasante.cedula).subscribe(
      // console.log(data);
      (data: any) => {
        pasante.pasEstado = 'A';
        this.loadPasantes();
        this.toastService.success(
          `Pasante ${pasante.nombres.toUpperCase()} ${pasante.apellidos.toUpperCase()} desbloqueado correctamente`
        );
      },
      (error: any) => {
        this.toastService.error('Error al desbloquear el pasante');
      }
    );
  }

  verDetalles(pasante: Pasante) {
    this.selectedPasante = pasante;
    this.displayDetalles = true;
  }

  onRowsChange(event: any) {
    this.rows = event.value;
    this.loadPasantesLazy({
      first: 0,
      rows: this.rows,
      page: this.currentPage,
    });
  }
}
