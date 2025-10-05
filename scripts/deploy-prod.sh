#!/bin/bash

# Production Environment Deployment Script
set -e

echo "🚀 Deploying to Production Environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Check if required environment variables are set
required_vars=("DB_PASSWORD" "JWT_SECRET")
missing_vars=()

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        missing_vars+=("$var")
    fi
done

if [ ${#missing_vars[@]} -ne 0 ]; then
    print_error "Missing required environment variables: ${missing_vars[*]}"
    print_info "Please set the following environment variables:"
    for var in "${missing_vars[@]}"; do
        echo "  export $var=<value>"
    done
    exit 1
fi

# Confirmation prompt for production deployment
if [ "$1" != "--force" ]; then
    print_warning "⚠️  This will deploy to PRODUCTION environment!"
    read -p "Are you sure you want to continue? (yes/no): " confirm
    if [ "$confirm" != "yes" ]; then
        print_info "Deployment cancelled."
        exit 0
    fi
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Create necessary directories
print_status "Creating necessary directories..."
mkdir -p logs/backend-prod
mkdir -p logs/frontend-prod
mkdir -p environments/prod/ssl
mkdir -p environments/prod/redis
mkdir -p environments/prod/nginx

# Create backup of current deployment (if exists)
if docker-compose -f docker-compose.prod.yml ps | grep -q "Up"; then
    print_status "Creating backup of current deployment..."
    timestamp=$(date +%Y%m%d_%H%M%S)
    docker-compose -f docker-compose.prod.yml down --remove-orphans
    print_status "Backup created at timestamp: $timestamp"
fi

# Remove old images (optional)
if [ "$2" = "--clean" ]; then
    print_warning "Cleaning up old images..."
    docker system prune -f
fi

# Build and start services
print_status "Building and starting production services..."
docker-compose -f docker-compose.prod.yml up --build -d

# Wait for services to be healthy
print_status "Waiting for services to be healthy..."
sleep 30

# Check service health
print_status "Checking service health..."
max_retries=10
retry_count=0

while [ $retry_count -lt $max_retries ]; do
    if curl -f http://localhost:8080/api/cloud/health > /dev/null 2>&1; then
        print_status "✅ Backend service is healthy"
        break
    else
        retry_count=$((retry_count + 1))
        print_warning "Backend health check attempt $retry_count/$max_retries failed, retrying in 10 seconds..."
        sleep 10
    fi
done

if [ $retry_count -eq $max_retries ]; then
    print_error "❌ Backend service health check failed after $max_retries attempts"
    docker-compose -f docker-compose.prod.yml logs backend
    exit 1
fi

# Check frontend health
retry_count=0
while [ $retry_count -lt $max_retries ]; do
    if curl -f http://localhost/ > /dev/null 2>&1; then
        print_status "✅ Frontend service is healthy"
        break
    else
        retry_count=$((retry_count + 1))
        print_warning "Frontend health check attempt $retry_count/$max_retries failed, retrying in 10 seconds..."
        sleep 10
    fi
done

if [ $retry_count -eq $max_retries ]; then
    print_error "❌ Frontend service health check failed after $max_retries attempts"
    docker-compose -f docker-compose.prod.yml logs frontend
    exit 1
fi

# Run comprehensive smoke tests
print_status "Running comprehensive smoke tests..."
tests_passed=0
tests_failed=0

# Test API endpoints
if curl -f http://localhost:8083/api/cloud/health > /dev/null 2>&1; then
    print_status "✅ Health endpoint test passed"
    tests_passed=$((tests_passed + 1))
else
    print_warning "⚠️  Health endpoint test failed"
    tests_failed=$((tests_failed + 1))
fi

if curl -f http://localhost:8083/api/cloud/test > /dev/null 2>&1; then
    print_status "✅ Test endpoint test passed"
    tests_passed=$((tests_passed + 1))
else
    print_warning "⚠️  Test endpoint test failed"
    tests_failed=$((tests_failed + 1))
fi

# Test frontend
if curl -f http://localhost/ > /dev/null 2>&1; then
    print_status "✅ Frontend test passed"
    tests_passed=$((tests_passed + 1))
else
    print_warning "⚠️  Frontend test failed"
    tests_failed=$((tests_failed + 1))
fi

# Show test results
echo ""
print_info "Smoke test results: $tests_passed passed, $tests_failed failed"

if [ $tests_failed -gt 0 ]; then
    print_warning "Some tests failed, but deployment completed. Please investigate."
fi

# Show running containers
print_status "Production environment deployed successfully!"
echo ""
echo "📊 Running containers:"
docker-compose -f docker-compose.prod.yml ps

echo ""
echo "🌐 Application URLs:"
echo "   Frontend: http://localhost (via Load Balancer)"
echo "   Backend:  http://localhost:8083/api (direct)"
echo "   Health:   http://localhost:8083/api/cloud/health"
echo "   Load Balancer: http://localhost"
echo ""
echo "📝 Useful commands:"
echo "   View logs:     docker-compose -f docker-compose.prod.yml logs -f [service]"
echo "   Stop services: docker-compose -f docker-compose.prod.yml down"
echo "   Restart:       docker-compose -f docker-compose.prod.yml restart [service]"
echo "   Scale backend: docker-compose -f docker-compose.prod.yml up --scale backend=3 -d"
