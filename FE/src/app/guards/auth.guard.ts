import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../services/user.service';

export const authGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserService);
  const router = inject(Router);

  if (userService.isAuthenticated()) {
    return true;
  }

  // Not logged in, redirect to login page with the return url
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};
