import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReporteCitasPageComponent } from './pages/reporte-citas-page/reporte-citas-page.component';

const routes: Routes = [{ path: 'citas', component: ReporteCitasPageComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportesRoutingModule { }
