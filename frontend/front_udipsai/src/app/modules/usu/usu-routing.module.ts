import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfilePageComponent } from './pages/profile-page/profile-page.component';
import { GestionPasantesComponent } from './pages/gestion-pasantes/gestion-pasantes.component';
import { GestionProfesionalesComponent } from './pages/gestion-profesionales/gestion-profesionales.component';
import { GestionCordinadoresComponent } from './pages/gestion-cordinadores/gestion-cordinadores.component';
import { GestionSecretariasComponent } from './pages/gestion-secretarias/gestion-secretarias.component';
import { authGuard } from 'src/app/core/guards/auth.guard';
import { hasRolesGuard } from 'src/app/core/guards/has-roles.guard';

const routes: Routes = [
  {
    path: 'profile',
    component: ProfilePageComponent,
  },
  {
    path: 'gestion-pasantes',
    component: GestionPasantesComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['ADMIN', 'COORDINADOR', 'SECRETARIA', 'PROFESIONAL'],
    },
  },
  {
    path: 'gestion-profesionales',
    component: GestionProfesionalesComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['ADMIN', 'COORDINADOR', 'SECRETARIA'],
    },
  },
  {
    path: 'gestion-cordinadores',
    component: GestionCordinadoresComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['ADMIN', 'COORDINADOR', 'SECRETARIA'],
    },
  },
  {
    path: 'gestion-secretarias',
    component: GestionSecretariasComponent,
    canActivate: [authGuard, hasRolesGuard],
    data: {
      allowedRoles: ['ADMIN', 'COORDINADOR', 'SECRETARIA'],
    },
  },
  {
    path: '**',
    redirectTo: 'gestion-cordinadores',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UsuRoutingModule {}
