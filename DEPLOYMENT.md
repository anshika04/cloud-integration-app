# Cloud Integration Platform - Deployment Guide

This guide covers the deployment process for the Cloud Integration Platform across different environments (Development, QA, and Production).

## Environment Overview

The platform supports three deployment environments:

- **Development (dev)**: Local development with debugging enabled
- **QA**: Testing environment with production-like configuration
- **Production (prod)**: Live environment with full security and performance optimizations

## Prerequisites

- Docker and Docker Compose installed
- Git repository cloned
- Environment variables configured (see [Environment Variables](#environment-variables))

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd cloud-integration-app
```

### 2. Configure Environment Variables
```bash
# Copy the template
cp env.template .env

# Edit with your values
nano .env
```

### 3. Deploy to Development
```bash
./scripts/deploy.sh dev
```

### 4. Deploy to QA
```bash
export DB_PASSWORD="your_qa_password"
export JWT_SECRET="your_qa_jwt_secret"
./scripts/deploy.sh qa
```

### 5. Deploy to Production
```bash
export DB_PASSWORD="your_secure_prod_password"
export JWT_SECRET="your_secure_prod_jwt_secret"
./scripts/deploy.sh prod --force
```

## Deployment Scripts

### Master Deployment Script
```bash
./scripts/deploy.sh <environment> [options]
```

**Environments:**
- `dev` - Development environment
- `qa` - QA environment  
- `prod` - Production environment

**Options:**
- `--clean` - Clean up old Docker images before deployment
- `--force` - Force deployment (required for production)
- `--help` - Show help message

### Individual Environment Scripts
- `scripts/deploy-dev.sh` - Development deployment
- `scripts/deploy-qa.sh` - QA deployment
- `scripts/deploy-prod.sh` - Production deployment

## Environment Variables

### Required Variables

#### Database Configuration
```bash
DB_HOST=postgres
DB_PORT=5432
DB_NAME=cloudintegration
DB_USERNAME=user
DB_PASSWORD=password
```

#### JWT Configuration
```bash
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=3600000
```

### Optional Variables

#### Azure Configuration
```bash
AZURE_CLIENT_ID=your_azure_client_id
AZURE_CLIENT_SECRET=your_azure_client_secret
AZURE_TENANT_ID=your_azure_tenant_id
```

#### GCP Configuration
```bash
GCP_PROJECT_ID=your_gcp_project_id
GCP_SERVICE_ACCOUNT_KEY=your_gcp_service_account_key
```

#### Splunk Configuration
```bash
SPLUNK_HOST=your_splunk_host
SPLUNK_PORT=8089
SPLUNK_TOKEN=your_splunk_token
```

## Environment-Specific Configurations

### Development Environment
- **Ports**: Frontend: 80, Backend: 8080, Database: 5432, Redis: 6379
- **Features**: Debug mode enabled, JWT disabled, verbose logging
- **Database**: `cloudintegration_dev`
- **Security**: Minimal security for easy development

### QA Environment
- **Ports**: Frontend: 81, Backend: 8081, Database: 5433, Redis: 6380
- **Features**: Production-like configuration with test data
- **Database**: `cloudintegration_qa`
- **Security**: JWT enabled, moderate security

### Production Environment
- **Ports**: Frontend: 80/443, Backend: 8080, Database: 5432, Redis: 6379
- **Features**: Full security, performance optimizations, load balancing
- **Database**: `cloudintegration_prod`
- **Security**: Maximum security, rate limiting, SSL/TLS

## Docker Compose Files

- `docker-compose.dev.yml` - Development environment
- `docker-compose.qa.yml` - QA environment
- `docker-compose.prod.yml` - Production environment

## Configuration Files

### Backend Configuration
Located in `environments/{env}/backend/`:
- `application-{env}.yml` - Spring Boot configuration
- `init.sql` - Database initialization scripts

### Frontend Configuration
Located in `environments/{env}/frontend/`:
- `environment.{env}.ts` - Angular environment configuration

### Nginx Configuration
Located in `environments/prod/nginx/`:
- `nginx.conf` - Main Nginx configuration
- `nginx-lb.conf` - Load balancer configuration

### Redis Configuration
Located in `environments/prod/redis/`:
- `redis.conf` - Redis production configuration

## Service Architecture

### Development & QA
```
[Frontend:80/81] → [Backend:8080/8081] → [PostgreSQL:5432/5433]
                                    → [Redis:6379/6380]
```

### Production
```
[Load Balancer:80/443] → [Frontend Cluster] → [Backend Cluster] → [PostgreSQL:5432]
                                                               → [Redis:6379]
```

## Monitoring and Health Checks

### Health Check Endpoints
- Backend: `http://localhost:8080/api/cloud/health`
- Frontend: `http://localhost/` (returns 200 OK)

### Logs
```bash
# View logs for specific service
docker-compose -f docker-compose.{env}.yml logs -f [service]

# View all logs
docker-compose -f docker-compose.{env}.yml logs -f
```

### Container Status
```bash
# Check running containers
docker-compose -f docker-compose.{env}.yml ps

# Check resource usage
docker stats
```

## Troubleshooting

### Common Issues

#### 1. Port Conflicts
```bash
# Check what's using the port
netstat -tulpn | grep :8080

# Kill process using port
sudo kill -9 $(lsof -t -i:8080)
```

#### 2. Database Connection Issues
```bash
# Check database container
docker-compose -f docker-compose.{env}.yml logs postgres

# Test database connection
docker-compose -f docker-compose.{env}.yml exec postgres psql -U username -d database_name
```

#### 3. Build Failures
```bash
# Clean build
./scripts/deploy.sh {env} --clean

# Check build logs
docker-compose -f docker-compose.{env}.yml build --no-cache
```

#### 4. Memory Issues
```bash
# Check Docker resources
docker system df
docker system prune -f
```

### Log Locations
- Backend logs: `./logs/backend/` or `./logs/backend-{env}/`
- Frontend logs: `./logs/frontend/` or `./logs/frontend-{env}/`
- Container logs: `docker-compose logs [service]`

## Security Considerations

### Development
- JWT disabled for easier testing
- Debug logging enabled
- CORS allows localhost origins

### QA
- JWT enabled with test credentials
- Moderate logging
- Limited CORS origins

### Production
- Strong JWT secrets required
- Minimal logging
- Strict CORS policies
- Rate limiting enabled
- SSL/TLS encryption
- Security headers configured

## Backup and Recovery

### Database Backup
```bash
# Create backup
docker-compose -f docker-compose.{env}.yml exec postgres pg_dump -U username database_name > backup.sql

# Restore backup
docker-compose -f docker-compose.{env}.yml exec -T postgres psql -U username database_name < backup.sql
```

### Volume Backup
```bash
# Backup volumes
docker run --rm -v postgres_{env}_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz /data
```

## Scaling

### Horizontal Scaling (Production)
```bash
# Scale backend services
docker-compose -f docker-compose.prod.yml up --scale backend=3 -d

# Scale frontend services
docker-compose -f docker-compose.prod.yml up --scale frontend=3 -d
```

## Maintenance

### Updates
```bash
# Pull latest changes
git pull origin main

# Rebuild and deploy
./scripts/deploy.sh {env} --clean
```

### Cleanup
```bash
# Remove unused containers and images
docker system prune -f

# Remove unused volumes
docker volume prune -f

# Remove unused networks
docker network prune -f
```

## Support

For issues and questions:
1. Check the logs: `docker-compose logs [service]`
2. Review this documentation
3. Check GitHub issues
4. Contact the development team
