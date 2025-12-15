import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ConfirmationService } from 'primeng/api';
import { AreaDTO } from 'src/app/core/models/AreaDTO';
import { Coordinador } from 'src/app/core/models/Cordinador';
import { AreaService } from 'src/app/core/services/area.service';
import { UsuarioService } from 'src/app/core/services/usuario.service';

@Component({
  selector: 'app-gestion-cordinadores',
  templateUrl: './gestion-cordinadores.component.html',
  styleUrls: ['./gestion-cordinadores.component.scss'],
})
export class GestionCordinadoresComponent implements OnInit {
  coordinadores: Coordinador[] = [];
  filteredCoordinadores: Coordinador[] = [];
  searchTerm: string = '';
  displayDetalles: boolean = false;
  selectedCoordinador: Coordinador | null = null;
  displayEditModal: boolean = false;
  editCoordinador: Coordinador | any = {};
  displayAgregarModal: boolean = false;
  formSubmitted: boolean = false;
  enableBloquear: boolean = true;
  areas: AreaDTO[] = [];
  //paginado
  totalRecords: number = 0;
  rows: number = 10; // Número de filas por página
  rowsOptions = [5, 10, 15, 20]; // Opciones para el dropdown de número de filas
  currentPage: number = 0; // Página actual
  nuevoCoordinador: Coordinador = {
    idUsuario: 0,
    cedula: '',
    estado: '',
    nombres: '',
    apellidos: '',
    email: '',
    celular: '',
    roles: [],
    areas: [],
    idCoordinador: 0,
    coorEstado: 'A',
    contrasenia: '',
  };

  constructor(
    private usuarioService: UsuarioService,
    private areaService: AreaService,
    private toastService: ToastrService,
    private confirmService: ConfirmationService
  ) { }

  ngOnInit() {
    this.loadCoordinadores();
    this.loadAreas();
  }

  loadCoordinadores(event?: any) {
    if (this.searchTerm != '') {
      this.usuarioService
        .obtenerCoordinadoresPorFiltro(this.searchTerm, 0, 5)
        .subscribe((data) => {
          this.coordinadores = data.content;
          this.totalRecords = data.totalElements;
        });
    } else {
      if (event) {
        this.currentPage = event.first / event.rows;
        this.rows = event.rows;
      }

      this.usuarioService
        .obtenerCoordinadores(this.currentPage, this.rows)
        .subscribe((data) => {
          this.coordinadores = data.content;
          this.totalRecords = data.totalElements;
        });
    }
  }

  loadCoordinadoresLazy(event: any) {
    this.loadCoordinadores(event);
  }

  loadAreas() {
    this.areaService.obtenerAreas().subscribe((data) => {
      this.areas = data;
    });
  }

  filterCoordinadores() {
    if (this.searchTerm) {
      this.filteredCoordinadores = this.coordinadores.filter((coordinador) =>
        Object.values(coordinador).some(
          (value: any) =>
            value &&
            value
              .toString()
              .toLowerCase()
              .includes(this.searchTerm.toLowerCase())
        )
      );
    } else {
      this.filteredCoordinadores = this.coordinadores;
    }
  }

  agregarCoordinador() {
    this.displayAgregarModal = true;
  }

  guardarNuevoCoordinador() {
    this.formSubmitted = true;
    if (this.validarFormulario()) {
      this.nuevoCoordinador.contrasenia = this.nuevoCoordinador.cedula;
      this.usuarioService.registrarCoordinador(this.nuevoCoordinador).subscribe(
        (data: any) => {
          this.toastService.success('Coordinador guardado exitosamente');
          this.loadCoordinadores();
          this.displayAgregarModal = false;
          this.resetFormulario();
        },
        (error: any) => {
          this.toastService.error('Error al guardar el coordinador');
        }
      );
    }
  }

  cancelarAgregar() {
    this.displayAgregarModal = false;
    this.resetFormulario();
  }

  editarCoordinador(coordinador: Coordinador) {
    this.editCoordinador = { ...coordinador };
    this.displayEditModal = true;
  }

