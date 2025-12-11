import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsuRoutingModule } from './usu-routing.module';
import { GestionPasantesComponent } from './pages/gestion-pasantes/gestion-pasantes.component';
import { GestionProfesionalesComponent } from './pages/gestion-profesionales/gestion-profesionales.component';
import { GestionCordinadoresComponent } from './pages/gestion-cordinadores/gestion-cordinadores.component';
import { GestionSecretariasComponent } from './pages/gestion-secretarias/gestion-secretarias.component';
import { ProfilePageComponent } from './pages/profile-page/profile-page.component';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { MultiSelectModule } from 'primeng/multiselect';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';

@NgModule({
  declarations: [
    GestionPasantesComponent,
    GestionProfesionalesComponent,
    GestionCordinadoresComponent,
    GestionSecretariasComponent,
    ProfilePageComponent,
  ],
  imports: [
    ConfirmDialogModule,
    CommonModule,
    UsuRoutingModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    FormsModule,
    CalendarModule,
    DropdownModule,
    MultiSelectModule,
  ],
  providers: [ConfirmationService],
})
export class UsuModule {}
