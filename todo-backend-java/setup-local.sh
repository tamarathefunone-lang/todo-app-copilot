#!/bin/bash

# Local Development Setup Script
# This script sets up the local development environment

set -e

echo "Setting up Todo Task Manager Backend for local development..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 11 or later."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "Error: Java 11 or later is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven 3.6 or later."
    exit 1
fi

# Navigate to project directory
cd "$(dirname "$0")"

# Build the project
echo "Building the project..."
mvn clean compile

# Run tests
echo "Running tests..."
mvn test

# Package the application
echo "Packaging the application..."
mvn package

echo ""
echo "========================================"
echo "Local development setup completed!"
echo "========================================"
echo "Project built successfully."
echo "JAR file: target/todo-backend-java-1.0-SNAPSHOT.jar"
echo ""
echo "To deploy to AWS:"
echo "  1. Configure AWS credentials: aws configure"
echo "  2. Copy and customize parameter files:"
echo "     - infrastructure/terraform.tfvars.example → terraform.tfvars"
echo "     - infrastructure/cloudformation-params.example.json → cloudformation-params.json"
echo "  3. Run deployment script:"
echo "     - CloudFormation: ./deploy-cloudformation.sh [environment] [region]"
echo "     - Terraform:      ./deploy-terraform.sh [environment] [region]"
echo ""
echo "Example:"
echo "  ./deploy-terraform.sh dev us-east-1"
echo "========================================"
