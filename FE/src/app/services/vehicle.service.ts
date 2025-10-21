import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VehicleResponse } from '../models/responses.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private apiUrl = `${environment.apiUrl}/vehicles`;

  constructor(private http: HttpClient) {}

  /**
   * Get all vehicles
   */
  getAllVehicles(): Observable<VehicleResponse[]> {
    return this.http.get<VehicleResponse[]>(this.apiUrl);
  }
}