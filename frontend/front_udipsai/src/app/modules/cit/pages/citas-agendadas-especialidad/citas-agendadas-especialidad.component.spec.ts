import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CitasAgendadasEspecialidadComponent } from './citas-agendadas-especialidad.component';

describe('CitasAgendadasEspecialidadComponent', () => {
  let component: CitasAgendadasEspecialidadComponent;
  let fixture: ComponentFixture<CitasAgendadasEspecialidadComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CitasAgendadasEspecialidadComponent]
    });
    fixture = TestBed.createComponent(CitasAgendadasEspecialidadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
