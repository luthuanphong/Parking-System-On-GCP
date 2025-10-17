import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {

  constructor() { }

  get isProduction(): boolean {
    return environment.production;
  }

  get apiUrl(): string {
    return environment.apiUrl;
  }

  get appName(): string {
    return environment.appName;
  }

  get version(): string {
    return environment.version;
  }

  get enableDebugTools(): boolean {
    return environment.enableDebugTools;
  }

  get logLevel(): string {
    return environment.logLevel;
  }

  get features() {
    return environment.features;
  }

  /**
   * Log message based on environment log level
   */
  log(level: 'debug' | 'info' | 'warn' | 'error', message: string, ...args: any[]): void {
    if (this.shouldLog(level)) {
      console[level](message, ...args);
    }
  }

  /**
   * Check if we should log based on current environment log level
   */
  private shouldLog(level: string): boolean {
    const levels = ['debug', 'info', 'warn', 'error'];
    const currentLevelIndex = levels.indexOf(environment.logLevel);
    const requestedLevelIndex = levels.indexOf(level);
    return requestedLevelIndex >= currentLevelIndex;
  }
}