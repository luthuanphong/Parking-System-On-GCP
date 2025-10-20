import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog } from '@angular/material/dialog';
import { ParkingService } from '../services/parking.service';
import { UserService } from '../services/user.service';
import { ToastService } from '../services/toast.service';
import { ParkingSpotResponse, ReservationResponse } from '../models/responses.model';
import { BookParkingSpotRequest, DepositRequest } from '../models/requests.model';
import { ReservationStatus } from '../models/enums.model';
import { DepositDialogComponent, DepositDialogData, DepositDialogResult } from '../components/deposit-dialog/deposit-dialog.component';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ]
})
export class BookingComponent implements OnInit {
  parkingSpots: ParkingSpotResponse[] = [];
  currentReservations: ReservationResponse[] = [];
  loading = false;
  errorMessage = '';
  isUserMenuOpen = false;
  
  // Expose enum to template
  ReservationStatus = ReservationStatus;

  constructor(
    private parkingService: ParkingService,
    private userService: UserService,
    private toastService: ToastService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadParkingSpots();
    this.loadCurrentReservations();
  }

  loadParkingSpots(): void {
    this.loading = true;
    this.parkingService.getAllParkingSpots().subscribe({
      next: (spots) => {
        this.parkingSpots = spots;
        this.loading = false;
      },
      error: (error) => {
        // Error handling is now done by the interceptor
        this.loading = false;
        console.error('Error loading parking spots:', error);
      }
    });
  }

  loadCurrentReservations(): void {
    this.parkingService.getCurrentReservations().subscribe({
      next: (reservations) => {
        this.currentReservations = reservations;
      },
      error: (error) => {
        console.error('Error loading reservations:', error);
      }
    });
  }

  bookSpot(spotId: number): void {
    const request: BookParkingSpotRequest = { spotId };
    
    this.parkingService.bookParkingSpot(request).subscribe({
      next: (reservation) => {
        console.log('Spot booked successfully:', reservation);
        this.toastService.showSuccess(`Parking spot #${spotId} booked successfully!`);
        // Refresh the data
        this.loadParkingSpots();
        this.loadCurrentReservations();
      },
      error: (error) => {
        // Error handling is now done by the interceptor
        console.error('Error booking spot:', error);
      }
    });
  }

  toggleUserMenu(): void {
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }

  closeUserMenu(): void {
    this.isUserMenuOpen = false;
  }

  onUserMenuAction(action: string): void {
    console.log('User menu action:', action);
    this.closeUserMenu();
    
    if (action === 'deposit') {
      this.openDepositDialog();
    }
  }

  openDepositDialog(): void {
    // Get current user balance (assuming it's available from UserService)
    const currentBalance = this.userService.getCurrentUser()?.balanceCents || 0;
    
    const dialogData: DepositDialogData = {
      currentBalance: currentBalance
    };

    const dialogRef = this.dialog.open(DepositDialogComponent, {
      width: '450px',
      data: dialogData,
      disableClose: false
    });

    dialogRef.afterClosed().subscribe((result: DepositDialogResult | undefined) => {
      if (result) {
        this.processDeposit(result.amount);
      }
    });
  }

  processDeposit(amountInCents: number): void {
    const currentUser = this.userService.getCurrentUser();
    if (!currentUser) {
      this.toastService.showError('User not found. Please log in again.');
      return;
    }

    const depositRequest: DepositRequest = {
      email: currentUser.username,
      amountCents: amountInCents
    };

    this.userService.deposit(depositRequest).subscribe({
      next: (response) => {
        this.toastService.showSuccess(
          `Successfully deposited $${(amountInCents / 100).toFixed(2)}! New balance: $${(response.balanceCents / 100).toFixed(2)}`
        );
        // Update current user balance
        if (this.userService.getCurrentUser()) {
          this.userService.getCurrentUser()!.balanceCents = response.balanceCents;
        }
      },
      error: (error) => {
        // Error handling is done by the interceptor
        console.error('Deposit failed:', error);
      }
    });
  }
}