import { LoginComponent } from "./login/login.component";
import { Routes } from '@angular/router';

export const authRoutes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  }
];