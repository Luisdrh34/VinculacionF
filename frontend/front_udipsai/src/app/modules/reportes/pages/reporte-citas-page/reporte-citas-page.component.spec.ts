import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteCitasPageComponent } from './reporte-citas-page.component';

describe('ReporteCitasPageComponent', () => {
  let component: ReporteCitasPageComponent;
  let fixture: ComponentFixture<ReporteCitasPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReporteCitasPageComponent]
    });
    fixture = TestBed.createComponent(ReporteCitasPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
