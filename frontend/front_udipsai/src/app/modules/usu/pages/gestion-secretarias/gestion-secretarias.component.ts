import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ConfirmationService } from 'primeng/api';
import { AreaDTO } from 'src/app/core/models/AreaDTO';
import { Secretaria } from 'src/app/core/models/Secretaria';
import { AreaService } from 'src/app/core/services/area.service';
import { UsuarioService } from 'src/app/core/services/usuario.service';

@Component({
  selector: 'app-gestion-secretarias',
  templateUrl: './gestion-secretarias.component.html',
  styleUrls: ['./gestion-secretarias.component.scss'],
})
export class GestionSecretariasComponent implements OnInit {
  secretarias: Secretaria[] = [];
  filteredSecretarias: Secretaria[] = [];
  searchTerm: string = '';
  displayDetalles: boolean = false;
  selectedSecretaria: Secretaria | null = null;
  displayEditModal: boolean = false;
  editSecretaria: Secretaria | any = {};
  displayAgregarModal: boolean = false;
  formSubmitted: boolean = false;
  areas: AreaDTO[] = [];

  nuevoSecretaria: Secretaria = {
    idUsuario: 0,
    cedula: '',
    estado: '',
    nombres: '',
    apellidos: '',
    email: '',
    celular: '',
    roles: [],
    areas: [],
    idSecretaria: 0,
    secEstado: 'A',
  };

  constructor(
    private usuarioService: UsuarioService,
    private areaService: AreaService,
    private toastService: ToastrService,
    private confirmService: ConfirmationService
  ) { }

  ngOnInit() {
    this.loadSecretarias();
    this.loadAreas();
  }

  loadSecretarias() {
    if (this.searchTerm != '') {
      this.usuarioService
        .obtenerSecretariasPorFiltro(this.searchTerm, 0, 5)
        .subscribe((data) => {
          this.secretarias = data.content;
          this.filteredSecretarias = this.secretarias;
        });
    }
    this.usuarioService.obtenerSecretarias().subscribe((data) => {
      this.secretarias = data.content;
      this.filteredSecretarias = this.secretarias;
    });
  }

  loadAreas() {
    this.areaService.obtenerAreas().subscribe((data) => {
      this.areas = data;
    });
  }

  filterSecretarias() {
    if (this.searchTerm) {
      this.filteredSecretarias = this.secretarias.filter((secretaria) =>
        Object.values(secretaria).some((value: any) =>
          value.toString().toLowerCase().includes(this.searchTerm.toLowerCase())
        )
      );
    } else {
      this.filteredSecretarias = this.secretarias;
    }
  }

  agregarSecretaria() {
    this.displayAgregarModal = true;
  }

  guardarNuevoSecretaria() {
    this.formSubmitted = true;
    console.log(this.nuevoSecretaria);
    if (this.validarFormulario()) {
      this.nuevoSecretaria.contrasenia = this.nuevoSecretaria.cedula;
      this.usuarioService.registrarSecretaria(this.nuevoSecretaria).subscribe(
        (data: any) => {
          this.toastService.success('Secretaria guardada correctamente');
          this.loadSecretarias();
          this.displayAgregarModal = false;
          this.resetFormulario();
        },
        (error: any) => {
          this.toastService.error('Error al guardar la secretaria');
        }
      );
    }
  }

  cancelarAgregar() {
    this.displayAgregarModal = false;
    this.resetFormulario();
  }

  editarSecretaria(secretaria: Secretaria) {
    this.editSecretaria = { ...secretaria };
    this.displayEditModal = true;
  }

  guardarSecretariaEditado() {
    this.formSubmitted = true;
    if (this.validarFormularioEdicion()) {
      this.usuarioService
        .actualizarSecretaria(this.editSecretaria.cedula, this.editSecretaria)
        .subscribe(
          (data: any) => {
            this.toastService.success('Secretaria actualizada correctamente');
            this.loadSecretarias();
            this.displayEditModal = false;
            this.formSubmitted = false;
          },
          (error: any) => {
            this.toastService.error('Error al actualizar la secretaria');
          }
        );
    }
  }

  eliminarSecretaria(secretaria: Secretaria) {
    this.confirmService.confirm({
      message: '¿Está seguro de que desea eliminar la secretaria?',
      header: 'Confirmar Eliminación',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.usuarioService.eliminarSecretaria(secretaria.cedula).subscribe(
          (data: any) => {
            this.secretarias = this.secretarias.filter(
              (s) => s.cedula !== secretaria.cedula
            );
            this.filterSecretarias();
          },
          (error: any) => {
            this.toastService.error('Error al eliminar la secretaria');
          }
        );
      },
    });
  }

  activarSecretaria(secretaria: Secretaria) {
    this.usuarioService.activarSecretaria(secretaria.cedula).subscribe(
      // console.log(data);
      (data: any) => {
        secretaria.secEstado = 'A';
        this.loadSecretarias();
        this.toastService.success(
          `Secretaria ${secretaria.nombres.toUpperCase()} ${secretaria.apellidos.toUpperCase()} desbloqueada correctamente`
        );
      },
      (error: any) => {
        this.toastService.error('Error al desbloquear la secretaria');
      }
    );
  }

  bloquearSecretaria(secretaria: Secretaria) {
    this.usuarioService.bloquearSecretaria(secretaria.cedula).subscribe(
      // console.log(data);
      (data: any) => {
        secretaria.secEstado = 'B';
        this.loadSecretarias();
        this.toastService.success(
          `Secretaria ${secretaria.nombres.toUpperCase()} ${secretaria.apellidos.toUpperCase()} bloqueada correctamente`
        );
      },
      (error: any) => {
        this.toastService.error('Error al bloquear la secretaria');
      }
    );
  }

  verDetalles(secretaria: Secretaria) {
    this.selectedSecretaria = secretaria;
    this.displayDetalles = true;
  }

  validarFormulario(): boolean {
    return (
      this.nuevoSecretaria.cedula?.trim() !== '' &&
      this.nuevoSecretaria.apellidos?.trim() !== '' &&
      this.nuevoSecretaria.nombres?.trim() !== '' &&
      this.correoValido(this.nuevoSecretaria.email) &&
      this.telefonoValido(this.nuevoSecretaria.celular)
    );
  }

  validarFormularioEdicion(): boolean {
    return (
      this.editSecretaria.cedula?.trim() !== '' &&
      this.editSecretaria.apellidos?.trim() !== '' &&
      this.editSecretaria.nombres?.trim() !== '' &&
      this.correoValido(this.editSecretaria.email) &&
      this.telefonoValido(this.editSecretaria.celular)
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
    this.nuevoSecretaria = {
      idUsuario: 0,
      cedula: '',
      estado: '',
      nombres: '',
      apellidos: '',
      email: '',
      celular: '',
      roles: [],
      areas: [],
      idSecretaria: 0,
      secEstado: '',
    };
    this.formSubmitted = false;
  }
}
