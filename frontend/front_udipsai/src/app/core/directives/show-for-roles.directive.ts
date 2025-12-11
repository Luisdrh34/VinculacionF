import {
  Directive,
  Input,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { distinctUntilChanged, map, Subscription, switchMap, tap } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Directive({
  selector: '[akoShowForRoles]',
})
export class ShowForRolesDirective implements OnInit, OnDestroy {
  @Input('akoShowForRoles') allowedRoles?: any[];

  constructor(
    private authService: AuthService,
    private viewContainerRef: ViewContainerRef,
    private templateRef: TemplateRef<any>
  ) {}
  ngOnDestroy(): void {
  }
  ngOnInit(): void {
    let rolesUsuario:any[] = this.authService.getUsuarioRoles();
        this.allowedRoles?.some((allowedRole) =>
          rolesUsuario.includes(allowedRole))
          ? this.viewContainerRef.createEmbeddedView(this.templateRef)
          : this.viewContainerRef.clear();


  }
}
