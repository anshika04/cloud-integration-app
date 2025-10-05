# Deployment Guide

## 🚀 Complete Deployment Instructions

This guide provides step-by-step instructions for deploying the Cloud Integration Application across all environments.

## 📋 Prerequisites

### System Requirements
- **Operating System**: macOS, Linux, or Windows with WSL2
- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher
- **Memory**: Minimum 8GB RAM (16GB recommended)
- **Storage**: Minimum 10GB free space
- **CPU**: Multi-core processor (4+ cores recommended)

### Software Dependencies
```bash
# Check Docker installation
docker --version
docker-compose --version

# Verify Docker is running
docker ps
```

### Network Requirements
- **Ports Available**: 3001-3003, 5432-5434, 6379-6381, 8081-8083
- **Internet Access**: Required for downloading Docker images and cloud integrations
- **Firewall**: Ensure Docker can access required ports

## 🔧 Initial Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd cloud-integration-app
```

### 2. Environment Configuration
```bash
# Copy environment template
cp env.template .env

# Edit environment variables
nano .env
```

**Required Environment Variables:**
```bash
# Database Configuration
DB_PASSWORD=your_secure_password_here
DB_USERNAME=your_database_username
DB_NAME=cloudintegration

# JWT Configuration
JWT_SECRET=your_jwt_secret_minimum_256_bits_long

# Azure Configuration (Optional)
AZURE_CLIENT_ID=your_azure_client_id
AZURE_CLIENT_SECRET=your_azure_client_secret
AZURE_TENANT_ID=your_azure_tenant_id
AZURE_KEY_VAULT_URL=https://your-keyvault.vault.azure.net/
AZURE_STORAGE_CONNECTION_STRING=your_azure_storage_connection_string

# GCP Configuration (Optional)
GCP_PROJECT_ID=your_gcp_project_id
GCP_SERVICE_ACCOUNT_KEY=your_base64_encoded_service_account_json

