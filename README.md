# Cloud Integration Application

A comprehensive full-stack application integrating Spring Boot backend with Angular frontend, featuring multi-cloud integrations (Azure, GCP), logging with Splunk, and multi-environment deployment support.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Environment Setup](#environment-setup)
- [Build & Run Commands](#build--run-commands)
- [Deployment](#deployment)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🎯 Project Overview

This project provides a comprehensive cloud integration platform with the following features:

- **Multi-Environment Support**: Development, QA, and Production environments
- **Cloud Integrations**: Azure (Key Vault, Blob Storage, Queue Service), GCP (Storage, Pub/Sub, Secret Manager)
- **GCP File Management**: Environment-specific storage paths, upload, download, list, and delete operations
- **Excel Processing**: Parse Excel files from GCP, extract sheets, and cache to Redis with unique reference IDs
- **Logging & Monitoring**: Splunk integration with comprehensive logging
- **Security**: OAuth2 Resource Server with environment-specific security configurations
- **Containerization**: Docker support for all environments
- **Database**: PostgreSQL with Redis caching and data management
- **Frontend**: Angular 17 with Material Design components and clean GCP file management UI
- **Monorepo Structure**: Backend and frontend in unified repository

## 🏗️ Architecture

### System Architecture Flow

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Development   │    │      QA         │    │   Production    │
│   Environment   │    │   Environment   │    │   Environment   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │  Load Balancer  │
                    │     (Nginx)     │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Frontend      │
                    │   (Angular)     │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │    Backend      │
                    │ (Spring Boot)   │
                    └─────────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                       │                        │
┌───────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Database    │    │   Redis Cache   │    │ Cloud Services  │
│ (PostgreSQL)  │    │                 │    │ (Azure/GCP)     │
└───────────────┘    └─────────────────┘    └─────────────────┘
```

### Component Architecture

```
Frontend (Angular 17)
├── Dashboard Component
├── Azure Integration Component
├── GCP Integration Component
├── Excel Processor Component
├── Splunk Monitoring Component
└── Cloud Service Integration

Backend (Spring Boot 3.2)
├── REST Controllers
├── Security Configuration
├── Cloud Integration Services
│   ├── Azure Service
│   ├── GCP Service
│   └── Splunk Service
├── Excel Processing Service
│   ├── Excel File Parsing (Apache POI)
│   ├── Sheet Extraction and Caching
│   └── Reference ID Generation
├── Data Access Layer
│   ├── Redis Cache Service
│   ├── Data Entity Repository
│   └── Cache Data Management
├── Configuration Management
└── Redis Integration Services

Infrastructure
├── Docker Containers
├── Environment Configurations
├── Database (PostgreSQL)
├── Cache (Redis)
│   ├── Redis Cache Service
│   ├── Data Entity Management
│   ├── Reference ID Generation
│   └── Cache Statistics & Monitoring
└── Logging (Splunk)
```

## 📋 Prerequisites

### System Requirements

- **Java**: OpenJDK 21 or higher
- **Node.js**: 18.x or higher
- **Docker**: 20.10 or higher
- **Docker Compose**: 2.0 or higher
- **Maven**: 3.8 or higher
- **Git**: Latest version

### Cloud Service Prerequisites

#### Azure
- Azure subscription
- Azure Key Vault
- Azure Blob Storage account
- Azure Service Bus (optional)

#### GCP
- Google Cloud Platform project
- Service Account with appropriate permissions
- Cloud Storage bucket (default: `my-excel-reports`)
- Pub/Sub topics (optional)
- Secret Manager (optional)

**GCP Authentication Methods** (in order of priority):
1. Service Account JSON Key File (recommended for local/Docker)
2. Base64 Encoded Service Account Key (via environment variable)
3. Application Default Credentials (for GCP VM/GKE)

**GCP Configuration:**
```yaml
gcp:
  enabled: true
  project-id: your_gcp_project_id
  storage:
    bucket-name: my-excel-reports
  credentials:
    service-account-key: ${GCP_SERVICE_ACCOUNT_KEY:}  # Base64 encoded (optional)
    key-file-path: ${GCP_KEY_FILE_PATH:}               # Absolute path to JSON (optional)
  pubsub:
    topic-name: your_topic_name
    subscription-name: your_subscription_name
```

**Environment Variables:**
```bash
GCP_ENABLED=true
GCP_PROJECT_ID=your_gcp_project_id
GCP_KEY_FILE_PATH=/path/to/gcp-storage-key.json
# GCP_SERVICE_ACCOUNT_KEY=base64_encoded_json_key (alternative)
```

#### Splunk
- Splunk Enterprise or Cloud instance
- Splunk API token
- Appropriate log indexes configured

### Environment Variables

Create a `.env` file in the project root with the following variables:

```bash
# Database Configuration
DB_PASSWORD=your_secure_password
DB_USERNAME=your_username
DB_NAME=cloudintegration

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_minimum_256_bits

# Azure Configuration
AZURE_CLIENT_ID=your_azure_client_id
AZURE_CLIENT_SECRET=your_azure_client_secret
AZURE_TENANT_ID=your_azure_tenant_id
AZURE_KEY_VAULT_URL=https://your-keyvault.vault.azure.net/
AZURE_STORAGE_CONNECTION_STRING=your_azure_storage_connection_string

# GCP Configuration
GCP_PROJECT_ID=your_gcp_project_id
GCP_SERVICE_ACCOUNT_KEY=your_base64_encoded_service_account_json

# Splunk Configuration
SPLUNK_HOST=your_splunk_host
SPLUNK_PORT=8089
SPLUNK_TOKEN=your_splunk_api_token
SPLUNK_INDEX=your_splunk_index_name
```

## 📁 Project Structure

```
cloud-integration-app/
├── src/                                    # Backend source code
│   ├── main/java/com/example/cloudintegrationapp/
│   │   ├── CloudIntegrationAppApplication.java
│   │   ├── config/                         # Configuration classes
│   │   ├── controller/                     # REST controllers
│   │   ├── integration/                    # Cloud integration services
│   │   │   ├── azure/                     # Azure integration
│   │   │   ├── gcp/                       # GCP integration (Storage, Pub/Sub, Secrets)
│   │   │   └── splunk/                    # Splunk integration
│   │   ├── model/                         # Data models
│   │   │   ├── DataEntity.java           # JPA entity for structured data
│   │   │   ├── CacheData.java            # Redis cache data model
│   │   │   └── ApiResponse.java          # Standardized API responses
│   │   ├── repository/                    # Data access layer
│   │   │   └── DataEntityRepository.java # JPA repository for data entities
│   │   └── service/                       # Business logic services
│   │       ├── RedisCacheService.java    # Redis cache operations
│   │       ├── DataService.java          # Data orchestration service
│   │       ├── ExcelProcessingService.java # Excel file parsing and caching
│   │       └── ReferenceIdGenerator.java # Unique ID generation service
│   └── main/resources/
│       └── application.yml                # Main configuration
├── frontend/                              # Angular frontend (monorepo)
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/               # Angular components
│   │   │   │   ├── dashboard/
│   │   │   │   ├── azure/
│   │   │   │   ├── gcp/                  # GCP file management UI
│   │   │   │   ├── excel-processor/       # Excel processing UI
│   │   │   │   ├── splunk/
│   │   │   │   └── monitoring/
│   │   │   ├── services/                 # Angular services
│   │   │   ├── app.component.ts
│   │   │   ├── app.routes.ts
│   │   │   └── main.ts
│   │   ├── assets/                       # Static assets
│   │   └── index.html
│   ├── Dockerfile.frontend               # Frontend Dockerfile
│   ├── nginx.conf                        # Nginx configuration
│   ├── package.json                      # Node.js dependencies
│   └── angular.json                      # Angular configuration
├── environments/                          # Environment-specific configs
│   ├── dev/                              # Development environment
│   │   ├── backend/
│   │   │   ├── application-dev.yml
│   │   │   └── init.sql
│   │   └── frontend/
│   │       └── environment.dev.ts
│   ├── qa/                               # QA environment
│   │   ├── backend/
│   │   │   ├── application-qa.yml
│   │   │   └── init.sql
│   │   └── frontend/
│   │       └── environment.qa.ts
│   └── prod/                             # Production environment
│       ├── backend/
│       │   ├── application-prod.yml
│       │   └── init.sql
│       ├── frontend/
│       │   └── environment.prod.ts
│       ├── nginx/                        # Nginx configurations
│       └── redis/                        # Redis configurations
├── scripts/                              # Deployment scripts
│   ├── deploy-dev.sh
│   ├── deploy-qa.sh
│   ├── deploy-prod.sh
│   ├── deploy-all.sh
│   ├── run-all-environments.sh
│   └── stop-all.sh
├── docker-compose.dev.yml                # Development Docker Compose
├── docker-compose.qa.yml                 # QA Docker Compose
├── docker-compose.prod.yml               # Production Docker Compose
├── Dockerfile.backend                    # Backend Dockerfile
├── pom.xml                               # Maven configuration
├── .gitignore                            # Git ignore rules (excludes credentials)
├── REDIS_CACHE_API.md                    # Redis cache API documentation
└── README.md
```

## 🚀 Quick Start

### Option 1: Single Command (Recommended)

Deploy all environments simultaneously:

```bash
# Clone the repository
git clone <repository-url>
cd cloud-integration-app

# Set environment variables (copy from env.template)
cp env.template .env
# Edit .env with your configuration

# Deploy all environments
./scripts/run-all-environments.sh
```

### Option 2: Individual Environment Deployment

```bash
# Deploy specific environment
./scripts/deploy-dev.sh    # Development
./scripts/deploy-qa.sh     # QA
./scripts/deploy-prod.sh   # Production
```

## 🔧 Build & Run Commands

### Backend (Spring Boot)

#### Local Development

```bash
# Navigate to backend directory
cd cloud-integration-app

# Install dependencies
mvn clean install

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=qa
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/cloud-integration-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

#### Docker Build

```bash
# Build backend image
docker build -f Dockerfile.backend -t cloud-integration-app-backend .

# Run backend container
docker run -d \
  --name cloud-integration-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  cloud-integration-app-backend
```

### Frontend (Angular)

#### Local Development

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Development server
npm start
# or
ng serve

# Build for production
npm run build
# or
ng build --configuration=production

# Run tests
npm test
# or
ng test
```

#### Docker Build

```bash
# Build frontend image
docker build -f frontend/Dockerfile.frontend -t cloud-integration-app-frontend ./frontend

# Run frontend container
docker run -d \
  --name cloud-integration-frontend \
  -p 4200:80 \
  cloud-integration-app-frontend
```

## 🌍 Environment Setup

### Development Environment

**Ports:**
- Frontend: `3001`
- Backend: `8081`
- Database: `5432`
- Redis: `6379`

**Features:**
- Anonymous authentication
- Debug logging enabled
- Hot reload support
- Development database

**Deploy:**
```bash
./scripts/deploy-dev.sh
```

**Access:**
- Frontend: http://localhost:3001
- Backend: http://localhost:8081/api
- Health Check: http://localhost:8081/api/cloud/health

### QA Environment

**Ports:**
- Frontend: `3002`
- Backend: `8082`
- Database: `5433`
- Redis: `6380`

**Features:**
- Anonymous authentication
- QA-specific configurations
- Performance testing enabled
- Separate QA database

**Deploy:**
```bash
./scripts/deploy-qa.sh
```

**Access:**
- Frontend: http://localhost:3002
- Backend: http://localhost:8082/api
- Health Check: http://localhost:8082/api/cloud/health

### Production Environment

**Ports:**
- Frontend: `3003`
- Backend: `8083`
- Database: `5434`
- Redis: `6381`

**Features:**
- OAuth2 authentication
- Production security settings
- Optimized performance
- Production database
- Monitoring enabled

**Deploy:**
```bash
./scripts/deploy-prod.sh
```

**Access:**
- Frontend: http://localhost:3003
- Backend: http://localhost:8083/api
- Health Check: http://localhost:8083/api/cloud/health

## 🚀 Deployment

### Docker Compose Commands

#### Individual Environments

```bash
# Development
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml up -d
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml down

# QA
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml up -d
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml down

# Production
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml up -d
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml down
```

#### All Environments

```bash
# Start all environments
./scripts/run-all-environments.sh

# Stop all environments
./scripts/stop-all.sh
```

### Kubernetes Deployment

#### AKS (Azure Kubernetes Service)

```bash
# Deploy to AKS
kubectl apply -f k8s/aks/namespace.yaml
kubectl apply -f k8s/aks/backend-deployment.yaml
kubectl apply -f k8s/aks/backend-service.yaml
kubectl apply -f k8s/aks/frontend-deployment.yaml
kubectl apply -f k8s/aks/ingress.yaml
```

#### GKE (Google Kubernetes Engine)

```bash
# Deploy to GKE
kubectl apply -f k8s/gke/backend-deployment.yaml
```

### Health Checks

```bash
# Check all frontend services
curl -I http://localhost:3001  # Dev
curl -I http://localhost:3002  # QA
curl -I http://localhost:3003  # Prod

# Check all backend services
curl -I http://localhost:8081/api/cloud/health  # Dev
curl -I http://localhost:8082/api/cloud/health  # QA
curl -I http://localhost:8083/api/cloud/health  # Prod

# Check container status
docker ps | grep cloud-integration
```

## 📚 API Documentation

### Base URLs

| Environment | Base URL |
|-------------|----------|
| Development | http://localhost:8081/api |
| QA | http://localhost:8082/api |
| Production | http://localhost:8083/api |

### Endpoints

#### Health Check
```http
GET /cloud/health
```

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-01T00:00:00Z",
  "services": {
    "database": "UP",
    "redis": "UP",
    "azure": "UP",
    "gcp": "UP",
    "splunk": "UP"
  }
}
```

#### Test Endpoint
```http
GET /cloud/test
```

**Response:**
```json
{
  "message": "Cloud Integration API is working!",
  "timestamp": "2024-01-01T00:00:00Z",
  "environment": "dev"
}
```

#### Azure Integration
```http
GET /cloud/azure/status
POST /cloud/azure/blob/upload
GET /cloud/azure/queue/messages
```

#### GCP Integration
```http
GET /cloud/gcp/status
POST /cloud/gcp/upload
GET /cloud/gcp/download/{filename}
GET /cloud/gcp/files
DELETE /cloud/gcp/files/{filename}
POST /cloud/gcp/pubsub
POST /cloud/gcp/excel/parse/{filename}
```

#### GCP File Management

The GCP integration provides comprehensive file management with environment-specific storage paths. Files are automatically organized by environment to ensure proper data isolation.

**Upload File:**
```http
POST /cloud/gcp/upload
Content-Type: multipart/form-data

file: [binary file data]
```

**Response:**
```json
{
  "message": "File uploaded successfully to GCP",
  "filename": "sample.xlsx"
}
```

**List Files:**
```http
GET /cloud/gcp/files
```

**Response:**
```json
{
  "success": true,
  "message": "Files retrieved successfully",
  "data": [
    {
      "name": "dev/reports/sample.xlsx",
      "path": "dev/reports/sample.xlsx",
      "filename": "sample.xlsx",
      "size": 10621,
      "contentType": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "created": 1727819836000,
      "updated": 1727819836000
    }
  ],
  "timestamp": "2025-11-01T02:37:16"
}
```

**Download File:**
```http
GET /cloud/gcp/download/{filename}
```

Returns binary file data with appropriate content type.

**Delete File:**
```http
DELETE /cloud/gcp/files/{filename}
```

**Response:**
```json
{
  "success": true,
  "message": "File deleted successfully",
  "data": "sample.xlsx",
  "timestamp": "2025-11-01T02:37:16"
}
```

**Environment-Specific Paths:**
- **Development**: `dev/reports/`
- **QA**: `qa/reports/`
- **Production**: `prod/reports/`

Files are automatically stored in the appropriate environment path based on the active Spring profile.

#### Excel Processing

The Excel processing feature allows you to parse Excel files from GCP storage, extract data from all sheets, and cache each sheet to Redis with unique reference IDs for easy retrieval.

**Parse Excel File from GCP:**
```http
POST /cloud/gcp/excel/parse/{filename}
```

**Response:**
```json
{
  "success": true,
  "message": "Excel file parsed and cached successfully",
  "data": {
    "filename": "sample-data-file.xlsx",
    "totalSheets": 2,
    "sheets": [
      {
        "name": "QuerySheet1",
        "headers": ["First Name", "Last Name", "Gender", "Country", "Age", "Date", "Id"],
        "rows": [
          {
            "First Name": "1",
            "Last Name": "Dulce",
            "Gender": "Abril",
            "Country": "Female",
            "Age": "United States",
            "Date": "32",
            "Id": "15/10/2017"
          }
        ],
        "rowCount": 9
      }
    ],
    "cachedSheets": [
      {
        "sheetName": "QuerySheet1",
        "referenceId": "EXCEL-20251101061840-TCNYB1-0003",
        "rowCount": "9"
      },
      {
        "sheetName": "QuerySheet2",
        "referenceId": "EXCEL-20251101061840-JS8QGZ-0004",
        "rowCount": "5"
      }
    ]
  },
  "timestamp": "2025-11-01T06:18:40"
}
```

**Features:**
- Supports `.xlsx`, `.xls`, and `.xlsm` file formats
- Automatically extracts all sheets from the workbook
- Parses headers and data rows from each sheet
- Generates unique reference IDs for each sheet (format: `EXCEL-TIMESTAMP-RANDOM-SEQUENCE`)
- Caches each sheet to Redis with 1-hour TTL
- Returns comprehensive parsing results with row counts

**Frontend UI:**
Access the Excel Processor via the side navigation menu or directly at `/excel-processor`. The UI allows you to:
- Browse Excel files available in GCP bucket
- Select and process Excel files
- View parsed sheets with their reference IDs
- Monitor processing progress

**Retrieving Cached Sheet Data:**
Once an Excel file is parsed, you can retrieve individual sheets from Redis using their reference IDs:

```http
GET /cache/retrieve/{referenceId}
```

For example, to retrieve the first sheet:
```http
GET /cache/retrieve/EXCEL-20251101061840-TCNYB1-0003
```

#### Splunk Integration
```http
GET /cloud/splunk/status
POST /cloud/splunk/log
GET /cloud/splunk/search
```

### Redis Cache Integration

The application includes comprehensive Redis cache integration with data management capabilities, reference ID generation, and cache statistics monitoring.

#### Cache Management Endpoints

**Cache Statistics:**
```http
GET /cache/stats
```

**Response:**
```json
{
  "success": true,
  "message": "Cache statistics retrieved successfully",
  "data": {
    "redis_version": "7.4.6",
    "total_commands_processed": "150",
    "used_memory": "2.5M",
    "connected_clients": "3",
    "keyspace_hits": "45",
    "application_keys_count": 12,
    "keyspace_misses": "8"
  },
  "timestamp": "2025-10-07T06:55:06"
}
```

**Reference ID Generation:**
```http
GET /cache/generate-reference-id
GET /cache/generate-reference-id/{prefix}
```

**Response:**
```json
{
  "success": true,
  "message": "Reference ID generated successfully",
  "data": "CLD-20251007065510-ABC123-0001",
  "referenceId": "CLD-20251007065510-ABC123-0001",
  "timestamp": "2025-10-07T06:55:10"
}
```

**Data Storage:**
```http
POST /cache/store
Content-Type: application/json

{
  "prefix": "USER",
  "data": {
    "userId": 12345,
    "sessionId": "sess_abc123",
    "preferences": {
      "theme": "dark",
      "language": "en"
    }
  },
  "dataType": "USER_SESSION",
  "ttlSeconds": 3600
}
```

**Data Retrieval:**
```http
GET /cache/retrieve/{referenceId}
```

**Response:**
```json
{
  "success": true,
  "message": "Data retrieved from cache successfully",
  "data": {
    "referenceId": "USER-20251007065510-DEF456-0002",
    "dataType": "USER_SESSION",
    "data": {
      "userId": 12345,
      "sessionId": "sess_abc123",
      "preferences": {
        "theme": "dark",
        "language": "en"
      }
    },
    "cachedAt": "2025-10-07T06:55:10",
    "ttlSeconds": 3600
  },
  "referenceId": "USER-20251007065510-DEF456-0002"
}
```

**Cache Operations:**
```http
# Check if data exists
GET /cache/exists/{referenceId}

# Delete cached data
DELETE /cache/delete/{referenceId}

# Get TTL for data
GET /cache/ttl/{referenceId}

# Set TTL for data
PUT /cache/ttl/{referenceId}?ttlSeconds=7200
```

#### Data Entity Management

**Create Data Entity:**
```http
POST /cache/data-entity
Content-Type: application/json

{
  "name": "Sample Data Entity",
  "description": "This is a sample data entity for testing",
  "category": "SAMPLE"
}
```

**List Data Entities:**
```http
GET /cache/data-entities
```

**Get Specific Data Entity:**
```http
GET /cache/data-entity/{referenceId}
```

#### Cloud Integration with Cache

**Store Custom Data:**
```http
POST /cloud/store-data
Content-Type: application/json

{
  "prefix": "CLOUD",
  "data": {
    "operation": "upload",
    "fileSize": 1024,
    "status": "completed"
  },
  "dataType": "FILE_UPLOAD",
  "ttlSeconds": 1800
}
```

**Retrieve Custom Data:**
```http
GET /cloud/retrieve-data/{referenceId}
```

**Azure Upload with Cache Tracking:**
```http
POST /cloud/azure/upload-with-cache
Content-Type: multipart/form-data

file: [binary file data]
```

**Azure Upload Status:**
```http
GET /cloud/azure/upload-status/{referenceId}
```

#### Redis Configuration

**Environment-Specific Redis Settings:**

**Development:**
- Host: `redis` (Docker network)
- Port: `6379`
- Database: `0`
- Connection Pool: 8 max active connections

**QA:**
- Host: `redis` (Docker network)
- Port: `6379`
- Database: `1`
- Connection Pool: 8 max active connections

**Production:**
- Host: `redis` (Docker network)
- Port: `6379`
- Database: `2`
- Connection Pool: 20 max active connections
- Enhanced monitoring and logging

#### Reference ID Generation

The system provides various reference ID generators:

- **Default**: `CLD-{timestamp}-{random}`
- **Azure**: `AZR-{timestamp}-{random}`
- **GCP**: `GCP-{timestamp}-{random}`
- **Splunk**: `SPL-{timestamp}-{random}`
- **User**: `USR-{timestamp}-{random}`
- **Document**: `DOC-{timestamp}-{random}`
- **Transaction**: `TXN-{timestamp}-{random}`
- **Cache**: `CACHE-{timestamp}-{random}`
- **System**: `SYS-{timestamp}-{random}`

**Format:** `{PREFIX}-{YYYYMMDDHHMMSS}-{6_CHAR_ALPHANUMERIC}-{SEQUENCE}`

### Authentication

#### Development/QA
- No authentication required
- Anonymous access enabled

#### Production
- OAuth2 Resource Server
- Bearer token authentication
- Health endpoints are public

## ⚙️ Configuration

### Backend Configuration

#### Application Properties

**Development (`application-dev.yml`):**
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:postgresql://postgres:5432/cloudintegration_dev
    username: dev_user
    password: devpassword123
  security:
    oauth2:
      resourceserver:
        jwt:
          validation: false
  jpa:
    hibernate:
      ddl-auto: update
  data:
    redis:
      host: redis
      port: 6379
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
logging:
  level:
    com.example.cloudintegrationapp: DEBUG
    org.springframework.security: DEBUG
    org.springframework.data.redis: DEBUG
```

**QA (`application-qa.yml`):**
```yaml
spring:
  profiles:
    active: qa
  datasource:
    url: jdbc:postgresql://postgres:5432/cloudintegration_qa
    username: qa_user
    password: qapassword123
  security:
    oauth2:
      resourceserver:
        jwt:
          validation: false
  data:
    redis:
      host: redis
      port: 6379
      database: 1
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
azure:
  enabled: false
gcp:
  enabled: true
splunk:
  enabled: true
```

**Production (`application-prod.yml`):**
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:postgresql://postgres:5432/cloudintegration_prod
    username: prod_user
    password: ${DB_PASSWORD}
  security:
    oauth2:
      resourceserver:
        jwt:
          validation: true
  data:
    redis:
      host: redis
      port: 6379
      database: 2
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: 3000ms
azure:
  enabled: true
gcp:
  enabled: true
splunk:
  enabled: true
```

### Frontend Configuration

#### Environment Files

**Development (`environment.dev.ts`):**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081/api',
  environment: 'development'
};
```

**QA (`environment.qa.ts`):**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8082/api',
  environment: 'qa'
};
```

**Production (`environment.prod.ts`):**
```typescript
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8083/api',
  environment: 'production'
};
```

## 🔍 Troubleshooting

### Common Issues

#### 1. Port Conflicts

**Error:** `Port already in use`

**Solution:**
```bash
# Check what's using the port
lsof -i :3001
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or stop all environments
./scripts/stop-all.sh
```

#### 2. Database Connection Issues

**Error:** `Connection refused to database`

**Solution:**
```bash
# Check database container status
docker ps | grep postgres

# Check database logs
docker logs cloud-integration-dev-postgres

# Restart database
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml restart postgres
```

#### 3. Frontend Build Issues

**Error:** `Module not found` or build failures

**Solution:**
```bash
# Clear node modules and reinstall
cd cloud-integration-frontend
rm -rf node_modules package-lock.json
npm install

# Clear Angular cache
ng cache clean

# Rebuild
npm run build
```

#### 4. Docker Build Issues

**Error:** `Build context too large`

**Solution:**
```bash
# Add to .dockerignore
node_modules
target
.git
*.log
dist
```

#### 5. Security Configuration Issues

**Error:** `401 Unauthorized` in production

**Solution:**
```bash
# Check security configuration
curl -H "Authorization: Bearer <token>" http://localhost:8083/api/cloud/health

# Verify JWT_SECRET is set
echo $JWT_SECRET
```

#### 6. Redis Cache Issues

**Error:** `Connection refused to Redis` or cache operations failing

**Solution:**
```bash
# Check Redis container status
docker ps | grep redis

# Check Redis logs
docker logs cloud-integration-dev-redis
docker logs cloud-integration-qa-redis
docker logs cloud-integration-prod-redis

# Test Redis connection
docker exec cloud-integration-dev-redis redis-cli ping

# Check Redis memory usage
docker exec cloud-integration-dev-redis redis-cli info memory

# Clear Redis cache if needed
docker exec cloud-integration-dev-redis redis-cli flushdb
```

**Error:** `Cache data not found` or TTL issues

**Solution:**
```bash
# Check if data exists in Redis
docker exec cloud-integration-dev-redis redis-cli keys "cloud-integration:data:*"

# Check TTL for specific key
docker exec cloud-integration-dev-redis redis-cli ttl "cloud-integration:data:YOUR_REFERENCE_ID"

# Monitor Redis operations
docker exec cloud-integration-dev-redis redis-cli monitor
```

#### 7. Database Schema Issues

**Error:** `Schema-validation: missing table [data_entities]`

**Solution:**
```bash
# Check if JPA is configured to create tables
grep -r "ddl-auto" environments/*/backend/

# Should be set to "update" not "validate"
# Restart backend container after fixing configuration
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml restart backend
```

#### 8. GCP Integration Issues

**Error:** `GCP service account key file not found` or `Permission denied`

**Solution:**
```bash
# Check if GCP key file exists and is accessible
ls -la ~/Downloads/gcp-storage-key.json
chmod 600 ~/Downloads/gcp-storage-key.json

# Verify Docker volume mount
grep -A5 "gcp-storage-key" docker-compose.dev.yml

# Check GCP configuration
docker logs cloud-integration-dev-backend | grep -i "gcp"

# Test GCP connection
curl http://localhost:8081/api/cloud/gcp/files
```

**Error:** `404 Not Found` when accessing GCP endpoints

**Solution:**
```bash
# Verify GCP is enabled in configuration
grep -A3 "gcp.enabled" src/main/resources/application.yml

# Check GcpService is loaded
docker logs cloud-integration-dev-backend | grep "GcpService"

# Verify bucket and project ID are correct
docker exec cloud-integration-dev-backend env | grep GCP
```

**Error:** `MalformedJsonException` when loading GCP credentials

**Solution:**
```bash
# Verify JSON key file is valid
cat ~/Downloads/gcp-storage-key.json | jq .

# Check if using correct authentication method
# Remove GCP_SERVICE_ACCOUNT_KEY if using file path
docker-compose down && docker-compose up -d
```

**Error:** JVM crash (SIGSEGV) on ARM64 with Alpine-based Docker image

**Solution:**
- Already fixed: Using Debian-based JRE instead of Alpine in `Dockerfile.backend`
- If issue persists, ensure using `eclipse-temurin:21-jre` base image

### Log Analysis

#### View Application Logs

```bash
# Backend logs
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs -f backend

# Frontend logs
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs -f frontend

# Database logs
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs -f postgres
```

#### Splunk Log Analysis

```bash
# Search logs in Splunk
source="cloud-integration-app" | head 100
source="cloud-integration-app" | where status="ERROR" | head 50
```

### Performance Monitoring

#### Check Resource Usage

```bash
# Docker stats
docker stats

# Container resource usage
docker exec cloud-integration-dev-backend top
docker exec cloud-integration-dev-frontend top
```

#### Database Performance

```bash
# Connect to database
docker exec -it cloud-integration-dev-postgres psql -U dev_user -d cloudintegration_dev

# Check connections
SELECT count(*) FROM pg_stat_activity;

# Check slow queries
SELECT query, mean_time, calls FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;
```

#### Redis Cache Performance

```bash
# Connect to Redis
docker exec -it cloud-integration-dev-redis redis-cli

# Check Redis info
INFO stats
INFO memory
INFO clients

# Monitor Redis operations in real-time
MONITOR

# Check cache hit ratio
INFO stats | grep keyspace_hits
INFO stats | grep keyspace_misses

# List all cached data
KEYS "cloud-integration:data:*"

# Check memory usage by key
MEMORY USAGE "cloud-integration:data:YOUR_REFERENCE_ID"

# Get Redis configuration
CONFIG GET "*"
```

#### Cache Statistics API

```bash
# Get cache statistics via API
curl http://localhost:8081/api/cache/stats
curl http://localhost:8082/api/cache/stats
curl http://localhost:8083/api/cache/stats

# Check cache health
curl http://localhost:8081/api/cloud/health
```

## 🤝 Contributing

### Development Workflow

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes**
4. **Test your changes**
   ```bash
   # Backend tests
   mvn test
   
   # Frontend tests
   npm test
   
   # Integration tests
   ./scripts/deploy-dev.sh
   curl http://localhost:8081/api/cloud/health
   ```
5. **Commit your changes**
   ```bash
   git commit -m "feat: add your feature description"
   ```
6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```
7. **Create a Pull Request**

### Code Standards

#### Backend (Java)
- Follow Spring Boot best practices
- Use proper logging with SLF4J
- Write unit tests for all services
- Document public APIs with JavaDoc

#### Frontend (Angular)
- Follow Angular style guide
- Use TypeScript strict mode
- Write unit tests for components
- Use Angular Material components

### Testing

#### Backend Testing
```bash
# Unit tests
mvn test

# Integration tests
mvn test -Dtest=*IntegrationTest

# Test coverage
mvn jacoco:report
```

#### Frontend Testing
```bash
# Unit tests
npm test

# E2E tests
npm run e2e

# Test coverage
npm run test:coverage
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:

- Create an issue in the repository
- Check the troubleshooting section
- Review the logs for error details
- Contact the development team

---

**Last Updated:** November 2025  
**Version:** 1.3.0  
**Features:** Excel Processing from GCP, GCP File Management, Redis Cache Integration, Multi-Environment Support, Cloud Integrations, Monorepo Structure  
**Maintainer:** Cloud Integration Team