import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { hasRolesGuard } from './has-roles.guard';

describe('hasRolesGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => hasRolesGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
