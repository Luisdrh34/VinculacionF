import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { ReportesRoutingModule } from './reportes-routing.module';
import { ReportesComponent } from './reportes.component';
import { ReporteCitasPageComponent } from './pages/reporte-citas-page/reporte-citas-page.component';


@NgModule({
  declarations: [
    ReportesComponent,
    ReporteCitasPageComponent
  ],
  imports: [
    CommonModule,
    ReportesRoutingModule,
    FormsModule
  ]
})
export class ReportesModule { }
