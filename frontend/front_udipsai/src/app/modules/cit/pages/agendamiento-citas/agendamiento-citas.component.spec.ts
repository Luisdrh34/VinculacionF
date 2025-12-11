import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AgendamientoCitasComponent } from './agendamiento-citas.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ButtonModule } from 'primeng/button';

describe('AgendamientoCitasComponent', () => {
  let component: AgendamientoCitasComponent;
  let fixture: ComponentFixture<AgendamientoCitasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AgendamientoCitasComponent ],
      imports: [
        FormsModule,
        TableModule,
        CalendarModule,
        DropdownModule,
        DialogModule,
        BrowserAnimationsModule,
        ButtonModule
      ],
      schemas: [ NO_ERRORS_SCHEMA ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AgendamientoCitasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the patients list', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('p-table')).toBeTruthy();
  });

  it('should filter patients by numeroFicha', () => {
    component.filterPacientes({ target: { value: '1234' } } as any);
    expect(component.pacientes.length).toBe(1);
    expect(component.pacientes[0].numeroFicha).toBe('1234');
  });

  it('should open modal for scheduling appointment', () => {
    const paciente = component.pacientes[0];
    component.agendarCita(paciente);
    expect(component.selectedPaciente).toBe(paciente);
    expect(component.displayModal).toBeTrue();
  });

  it('should open modal for viewing appointment history', () => {
    const paciente = component.pacientes[0];
    component.mostrarHistorialCitas(paciente);
    expect(component.selectedPaciente).toBe(paciente);
    expect(component.displayHistorial).toBeTrue();
    expect(component.historialCitas.length).toBeGreaterThan(0);
  });

  it('should save a new appointment', () => {
    const paciente = component.pacientes[0];
    component.agendarCita(paciente);
    component.fechaCita = new Date();
    component.horaCita = '10:00';
    component.especialidadCita = 'Clínica';
    component.especialistaCita = 'Dr. Pérez';
    component.guardarCita();
    expect(component.citas.length).toBe(1);
    expect(component.displayModal).toBeFalse();
  });
});
