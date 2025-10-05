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
- **Logging & Monitoring**: Splunk integration with comprehensive logging
- **Security**: OAuth2 Resource Server with environment-specific security configurations
- **Containerization**: Docker support for all environments
- **Database**: PostgreSQL with Redis caching
- **Frontend**: Angular 17 with Material Design components

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
├── Splunk Monitoring Component
└── Cloud Service Integration

Backend (Spring Boot 3.2)
├── REST Controllers
├── Security Configuration
├── Cloud Integration Services
│   ├── Azure Service
│   ├── GCP Service
│   └── Splunk Service
├── Data Access Layer
└── Configuration Management

Infrastructure
├── Docker Containers
├── Environment Configurations
├── Database (PostgreSQL)
├── Cache (Redis)
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
- Cloud Storage bucket
- Pub/Sub topics (optional)

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
│   │   │   ├── gcp/                       # GCP integration
│   │   │   └── splunk/                    # Splunk integration
│   │   ├── model/                         # Data models
│   │   ├── repository/                    # Data access layer
│   │   └── service/                       # Business logic services
│   └── main/resources/
│       └── application.yml                # Main configuration
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
└── README.md

cloud-integration-frontend/
├── src/                                  # Frontend source code
│   ├── app/
│   │   ├── components/                   # Angular components
│   │   │   ├── dashboard/
│   │   │   ├── azure/
│   │   │   ├── gcp/
│   │   │   ├── splunk/
│   │   │   └── monitoring/
│   │   ├── services/                     # Angular services
│   │   ├── app.component.ts
│   │   ├── app.routes.ts
│   │   └── main.ts
│   ├── assets/                           # Static assets
│   └── index.html
├── Dockerfile.frontend                   # Frontend Dockerfile
├── nginx.conf                            # Nginx configuration
├── package.json                          # Node.js dependencies
└── angular.json                          # Angular configuration
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
cd ../cloud-integration-frontend

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
docker build -f Dockerfile.frontend -t cloud-integration-app-frontend .

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
POST /cloud/gcp/storage/upload
GET /cloud/gcp/pubsub/messages
```

#### Splunk Integration
```http
GET /cloud/splunk/status
POST /cloud/splunk/log
GET /cloud/splunk/search
```

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
logging:
  level:
    com.example.cloudintegrationapp: DEBUG
    org.springframework.security: DEBUG
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

**Last Updated:** January 2024  
**Version:** 1.0.0  
**Maintainer:** Cloud Integration Team