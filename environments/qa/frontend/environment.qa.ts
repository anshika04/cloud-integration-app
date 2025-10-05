// QA Environment Configuration
export const environment = {
  production: false,
  environment: 'qa',
  
  // API Configuration
  apiUrl: 'http://localhost:8082/api',
  wsUrl: 'ws://localhost:8082/ws',
  
  // Application Configuration
  appName: 'Cloud Integration Platform',
  appVersion: '1.0.0-qa',
  
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
    level: 'info',
    console: true,
    remote: true,
    remoteUrl: 'https://qa-logs.cloud-integration.company.com/api/logs'
  },
  
  // Authentication Configuration
  auth: {
    enabled: true,
    tokenStorage: 'localStorage',
    tokenExpiry: 86400000  // 24 hours
  },
  
  // External Service URLs
  services: {
    azure: {
      enabled: true,
      apiUrl: 'http://localhost:8082/api/azure'
    },
    gcp: {
      enabled: true,
      apiUrl: 'http://localhost:8082/api/gcp'
    },
    splunk: {
      enabled: true,
      apiUrl: 'http://localhost:8082/api/splunk'
    }
  },
  
  // Performance Configuration
  performance: {
    enableProfiler: false,
    logSlowQueries: true,
    slowQueryThreshold: 2000
  },
  
  // QA Specific Configuration
  qa: {
    testDataEnabled: true,
    mockExternalServices: false,
    performanceMonitoring: true
  }
};
