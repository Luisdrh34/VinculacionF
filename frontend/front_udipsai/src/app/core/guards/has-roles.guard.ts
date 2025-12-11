import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, tap } from 'rxjs';

export const hasRolesGuard: CanActivateFn = (route, state) => {
  const allowedRoles = route.data?.['allowedRoles'];

 let rolesUsuario = inject(AuthService).getUsuarioRoles();

  const hasRequiredRole = allowedRoles.some((elemento: string) =>
    rolesUsuario.includes(elemento)
  );
  console.log(rolesUsuario);
  if (!hasRequiredRole) {
    alert('Acesso Denegado');
  }
  return hasRequiredRole;

};
