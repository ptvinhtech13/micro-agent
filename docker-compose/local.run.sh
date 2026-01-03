#!/bin/bash

# One-shot run script for local development environment
# Usage: ./local.run.sh [-d]
# -d flag runs docker-compose in detached/background mode

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Load environment variables
if [ -f versions.env ]; then
    export $(cat versions.env | grep -v '^#' | xargs)
fi

# Check if -d flag is provided
if [ "$1" == "-d" ]; then
    echo "Starting services in detached mode..."
    docker-compose -f docker-compose.local.yml up -d
    echo "Services started in background. Use 'docker-compose -f docker-compose.local.yml logs -f' to view logs."
else
    echo "Starting services in foreground mode..."
    docker-compose -f docker-compose.local.yml up
fi
