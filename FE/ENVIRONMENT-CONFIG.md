# Environment Configuration

This document explains how to configure environments for development and production builds.

## Environment Files

### Development (`src/environments/environment.ts`)
- Used for local development
- API points to localhost:8080
- Debug tools enabled
- Detailed logging
- Mock data support

### Production (`src/environments/environment.prod.ts`)
- Used for production builds
- API points to Cloud Run URL
- Debug tools disabled
- Error-level logging only
- Analytics enabled

## Configuration Options

| Property | Development | Production | Description |
|----------|-------------|------------|-------------|
| `production` | `false` | `true` | Environment flag |
| `apiUrl` | `http://localhost:8080/api` | `https://your-backend-url/api` | Backend API URL |
| `appName` | `Parking System` | `Parking System` | Application name |
| `version` | `1.0.0-dev` | `1.0.0` | Application version |
| `enableDebugTools` | `true` | `false` | Angular debug tools |
| `logLevel` | `debug` | `error` | Console logging level |
| `features.enableMockData` | `true` | `false` | Use mock data |
| `features.enableDevTools` | `true` | `false` | Development tools |
| `features.enableAnalytics` | `false` | `true` | Analytics tracking |

## Build Commands

### Development Build
```bash
ng build --configuration=development
```

### Production Build
```bash
ng build --configuration=production
```

### Serve Development
```bash
ng serve --configuration=development
```

### Serve Production (for testing)
```bash
ng serve --configuration=production
```

## File Replacement

The `angular.json` is configured to replace `environment.ts` with `environment.prod.ts` during production builds:

```json
"fileReplacements": [
  {
    "replace": "src/environments/environment.ts",
    "with": "src/environments/environment.prod.ts"
  }
]
```

## Usage in Components/Services

```typescript
import { environment } from '../../environments/environment';

// Direct usage
const apiUrl = environment.apiUrl;

// Or use EnvironmentService
constructor(private envService: EnvironmentService) {}

ngOnInit() {
  if (this.envService.isProduction) {
    // Production-specific logic
  }
  
  this.envService.log('debug', 'Component initialized');
}
```

## Environment Variables for Production

For production deployment, update `environment.prod.ts` with your actual URLs:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-actual-backend-url/api',
  // ... other config
};
```

## Security Notes

- Never commit sensitive data in environment files
- Use build-time replacement for secrets
- Consider using Angular's built-in support for runtime configuration