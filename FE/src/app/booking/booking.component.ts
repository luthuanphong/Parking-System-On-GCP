import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { ParkingService } from '../services/parking.service';
import { UserService } from '../services/user.service';
import { ParkingSpotResponse, ReservationResponse } from '../models/responses.model';
import { BookParkingSpotRequest } from '../models/requests.model';

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

  constructor(
    private parkingService: ParkingService,
    private userService: UserService
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
        this.errorMessage = 'Failed to load parking spots';
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
        // Refresh the data
        this.loadParkingSpots();
        this.loadCurrentReservations();
      },
      error: (error) => {
        this.errorMessage = 'Failed to book parking spot';
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
    // Handle menu actions here
  }
}