import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookParkingSpotRequest } from '../models/requests.model';
import { ParkingSpotResponse, ReservationResponse } from '../models/responses.model';

@Injectable({
  providedIn: 'root'
})
export class ParkingService {
  private apiUrl = '/api/parking';

  constructor(private http: HttpClient) {}

  /**
   * Get all parking spots
   */
  getAllParkingSpots(): Observable<ParkingSpotResponse[]> {
    return this.http.get<ParkingSpotResponse[]>(`${this.apiUrl}/spots`);
  }

  /**
   * Get all current reservations
   */
  getCurrentReservations(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(`${this.apiUrl}/reservations`);
  }

  /**
   * Book a parking spot
   */
  bookParkingSpot(request: BookParkingSpotRequest): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(`${this.apiUrl}/reservations`, request);
  }
}
