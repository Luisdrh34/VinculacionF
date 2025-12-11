import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionCordinadoresComponent } from './gestion-cordinadores.component';

describe('GestionCordinadoresComponent', () => {
  let component: GestionCordinadoresComponent;
  let fixture: ComponentFixture<GestionCordinadoresComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestionCordinadoresComponent]
    });
    fixture = TestBed.createComponent(GestionCordinadoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
