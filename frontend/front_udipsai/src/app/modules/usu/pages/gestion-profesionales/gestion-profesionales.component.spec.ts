import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionProfesionalesComponent } from './gestion-profesionales.component';

describe('GestionProfesionalesComponent', () => {
  let component: GestionProfesionalesComponent;
  let fixture: ComponentFixture<GestionProfesionalesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestionProfesionalesComponent]
    });
    fixture = TestBed.createComponent(GestionProfesionalesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
