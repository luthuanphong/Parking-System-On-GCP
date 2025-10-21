import { ReservationStatus } from './enums.model';

// Response DTOs
export interface LoginResponse {
  id: number;
  token: string;
  username: string;
  licensePlate: string;
  balanceCents: number;
}

export interface ParkingSpotResponse {
  id: number;
  name: string;
  reserved: boolean;
  reservedLicensePlate?: string; // License plate if reserved
}

export interface ReservationResponse {
  spotId: number;
  spotName: string;
  userName: string;
  userEmail: string;
  reservedForDate: string;
  reservationStatus: ReservationStatus;
}

export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
}
