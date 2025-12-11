import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthModule } from './modules/auth/auth.module';
import { SidebarComponent } from './principal/components/sidebar/sidebar.component';
import { HeaderBarComponent } from './principal/components/header-bar/header-bar.component';
import { PrincipalPageComponent } from './principal/pages/principal-page/principal-page.component';
import { HomePageComponent } from './principal/pages/home-page/home-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgxSpinnerModule } from 'ngx-spinner';
import { ToastrModule } from 'ngx-toastr';
import { ShowForRolesDirective } from './core/directives/show-for-roles.directive';
import { JwtInterceptorInterceptor } from './core/interceptors/jwt-interceptor.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    HeaderBarComponent,
    PrincipalPageComponent,
    HomePageComponent,
    ShowForRolesDirective,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AuthModule,
    BrowserAnimationsModule, // Asegúrate de que BrowserAnimationsModule esté antes de NgxSpinnerModule y ToastrModule
    HttpClientModule,
    NgxSpinnerModule,
    ToastrModule.forRoot({
      timeOut: 3000, // Duración de las notificaciones en milisegundos
      positionClass: 'toast-top-right', // Posición de las notificaciones
      preventDuplicates: true, // Prevenir duplicados
    }),
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptorInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
