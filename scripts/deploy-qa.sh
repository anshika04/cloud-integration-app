#!/bin/bash

# QA Environment Deployment Script
set -e

echo "🚀 Deploying to QA Environment..."

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

# QA environment uses default values, no need to check environment variables

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Create necessary directories
print_status "Creating necessary directories..."
mkdir -p logs/backend-qa
mkdir -p logs/frontend-qa

# Stop existing containers
print_status "Stopping existing QA containers..."
docker-compose -f docker-compose.qa.yml down --remove-orphans

# Remove old images (optional)
if [ "$1" = "--clean" ]; then
    print_warning "Cleaning up old images..."
    docker system prune -f
fi

# Build and start services
print_status "Building and starting QA services..."
docker-compose -f docker-compose.qa.yml up --build -d

# Wait for services to be healthy
print_status "Waiting for services to be healthy..."
sleep 15

# Check service health
print_status "Checking service health..."
if curl -f http://localhost:8082/api/cloud/health > /dev/null 2>&1; then
    print_status "✅ Backend service is healthy"
else
    print_error "❌ Backend service health check failed"
    docker-compose -f docker-compose.qa.yml logs backend
    exit 1
fi

if curl -f http://localhost:3002/ > /dev/null 2>&1; then
    print_status "✅ Frontend service is healthy"
else
    print_error "❌ Frontend service health check failed"
    docker-compose -f docker-compose.qa.yml logs frontend
    exit 1
fi

# Run basic smoke tests
print_status "Running smoke tests..."
if curl -f http://localhost:8082/api/cloud/test > /dev/null 2>&1; then
    print_status "✅ API smoke test passed"
else
    print_warning "⚠️  API smoke test failed"
fi

# Show running containers
print_status "QA environment deployed successfully!"
echo ""
echo "📊 Running containers:"
docker-compose -f docker-compose.qa.yml ps

echo ""
echo "🌐 Application URLs:"
echo "   Frontend: http://localhost:3002"
echo "   Backend:  http://localhost:8082/api"
echo "   Health:   http://localhost:8082/api/cloud/health"
echo ""
echo "📝 Useful commands:"
echo "   View logs:     docker-compose -f docker-compose.qa.yml logs -f [service]"
echo "   Stop services: docker-compose -f docker-compose.qa.yml down"
echo "   Restart:       docker-compose -f docker-compose.qa.yml restart [service]"