# Splunk Configuration (Optional)
SPLUNK_HOST=your_splunk_host
SPLUNK_PORT=8089
SPLUNK_TOKEN=your_splunk_api_token
SPLUNK_INDEX=your_splunk_index_name
```

### 3. Verify Setup
```bash
# Make scripts executable
chmod +x scripts/*.sh

# Test Docker access
docker run hello-world
```

## 🌍 Environment-Specific Deployment

### Development Environment

#### Quick Deploy
```bash
./scripts/deploy-dev.sh
```

#### Manual Deploy
```bash
# Set environment variables
export DB_PASSWORD=devpassword123
export JWT_SECRET=devjwtsecretkey123456789

# Deploy development environment
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml up -d

# Verify deployment
docker ps | grep cloud-integration-dev
curl -I http://localhost:3001
curl -I http://localhost:8081/api/cloud/health
```

#### Development Configuration
- **Frontend**: http://localhost:3001
- **Backend**: http://localhost:8081/api
- **Database**: localhost:5432
- **Redis**: localhost:6379
- **Authentication**: Anonymous (no auth required)
- **Logging**: Debug level enabled

### QA Environment

#### Quick Deploy
```bash
./scripts/deploy-qa.sh
```

#### Manual Deploy
```bash
# Set environment variables
export DB_PASSWORD=qapassword123
export JWT_SECRET=qajwtsecretkey123456789

# Deploy QA environment
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml up -d

# Verify deployment
docker ps | grep cloud-integration-qa
curl -I http://localhost:3002
curl -I http://localhost:8082/api/cloud/health
```

#### QA Configuration
- **Frontend**: http://localhost:3002
- **Backend**: http://localhost:8082/api
- **Database**: localhost:5433
- **Redis**: localhost:6380
- **Authentication**: Anonymous (no auth required)
- **Azure Integration**: Disabled
- **Logging**: Info level

### Production Environment

#### Quick Deploy
```bash
./scripts/deploy-prod.sh
```

#### Manual Deploy
```bash
# Set environment variables
export DB_PASSWORD=prodpassword123
export JWT_SECRET=prodjwtsecretkey123456789

# Deploy production environment
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml up -d

# Verify deployment
docker ps | grep cloud-integration-prod
curl -I http://localhost:3003
curl -I http://localhost:8083/api/cloud/health
```

#### Production Configuration
- **Frontend**: http://localhost:3003
- **Backend**: http://localhost:8083/api
- **Database**: localhost:5434
- **Redis**: localhost:6381
- **Authentication**: OAuth2 (requires JWT token)
- **All Integrations**: Enabled
- **Logging**: Warning level

## 🎯 All Environments Deployment

### Single Command Deployment
```bash
# Deploy all environments simultaneously
./scripts/run-all-environments.sh
```

This command will:
1. Deploy Development environment (ports 3001, 8081, 5432, 6379)
2. Deploy QA environment (ports 3002, 8082, 5433, 6380)
3. Deploy Production environment (ports 3003, 8083, 5434, 6381)
4. Verify all services are healthy
5. Display status and access URLs

### Manual All Environments
```bash
# Start all environments
./scripts/deploy-dev.sh &
./scripts/deploy-qa.sh &
./scripts/deploy-prod.sh &

# Wait for completion
wait

# Check status
docker ps | grep cloud-integration
```

## 🔍 Verification & Testing

### Health Checks
```bash
# Check all frontend services
curl -I http://localhost:3001 && echo "Dev Frontend: ✅"
curl -I http://localhost:3002 && echo "QA Frontend: ✅"
curl -I http://localhost:3003 && echo "Prod Frontend: ✅"

# Check all backend services
curl -I http://localhost:8081/api/cloud/health && echo "Dev Backend: ✅"
curl -I http://localhost:8082/api/cloud/health && echo "QA Backend: ✅"
curl -I http://localhost:8083/api/cloud/health && echo "Prod Backend: ✅"
```

### Container Status
```bash
# List all running containers
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep cloud-integration

# Check container health
docker ps --filter "health=healthy" | grep cloud-integration
```

### Log Verification
```bash
# Check backend logs
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs backend
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml logs backend
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml logs backend

# Check frontend logs
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs frontend
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml logs frontend
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml logs frontend
```

## 🛠️ Management Commands

### Starting Services
```bash
# Start specific environment
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml start
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml start
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml start

# Start all environments
./scripts/run-all-environments.sh
```

### Stopping Services
```bash
# Stop specific environment
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml stop
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml stop
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml stop

# Stop all environments
./scripts/stop-all.sh
```

### Restarting Services
```bash
# Restart specific environment
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml restart
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml restart
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml restart

# Restart specific service
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml restart backend
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml restart frontend
```

### Viewing Logs
```bash
# View logs for specific environment
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs -f

# View logs for specific service
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs -f backend
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs -f frontend

# View logs with timestamps
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml logs -f -t
```

### Scaling Services
```bash
# Scale backend service (if supported)
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml up -d --scale backend=2

# Scale frontend service (if supported)
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml up -d --scale frontend=2
```

## 🔧 Troubleshooting

### Common Issues

#### 1. Port Conflicts
```bash
# Check what's using the port
lsof -i :3001
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or stop all environments
./scripts/stop-all.sh
```

#### 2. Docker Build Failures
```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml build --no-cache

# Check Docker disk space
docker system df
```

#### 3. Database Connection Issues
```bash
# Check database container
docker logs cloud-integration-dev-postgres

# Test database connection
docker exec -it cloud-integration-dev-postgres psql -U dev_user -d cloudintegration_dev

# Restart database
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml restart postgres
```

#### 4. Memory Issues
```bash
# Check memory usage
docker stats

# Increase Docker memory limit
# Docker Desktop -> Settings -> Resources -> Memory

# Check system memory
free -h  # Linux
vm_stat  # macOS
```

#### 5. Network Issues
```bash
# Check Docker networks
docker network ls

# Inspect network
docker network inspect cloud-integration-dev_cloud-network-dev

# Recreate network
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml down
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml up -d
```

### Debug Mode

#### Enable Debug Logging
```bash
# Backend debug
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml exec backend java -Dlogging.level.com.example.cloudintegrationapp=DEBUG -jar app.jar

# Frontend debug
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml exec frontend nginx -T
```

#### Container Debugging
```bash
# Access container shell
docker exec -it cloud-integration-dev-backend sh
docker exec -it cloud-integration-dev-frontend sh

# Check container environment
docker exec cloud-integration-dev-backend env
docker exec cloud-integration-dev-backend ps aux
```

## 📊 Monitoring & Maintenance

### Health Monitoring
```bash
# Continuous health check
watch -n 5 'curl -s -o /dev/null -w "%{http_code}" http://localhost:3001 && echo " Dev" && curl -s -o /dev/null -w "%{http_code}" http://localhost:3002 && echo " QA" && curl -s -o /dev/null -w "%{http_code}" http://localhost:3003 && echo " Prod"'
```

### Resource Monitoring
```bash
# Monitor resource usage
docker stats --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}"

# Monitor disk usage
docker system df
```

### Log Rotation
```bash
# Configure log rotation in docker-compose files
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

### Backup & Recovery
```bash
# Backup database
docker exec cloud-integration-dev-postgres pg_dump -U dev_user cloudintegration_dev > backup_dev.sql

# Backup volumes
docker run --rm -v cloud-integration-dev_postgres_dev_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .
```

## 🚀 Advanced Deployment

### Kubernetes Deployment
```bash
# Deploy to AKS
kubectl apply -f k8s/aks/namespace.yaml
kubectl apply -f k8s/aks/backend-deployment.yaml
kubectl apply -f k8s/aks/backend-service.yaml
kubectl apply -f k8s/aks/frontend-deployment.yaml
kubectl apply -f k8s/aks/ingress.yaml

# Deploy to GKE
kubectl apply -f k8s/gke/backend-deployment.yaml
```

### CI/CD Integration
```bash
# Example CI/CD pipeline
# 1. Build and test
mvn clean test
npm test

# 2. Build Docker images
docker build -f Dockerfile.backend -t cloud-integration-backend:$BUILD_NUMBER .
docker build -f ../cloud-integration-frontend/Dockerfile.frontend -t cloud-integration-frontend:$BUILD_NUMBER ../cloud-integration-frontend/

# 3. Deploy to environment
docker-compose -p cloud-integration-$ENVIRONMENT -f docker-compose.$ENVIRONMENT.yml up -d
```

### Environment Promotion
```bash
# Promote from dev to qa
docker-compose -p cloud-integration-dev -f docker-compose.dev.yml down
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml up -d

# Promote from qa to prod
docker-compose -p cloud-integration-qa -f docker-compose.qa.yml down
docker-compose -p cloud-integration-prod -f docker-compose.prod.yml up -d
```

## 📝 Deployment Checklist

### Pre-Deployment
- [ ] Environment variables configured
- [ ] Docker and Docker Compose installed
- [ ] Required ports available
- [ ] Sufficient disk space and memory
- [ ] Network connectivity verified

### During Deployment
- [ ] Scripts are executable
- [ ] Environment variables exported
- [ ] Docker images building successfully
- [ ] Containers starting without errors
- [ ] Health checks passing

### Post-Deployment
- [ ] All services accessible via URLs
- [ ] Frontend applications loading
- [ ] Backend APIs responding
- [ ] Database connections working
- [ ] Redis cache functioning
- [ ] Logs showing no errors

### Verification
- [ ] Development: http://localhost:3001 ✅
- [ ] QA: http://localhost:3002 ✅
- [ ] Production: http://localhost:3003 ✅
- [ ] All backend health checks passing
- [ ] All containers in healthy state

## 🆘 Emergency Procedures

### Quick Recovery
```bash
# Stop all environments
./scripts/stop-all.sh

# Clean up resources
docker system prune -f

# Restart all environments
./scripts/run-all-environments.sh
```

### Data Recovery
```bash
# Restore database from backup
docker exec -i cloud-integration-dev-postgres psql -U dev_user cloudintegration_dev < backup_dev.sql

# Restore volumes from backup
docker run --rm -v cloud-integration-dev_postgres_dev_data:/data -v $(pwd):/backup alpine tar xzf /backup/postgres_backup.tar.gz -C /data
```

### Complete Reset
```bash
# Stop and remove all containers
./scripts/stop-all.sh
docker system prune -a -f

# Remove volumes
docker volume prune -f

# Rebuild and redeploy
./scripts/run-all-environments.sh
```

This deployment guide provides comprehensive instructions for deploying and managing the Cloud Integration Application across all environments. Follow the steps carefully and refer to the troubleshooting section if you encounter any issues.
