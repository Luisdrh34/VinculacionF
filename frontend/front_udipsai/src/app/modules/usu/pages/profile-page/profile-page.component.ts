import { Component } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from 'src/app/core/services/auth.service';
import { UsuarioService } from 'src/app/core/services/usuario.service';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss'],
})
export class ProfilePageComponent {
  isEditMode = false;
  isEditPersonalInfoMode = false;
  showAppointments = false;
  profileImage = 'https://via.placeholder.com/150';

  personalInfo: any = {};
  contrasenia: string = '';
  nuevaContrasenia: string = '';

  pacientes = [
    { nombre: 'Paciente 1', fecha: 'Hace 1 día' },
    { nombre: 'Paciente 2', fecha: 'Hace 2 días' },
    { nombre: 'Paciente 3', fecha: 'Hace 3 días' },
  ];
  constructor(
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private toastr: ToastrService
  ) {
    this.personalInfo = authService.getUsuarioActual();
  }
  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
  }

  toggleEditPersonalInfo() {
    this.isEditPersonalInfoMode = !this.isEditPersonalInfoMode;
  }

  toggleAppointments() {
    this.showAppointments = !this.showAppointments;
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profileImage = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  saveChanges() {
    this.isEditMode = false;
    // Save changes logic here (e.g., update the server with new profile image)
  }

  savePersonalInfoChanges() {
    console.log(this.personalInfo);
    if (!this.validarInformacionPersonal()) {
      this.toastr.error('Por favor revise la información ingresada (Email o Celular inválidos)');
      return;
    }

    this.usuarioService
      .editarUsuario(this.personalInfo.idUsuario, this.personalInfo)
      .subscribe(
        (response) => {
          this.toastr.success('Cambios guardados correctamente');
          this.authService.guardarUsuarioActual(response);
          this.isEditPersonalInfoMode = false;
          this.personalInfo = this.authService.getUsuarioActual();
        },
        (error) => {
          this.toastr.error('Error al guardar los cambios');
          this.isEditPersonalInfoMode = false;
          this.personalInfo = this.authService.getUsuarioActual();
        }
      );
  }

  changePassword() {
    if (!this.contrasenia || !this.nuevaContrasenia) {
      this.toastr.error('Por favor complete los campos de las contraseñas');
      return;
    }

    if (this.nuevaContrasenia.length < 5) {
      this.toastr.error('La nueva contraseña debe tener al menos 5 caracteres');
      return;
    }

    this.usuarioService
      .cambiarContrasenia(this.personalInfo.cedula, {
        contrasenia: this.contrasenia,
        nuevaContrasenia: this.nuevaContrasenia,
      })
      .subscribe(
        (response) => {
          console.log(response);
          this.toastr.success('Contraseña cambiada correctamente');
          this.resetPasswordFields();
        },
        (error) => {
          if (error.error && error.error.message) {
            this.toastr.error(error.error.message);
          } else {
            this.toastr.error('Error al cambiar la contraseña, compruebe que la contraseña actual sea correcta');
          }
          this.resetPasswordFields();
        }
      );
  }

  validarInformacionPersonal(): boolean {
    return (
      this.correoValido(this.personalInfo.email) &&
      this.telefonoValido(this.personalInfo.celular)
    );
  }

  correoValido(correo: string): boolean {
    if (!correo || correo.trim() === '') return false;
    const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return regex.test(correo);
  }

  telefonoValido(telefono: string): boolean {
    if (!telefono) return false;
    return telefono.startsWith('09') && telefono.length === 10 && !isNaN(Number(telefono));
  }

  resetPasswordFields() {
    this.contrasenia = '';
    this.nuevaContrasenia = '';
  }
}