  guardarCoordinadorEditado() {
    this.formSubmitted = true;
    console.log(this.editCoordinador);
    if (this.validarFormularioEdicion()) {
      this.usuarioService
        .actualizarCoordinador(
          this.editCoordinador.cedula,
          this.editCoordinador
        )
        .subscribe(
          (data: any) => {
            const index = this.coordinadores.findIndex(
              (c) => c.cedula === this.editCoordinador.cedula
            );
            if (index !== -1) {
              this.coordinadores[index] = this.editCoordinador;
              this.filterCoordinadores();
              this.displayEditModal = false;
              this.formSubmitted = false;
            }
          },
          (error: any) => {
            this.toastService.error('Error al actualizar el coordinador');
          }
        );
    }
  }

  activarCoordinador(coordinador: Coordinador) {
    this.usuarioService.activarCoordinador(coordinador.cedula).subscribe(
      (data: any) => {
        coordinador.coorEstado = 'A';
        this.toastService.success('Coordinador activado');
      },
      (error: any) => {
        this.toastService.error('Error al activar el coordinador');
      }
    );
  }

  eliminarCoordinador(coordinador: Coordinador) {
    this.confirmService.confirm({
      message: '¿Está seguro de que desea eliminar el coordinador?',
      header: 'Confirmación de eliminación',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.usuarioService.eliminarCoordinador(coordinador.cedula).subscribe(
          (data: any) => {
            this.toastService.success('Coordinador eliminado exitosamente');
            this.loadCoordinadores();
          },
          (error: any) => {
            this.toastService.error('Error al eliminar el coordinador');
          }
        );
      },
    });
  }

  bloquearCoordinador(coordinador: Coordinador) {
    if (coordinador.coorEstado === 'B') {
      this.usuarioService.activarCoordinador(coordinador.cedula).subscribe(
        (data: any) => {
          coordinador.coorEstado = 'A';
          this.toastService.success(
            `Coordinador ${coordinador.nombres.toUpperCase()} ${coordinador.apellidos.toUpperCase()} desbloqueado correctamente`
          );
        },
        (error: any) => {
          this.toastService.error('Error al desbloquear el coordinador');
        }
      );
    } else {
      this.usuarioService.bloquearCoordinador(coordinador.cedula).subscribe(
        (data: any) => {
          coordinador.coorEstado = 'B';
          this.toastService.success(
            `Coordinador ${coordinador.nombres.toUpperCase()} ${coordinador.apellidos.toUpperCase()} bloqueado correctamente`
          );
        },
        (error: any) => {
          this.toastService.error('Error al bloquear el coordinador');
        }
      );
    }
  }

  verDetalles(coordinador: Coordinador) {
    this.selectedCoordinador = coordinador;
    this.displayDetalles = true;
  }

  validarFormulario(): boolean {
    return (
      this.nuevoCoordinador.nombres?.trim() !== '' &&
      this.nuevoCoordinador.apellidos?.trim() !== '' &&
      this.nuevoCoordinador.areas.length > 0 &&
      this.correoValido(this.nuevoCoordinador.email) &&
      this.telefonoValido(this.nuevoCoordinador.celular)
    );
  }

  validarFormularioEdicion(): boolean {
    return (
      this.editCoordinador.nombres?.trim() !== '' &&
      this.editCoordinador.apellidos?.trim() !== '' &&
      this.editCoordinador.areas.length > 0 &&
      this.correoValido(this.editCoordinador.email) &&
      this.telefonoValido(this.editCoordinador.celular)
    );
  }

  correoValido(correo: string): boolean {
    if (!correo || correo.trim() === '') {
      return false;
    }
    const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return regex.test(correo);
  }

  telefonoValido(telefono: string): boolean {
    if (!telefono) {
      return false;
    }
    return telefono.startsWith('09') && telefono.length === 10;
  }

  textoValido(texto: string): boolean {
    return !/\d/.test(texto);
  }

  cedulaValida(cedula: string): boolean {
    return cedula.length === 10;
  }

  validarTexto(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\d/g, '');
  }

  validarTelefono(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
  }

  validarCedula(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
  }

  resetFormulario() {
    this.nuevoCoordinador = {
      idUsuario: 0,
      cedula: '',
      estado: '',
      nombres: '',
      apellidos: '',
      email: '',
      celular: '',
      roles: [],
      areas: [],
      idCoordinador: 0,
      coorEstado: '',
      contrasenia: '',
    };
    this.formSubmitted = false;
  }
  onRowsChange(event: any) {
    this.rows = event.value;
    this.loadCoordinadoresLazy({
      first: 0,
      rows: this.rows,
      page: this.currentPage,
    });
  }
}
