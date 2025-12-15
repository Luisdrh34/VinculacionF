import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { CitRoutingModule } from './cit-routing.module';
import { AgendamientoCitasComponent } from './pages/agendamiento-citas/agendamiento-citas.component';
import { TableModule } from 'primeng/table';
import { CitasAgendadasComponent } from './pages/citas-agendadas/citas-agendadas.component';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { CitasAgendadasUsuarioComponent } from './pages/citas-agendadas-usuario/citas-agendadas-usuario.component';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CitasAgendadasEspecialidadComponent } from './pages/citas-agendadas-especialidad/citas-agendadas-especialidad.component';
import { ListboxModule } from 'primeng/listbox';

@NgModule({
  declarations: [
    AgendamientoCitasComponent,
    CitasAgendadasComponent,
    CitasAgendadasUsuarioComponent,
    CitasAgendadasEspecialidadComponent,
  ],
  imports: [
    CommonModule,
    CitRoutingModule,
    TableModule,
    ButtonModule,
    ProgressSpinnerModule,
    DialogModule,
    InputTextModule,
    FormsModule,
    CalendarModule,
    DropdownModule,
    ConfirmDialogModule,
    ListboxModule
  ],
  providers: [ConfirmationService, DatePipe],
})
export class CitModule { }
