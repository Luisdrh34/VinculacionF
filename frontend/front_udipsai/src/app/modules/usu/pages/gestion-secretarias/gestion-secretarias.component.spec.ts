import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionSecretariasComponent } from './gestion-secretarias.component';

describe('GestionSecretariasComponent', () => {
  let component: GestionSecretariasComponent;
  let fixture: ComponentFixture<GestionSecretariasComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestionSecretariasComponent]
    });
    fixture = TestBed.createComponent(GestionSecretariasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
