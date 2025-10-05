#!/bin/bash

# Master Deployment Script for Cloud Integration Platform
set -e

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

# Function to show usage
show_usage() {
    echo "Usage: $0 <environment> [options]"
    echo ""
    echo "Environments:"
    echo "  dev     - Development environment"
    echo "  qa      - QA environment"
    echo "  prod    - Production environment"
    echo ""
    echo "Options:"
    echo "  --clean    - Clean up old images before deployment"
    echo "  --force    - Force deployment (for production)"
    echo "  --help     - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 dev"
    echo "  $0 qa --clean"
    echo "  $0 prod --force"
}

# Check if environment is provided
if [ $# -eq 0 ]; then
    print_error "No environment specified"
    show_usage
    exit 1
fi

ENVIRONMENT=$1
shift

# Parse additional options
CLEAN=false
FORCE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN=true
            shift
            ;;
        --force)
            FORCE=true
            shift
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate environment
case $ENVIRONMENT in
    dev)
        SCRIPT="./scripts/deploy-dev.sh"
        ;;
    qa)
        SCRIPT="./scripts/deploy-qa.sh"
        ;;
    prod)
        SCRIPT="./scripts/deploy-prod.sh"
        ;;
    *)
        print_error "Invalid environment: $ENVIRONMENT"
        show_usage
        exit 1
        ;;
esac

# Check if script exists
if [ ! -f "$SCRIPT" ]; then
    print_error "Deployment script not found: $SCRIPT"
    exit 1
fi

# Make script executable
chmod +x "$SCRIPT"

print_info "🚀 Starting deployment to $ENVIRONMENT environment..."

# Prepare arguments for the specific deployment script
ARGS=""
if [ "$CLEAN" = true ]; then
    ARGS="$ARGS --clean"
fi
if [ "$FORCE" = true ]; then
    ARGS="$ARGS --force"
fi

# Execute the specific deployment script
if [ -n "$ARGS" ]; then
    $SCRIPT $ARGS
else
    $SCRIPT
fi

print_status "✅ Deployment to $ENVIRONMENT environment completed successfully!"
