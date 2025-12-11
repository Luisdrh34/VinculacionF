import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CitasAgendadasComponent } from './citas-agendadas.component';

describe('CitasAgendadasComponent', () => {
  let component: CitasAgendadasComponent;
  let fixture: ComponentFixture<CitasAgendadasComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CitasAgendadasComponent]
    });
    fixture = TestBed.createComponent(CitasAgendadasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
