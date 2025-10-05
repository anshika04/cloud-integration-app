// Production Environment Configuration
export const environment = {
  production: true,
  environment: 'production',
  
  // API Configuration
  apiUrl: 'http://localhost:8083/api',
  wsUrl: 'ws://localhost:8083/ws',
  
  // Application Configuration
  appName: 'Cloud Integration Platform',
  appVersion: '1.0.0',
  
  // Feature Flags
  features: {
    azure: true,
    gcp: true,
    splunk: true,
    monitoring: true,
    analytics: true,
    debugMode: false
  },
  
  // Logging Configuration
  logging: {
    level: 'warn',
    console: false,
    remote: true,
    remoteUrl: 'https://logs.cloud-integration.company.com/api/logs'
  },
  
  // Authentication Configuration
  auth: {
    enabled: true,
    tokenStorage: 'sessionStorage',
    tokenExpiry: 3600000  // 1 hour
  },
  
  // External Service URLs
  services: {
    azure: {
      enabled: true,
      apiUrl: 'http://localhost:8083/api/azure'
    },
    gcp: {
      enabled: true,
      apiUrl: 'http://localhost:8083/api/gcp'
    },
    splunk: {
      enabled: true,
      apiUrl: 'http://localhost:8083/api/splunk'
    }
  },
  
  // Performance Configuration
  performance: {
    enableProfiler: false,
    logSlowQueries: false,
    slowQueryThreshold: 5000
  },
  
  // Production Specific Configuration
  production: {
    errorReporting: true,
    analytics: true,
    cdnEnabled: true,
    compression: true
  }
};
