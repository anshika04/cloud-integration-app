#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo "🛑 Stopping all environment deployments..."
echo ""

# Function to stop an environment
stop_environment() {
    local env=$1
    local compose_file="docker-compose.${env}.yml"
    
    if [ ! -f "$compose_file" ]; then
        print_warning "Docker Compose file for $env environment not found: $compose_file"
        return 0
    fi
    
    print_status "Stopping $env environment..."
    
    if docker-compose -f "$compose_file" down --volumes --remove-orphans; then
        print_success "✅ $env environment stopped successfully"
    else
        print_warning "⚠️  Some issues stopping $env environment (containers may not exist)"
    fi
}

# Stop all environments
stop_environment "dev"
stop_environment "qa"
stop_environment "prod"

echo ""
print_status "Cleaning up unused Docker resources..."

# Remove unused networks
docker network prune -f

# Remove unused volumes (optional - be careful with this in production)
read -p "Do you want to remove unused volumes? This will delete all data! (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_warning "Removing unused volumes..."
    docker volume prune -f
else
    print_status "Skipping volume cleanup"
fi

echo ""
print_success "🎉 All environments stopped and cleaned up!"

echo ""
echo "📊 Remaining Docker resources:"
echo "   Containers: $(docker ps -q | wc -l | tr -d ' ') running"
echo "   Networks: $(docker network ls -q | wc -l | tr -d ' ') total"
echo "   Volumes: $(docker volume ls -q | wc -l | tr -d ' ') total"
echo ""
echo "📝 To restart environments:"
echo "   ./scripts/deploy-all.sh    # Deploy all environments"
echo "   ./scripts/deploy.sh dev    # Deploy specific environment"
echo ""
