import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { UserService } from '../services/user.service';
import { ToastService } from '../services/toast.service';
import { ErrorResponse } from '../models/responses.model';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const userService = inject(UserService);
  const router = inject(Router);
  const toastService = inject(ToastService);

  // Get the auth token from the service
  const token = userService.getToken();

  // Clone the request and add the authorization header if token exists
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  // Handle the request and catch any errors
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Extract error message from backend ErrorResponse
      let errorMessage = 'An unexpected error occurred';
      
      if (error.error && typeof error.error === 'object') {
        const errorResponse = error.error as ErrorResponse;
        errorMessage = errorResponse.message || errorMessage;
      } else if (error.message) {
        errorMessage = error.message;
      }

      // Handle specific error cases
      if (error.status === 401) {
        // Unauthorized - redirect to login
        toastService.showError('Session expired. Please login again.');
        userService.logout();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        toastService.showError('Access denied: ' + errorMessage);
      } else if (error.status === 404) {
        toastService.showError('Resource not found: ' + errorMessage);
      } else if (error.status === 400) {
        toastService.showError('Invalid request: ' + errorMessage);
      } else if (error.status === 409) {
        toastService.showWarning('Conflict: ' + errorMessage);
      } else if (error.status >= 500) {
        toastService.showError('Server error: ' + errorMessage);
      } else if (error.status === 0) {
        toastService.showError('Network error. Please check your connection.');
      } else {
        toastService.showError(errorMessage);
      }

      return throwError(() => error);
    })
  );
};
