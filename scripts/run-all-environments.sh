#!/bin/bash

# Script to run all environments simultaneously with different project names
# This avoids container name conflicts

set -e

# Add Docker to PATH if needed
export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"

echo "🚀 Starting all environments simultaneously..."

# Set environment variables
export DB_PASSWORD=prodpassword123
export JWT_SECRET=prodjwtsecretkey123456789

# Function to run environment with specific project name
run_environment() {
    local env=$1
    local project_name=$2
    local compose_file=$3
    
    echo "📦 Starting $env environment with project name: $project_name"
    
    # Stop any existing containers for this project
    docker compose -p $project_name -f $compose_file down 2>/dev/null || true
    
    # Start the environment
    docker compose -p $project_name -f $compose_file up -d
    
    echo "✅ $env environment started"
}

# Start all environments in parallel
echo "Starting Development Environment..."
run_environment "Development" "cloud-integration-dev" "docker-compose.dev.yml" &

echo "Starting QA Environment..."
run_environment "QA" "cloud-integration-qa" "docker-compose.qa.yml" &

echo "Starting Production Environment..."
run_environment "Production" "cloud-integration-prod" "docker-compose.prod.yml" &

# Wait for all background processes to complete
wait

echo ""
echo "🎉 All environments started successfully!"
echo ""
echo "📊 Checking service status..."

# Show all running containers
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep cloud-integration

echo ""
echo "🌐 Application URLs:"
echo "   Development Frontend: http://localhost:3001"
echo "   Development Backend:  http://localhost:8081/api"
echo "   QA Frontend:         http://localhost:3002"
echo "   QA Backend:          http://localhost:8082/api"
echo "   Production Frontend: http://localhost:3003"
echo "   Production Backend:  http://localhost:8083/api"
echo ""
echo "📝 Useful commands:"
echo "   View logs:     docker-compose -p cloud-integration-[env] -f docker-compose.[env].yml logs -f [service]"
echo "   Stop all:      ./scripts/stop-all.sh"
echo "   Stop specific: docker-compose -p cloud-integration-[env] -f docker-compose.[env].yml down"
