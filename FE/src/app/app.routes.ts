import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login.component').then(c => c.LoginComponent)
  },
  {
    path: 'booking',
    loadComponent: () => import('./booking/booking.component').then(c => c.BookingComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '/login' }
];
