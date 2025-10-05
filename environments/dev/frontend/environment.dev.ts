// Development Environment Configuration
export const environment = {
  production: false,
  environment: 'development',
  
  // API Configuration
  apiUrl: 'http://localhost:8081/api',
  wsUrl: 'ws://localhost:8081/ws',
  
  // Application Configuration
  appName: 'Cloud Integration Platform',
  appVersion: '1.0.0-dev',
  
  // Feature Flags
  features: {
    azure: true,
    gcp: true,
    splunk: true,
    monitoring: true,
    analytics: true,
    debugMode: true
  },
  
  // Logging Configuration
  logging: {
    level: 'debug',
    console: true,
    remote: false
  },
  
  // Authentication Configuration
  auth: {
    enabled: false,  // Disabled for development
    tokenStorage: 'localStorage'
  },
  
  // External Service URLs
  services: {
    azure: {
      enabled: false,
      apiUrl: 'http://localhost:8081/api/azure'
    },
    gcp: {
      enabled: false,
      apiUrl: 'http://localhost:8081/api/gcp'
    },
    splunk: {
      enabled: false,
      apiUrl: 'http://localhost:8081/api/splunk'
    }
  },
  
  // Performance Configuration
  performance: {
    enableProfiler: true,
    logSlowQueries: true,
    slowQueryThreshold: 1000
  },
  
  // Development Tools
  devTools: {
    reduxDevTools: true,
    angularDevTools: true,
    mockData: true
  }
};
