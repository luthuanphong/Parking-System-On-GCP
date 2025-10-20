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
      // Extract and format user-friendly error message
      let errorMessage = 'Something went wrong. Please try again.';
      
      if (error.error && typeof error.error === 'object') {
        const errorResponse = error.error as ErrorResponse;
        if (errorResponse.message) {
          // Make backend error messages more user-friendly
          errorMessage = makeUserFriendly(errorResponse.message);
        }
      } else if (error.message) {
        errorMessage = makeUserFriendly(error.message);
      }

      // Handle specific error cases with user-friendly messages
      if (error.status === 401) {
        // Unauthorized - redirect to login
        toastService.showError('Your session has expired. Please log in again.');
        userService.logout();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        toastService.showError('You don\'t have permission to perform this action.');
      } else if (error.status === 404) {
        toastService.showError('The requested resource was not found.');
      } else if (error.status === 400) {
        toastService.showError(errorMessage.includes('validation') ? 
          'Please check your input and try again.' : errorMessage);
      } else if (error.status === 409) {
        toastService.showWarning(errorMessage.includes('already') ? 
          'This action has already been completed.' : errorMessage);
      } else if (error.status === 429) {
        toastService.showWarning('Too many requests. Please wait a moment and try again.');
      } else if (error.status >= 500) {
        toastService.showError('Server is temporarily unavailable. Please try again later.');
      } else if (error.status === 0) {
        toastService.showError('Unable to connect. Please check your internet connection.');
      } else {
        toastService.showError(errorMessage);
      }

      return throwError(() => error);
    })
  );

  // Helper function to make error messages more user-friendly
  function makeUserFriendly(message: string): string {
    // Common backend error patterns and their user-friendly versions
    const patterns: { [key: string]: string } = {
      'Insufficient balance': 'You don\'t have enough balance for this booking.',
      'already reserved': 'This parking spot is already taken.',
      'spot not found': 'The selected parking spot is no longer available.',
      'user not found': 'Account not found. Please check your login details.',
      'Invalid credentials': 'Incorrect username or password.',
      'validation failed': 'Please check your input and try again.',
      'Internal server error': 'Something went wrong on our end. Please try again.',
      'Bad request': 'There was an issue with your request. Please try again.',
      'Forbidden': 'You don\'t have permission to perform this action.',
      'Unauthorized': 'Please log in to continue.',
      'Connection refused': 'Unable to connect to the server.',
      'timeout': 'The request took too long. Please try again.'
    };

    // Check for patterns and return user-friendly message
    for (const [pattern, friendlyMessage] of Object.entries(patterns)) {
      if (message.toLowerCase().includes(pattern.toLowerCase())) {
        return friendlyMessage;
      }
    }

    // If no pattern matches, return the original message but capitalize first letter
    return message.charAt(0).toUpperCase() + message.slice(1);
  }
};
