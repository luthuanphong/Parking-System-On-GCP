// Request DTOs
export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  password: string;
  licensePlate: string;
}

export interface DepositRequest {
  email: string;
  amountCents: number;
}

export interface BookParkingSpotRequest {
  spotId: number;
}
