import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SidebarComponent } from './principal/components/sidebar/sidebar.component';
import { PrincipalPageComponent } from './principal/pages/principal-page/principal-page.component';
import { LoginPageComponent } from './modules/auth/pages/login-page/login-page.component';
import { HomePageComponent } from './principal/pages/home-page/home-page.component';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () =>
      import('./modules/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'principal',
    component: PrincipalPageComponent,
    children: [
      {
        path: 'sidebar',
        component: LoginPageComponent,
      },
      {
        path: 'home',
        component: HomePageComponent,
      },
      {
        path: 'cit',
        loadChildren: () => import('./modules/cit/cit.module').then((m) => m.CitModule),
      },
      {
        path: 'usu',
        loadChildren: () => import('./modules/usu/usu.module').then((m) => m.UsuModule),
      },
    ],
  },

  {
    path: '**',
    redirectTo: 'auth',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
