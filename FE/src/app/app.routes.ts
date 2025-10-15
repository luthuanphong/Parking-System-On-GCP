import { Routes } from '@angular/router';

export const routes: Routes = [
{ path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  // Add a catch-all route for 404
  { path: '**', redirectTo: '/auth/login' }
];
