import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionPasantesComponent } from './gestion-pasantes.component';

describe('GestionPasantesComponent', () => {
  let component: GestionPasantesComponent;
  let fixture: ComponentFixture<GestionPasantesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestionPasantesComponent]
    });
    fixture = TestBed.createComponent(GestionPasantesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
