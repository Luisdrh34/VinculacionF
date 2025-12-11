import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CitasAgendadasUsuarioComponent } from './citas-agendadas-usuario.component';

describe('CitasAgendadasUsuarioComponent', () => {
  let component: CitasAgendadasUsuarioComponent;
  let fixture: ComponentFixture<CitasAgendadasUsuarioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CitasAgendadasUsuarioComponent]
    });
    fixture = TestBed.createComponent(CitasAgendadasUsuarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
