import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

export interface ToastConfig {
  duration?: number;
  horizontalPosition?: 'start' | 'center' | 'end' | 'left' | 'right';
  verticalPosition?: 'top' | 'bottom';
  panelClass?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  constructor(private snackBar: MatSnackBar) {}

  /**
   * Show success toast message
   */
  showSuccess(message: string, config?: ToastConfig): void {
    this.snackBar.open(message, 'Close', {
      duration: config?.duration || 4000,
      horizontalPosition: config?.horizontalPosition || 'center',
      verticalPosition: config?.verticalPosition || 'top',
      panelClass: ['success-toast', ...(config?.panelClass || [])]
    });
  }

  /**
   * Show error toast message
   */
  showError(message: string, config?: ToastConfig): void {
    this.snackBar.open(message, 'Close', {
      duration: config?.duration || 8000, // Longer duration for errors
      horizontalPosition: config?.horizontalPosition || 'center',
      verticalPosition: config?.verticalPosition || 'top',
      panelClass: ['error-toast', ...(config?.panelClass || [])]
    });
  }

  /**
   * Show critical error that persists until dismissed
   */
  showCriticalError(message: string, config?: ToastConfig): void {
    this.snackBar.open(message, 'Dismiss', {
      duration: 0, // No auto-dismiss for critical errors
      horizontalPosition: config?.horizontalPosition || 'center',
      verticalPosition: config?.verticalPosition || 'top',
      panelClass: ['error-toast', 'critical-error', ...(config?.panelClass || [])]
    });
  }

  /**
   * Show warning toast message
   */
  showWarning(message: string, config?: ToastConfig): void {
    this.snackBar.open(message, 'Close', {
      duration: config?.duration || 5000,
      horizontalPosition: config?.horizontalPosition || 'center',
      verticalPosition: config?.verticalPosition || 'top',
      panelClass: ['warning-toast', ...(config?.panelClass || [])]
    });
  }

  /**
   * Show info toast message
   */
  showInfo(message: string, config?: ToastConfig): void {
    this.snackBar.open(message, 'Close', {
      duration: config?.duration || 4000,
      horizontalPosition: config?.horizontalPosition || 'center',
      verticalPosition: config?.verticalPosition || 'top',
      panelClass: ['info-toast', ...(config?.panelClass || [])]
    });
  }

  /**
   * Dismiss all toasts
   */
  dismiss(): void {
    this.snackBar.dismiss();
  }
}