import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AgendamientoCitasComponent } from './pages/agendamiento-citas/agendamiento-citas.component';
import { CitasAgendadasComponent } from './pages/citas-agendadas/citas-agendadas.component';
import { authGuard } from 'src/app/core/guards/auth.guard';
import { hasRolesGuard } from 'src/app/core/guards/has-roles.guard';
import { CitasAgendadasUsuarioComponent } from './pages/citas-agendadas-usuario/citas-agendadas-usuario.component';
import { CitasAgendadasEspecialidadComponent } from './pages/citas-agendadas-especialidad/citas-agendadas-especialidad.component';

const routes: Routes = [
  {
    path: 'agendamiento-citas',
    component: AgendamientoCitasComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['ADMIN', 'COORDINADOR', 'SECRETARIA'],
    },
  },
  {
    path: 'citas-agendadas',
    component: CitasAgendadasComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['ADMIN', 'COORDINADOR', 'SECRETARIA'],
    },
  },

  {
    path: 'citas-agendadas-usuario',
    component: CitasAgendadasUsuarioComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['ADMIN', 'PROFESIONAL', 'PASANTE'],
    },
  },
  {
    path: 'citas-agendadas-especialidad',
    component: CitasAgendadasEspecialidadComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['PASANTE'],
    },
  },
  {
    path: '**',
    redirectTo: 'agendamiento-citas',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CitRoutingModule {}
