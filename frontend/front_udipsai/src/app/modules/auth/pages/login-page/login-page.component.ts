import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { AuthService } from 'src/app/core/services/auth.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {
  formLogin: FormGroup;

  constructor(
    private fb: FormBuilder,
    private loadingSpinner: NgxSpinnerService,
    private authService: AuthService,
    private router: Router,
    private toast: ToastrService
  ) {
    this.formLogin = this.fb.group({
      cedula: ['', [Validators.required, Validators.maxLength(10), Validators.minLength(10)]],
      contrasenia: ['', [Validators.required]],
    });
  }

  ngOnInit(): void { }

  async ingresar() {

    await this.loadingSpinner.show();
    if (this.formLogin.valid) {
      this.authService
        .login(this.formLogin.value.cedula, this.formLogin.value.contrasenia)
        .then(async (data: any) => {
          await this.loadingSpinner.hide();
          if (data.resCode === 0) {
            console.log("Login exitoso");
            this.router.navigateByUrl('principal/home');
          } else {
            console.log(data.error);
            this.toast.error(data.error);
            this.router.navigate(['/']);
          }
        })
        .catch((err) => {
          this.loadingSpinner.hide();
        });
    } else {
      this.loadingSpinner.hide();
      this.toast.warning(
        'Por favor rellene los campos adecuadamente'
      );
    }
  }

  onInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const value = inputElement.value;

    // Remove any non-numeric characters
    const sanitizedValue = value.replace(/[^0-9]/g, '');

    // Update the value in the form control
    this.formLogin
      .get('cedula')
      ?.setValue(sanitizedValue, { emitEvent: false });
  }
}
