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

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

echo "🚀 Starting simultaneous deployment of all environments..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Function to deploy an environment
deploy_environment() {
    local env=$1
    local script="scripts/deploy-${env}.sh"
    
    if [ ! -f "$script" ]; then
        print_error "Deployment script for $env environment not found: $script"
        return 1
    fi
    
    print_status "Starting deployment of $env environment..."
    
    # Make script executable
    chmod +x "$script"
    
    # Run deployment in background
    "$script" > "logs/deploy-${env}.log" 2>&1 &
    local pid=$!
    
    # Store PID for later use
    echo "PID_${env}=$pid" >> "logs/deploy-pids.tmp"
    
    echo $pid
}

# Create logs directory
mkdir -p logs

# Clear PID file
> logs/deploy-pids.tmp

# Start all deployments in parallel
print_status "Starting parallel deployments..."

dev_pid=$(deploy_environment "dev")
qa_pid=$(deploy_environment "qa")

# For production, check if .env.prod exists
if [ -f ".env.prod" ]; then
    prod_pid=$(deploy_environment "prod")
    prod_deployed=true
else
    print_warning "Production environment file (.env.prod) not found. Skipping production deployment."
    print_info "To deploy production, create .env.prod based on env.template"
    prod_deployed=false
fi

print_status "Deployment PIDs:"
echo "   Dev: $dev_pid"
echo "   QA:  $qa_pid"
if [ "$prod_deployed" = true ]; then
    echo "   Prod: $prod_pid"
fi

echo ""

# Function to wait for deployment and show results
wait_for_deployment() {
    local env=$1
    local pid=$2
    
    print_status "Waiting for $env deployment (PID: $pid) to complete..."
    
    if wait $pid; then
        print_success "✅ $env environment deployed successfully!"
        
        # Show deployment logs
        echo ""
        print_info "📋 $env deployment logs:"
        echo "----------------------------------------"
        tail -20 "logs/deploy-${env}.log"
        echo "----------------------------------------"
        echo ""
    else
        print_error "❌ $env environment deployment failed!"
        
        # Show error logs
        echo ""
        print_error "📋 $env deployment error logs:"
        echo "----------------------------------------"
        tail -20 "logs/deploy-${env}.log"
        echo "----------------------------------------"
        echo ""
        
        return 1
    fi
}

# Wait for all deployments to complete
deployments_failed=0

wait_for_deployment "dev" $dev_pid || deployments_failed=$((deployments_failed + 1))
wait_for_deployment "qa" $qa_pid || deployments_failed=$((deployments_failed + 1))

if [ "$prod_deployed" = true ]; then
    wait_for_deployment "prod" $prod_pid || deployments_failed=$((deployments_failed + 1))
fi

echo ""
echo "🎯 Deployment Summary"
echo "===================="

if [ $deployments_failed -eq 0 ]; then
    print_success "All deployments completed successfully!"
else
    print_warning "$deployments_failed deployment(s) failed. Check the logs above for details."
fi

echo ""
echo "🌐 Environment URLs:"
echo "   Development:"
echo "     Frontend: http://localhost:3001"
echo "     Backend:  http://localhost:8081/api"
echo "     Database: localhost:5432"
echo ""
echo "   QA:"
echo "     Frontend: http://localhost:3002"
echo "     Backend:  http://localhost:8082/api"
echo "     Database: localhost:5433"
echo ""

if [ "$prod_deployed" = true ]; then
    echo "   Production:"
    echo "     Frontend: http://localhost (Load Balancer)"
    echo "     Backend:  http://localhost:8083/api (direct)"
    echo "     Database: localhost:5434"
    echo ""
fi

echo "📝 Useful Commands:"
echo "   View all containers: docker ps"
echo "   View logs: docker-compose -f docker-compose.<env>.yml logs -f [service]"
echo "   Stop all: ./scripts/stop-all.sh"
echo ""

if [ $deployments_failed -gt 0 ]; then
    exit 1
else
    print_success "🎉 All environments are now running!"
fi
