#!/bin/bash

# Build and Deploy Todo Task Manager Backend using Terraform
# This script builds the Java application and deploys it using Terraform

set -e

# Configuration
ENVIRONMENT=${1:-dev}
AWS_REGION=${2:-us-east-1}
BUCKET_NAME="${ENVIRONMENT}-todo-lambda-artifacts"

echo "Starting Terraform deployment for environment: $ENVIRONMENT"
echo "AWS Region: $AWS_REGION"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if Terraform is installed
if ! command -v terraform &> /dev/null; then
    echo "Error: Terraform is not installed. Please install it first."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install it first."
    exit 1
fi

# Navigate to project directory
cd "$(dirname "$0")/.."

# Build the Java application
echo "Building Java application..."
mvn clean package -q

# Check if JAR file was created
JAR_FILE="target/todo-backend-java-1.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    exit 1
fi

# Create S3 bucket if it doesn't exist
echo "Creating S3 bucket if it doesn't exist..."
aws s3api head-bucket --bucket "$BUCKET_NAME" --region "$AWS_REGION" 2>/dev/null || \
aws s3 mb "s3://$BUCKET_NAME" --region "$AWS_REGION"

# Upload JAR to S3
echo "Uploading JAR file to S3..."
aws s3 cp "$JAR_FILE" "s3://$BUCKET_NAME/todo-backend-java.jar"

# Change to infrastructure directory
cd infrastructure

# Check if terraform.tfvars exists
if [ ! -f "terraform.tfvars" ]; then
    echo "Error: terraform.tfvars file not found"
    echo "Please copy terraform.tfvars.example to terraform.tfvars and customize it."
    exit 1
fi

# Initialize Terraform
echo "Initializing Terraform..."
terraform init

# Plan deployment
echo "Planning Terraform deployment..."
terraform plan

# Apply deployment
echo "Applying Terraform deployment..."
terraform apply -auto-approve

# Get outputs
echo "Getting Terraform outputs..."
API_URL=$(terraform output -raw api_gateway_url)
USERS_TABLE=$(terraform output -raw users_table_name)
TASKS_TABLE=$(terraform output -raw tasks_table_name)

echo ""
echo "========================================"
echo "Terraform deployment completed successfully!"
echo "========================================"
echo "API Gateway URL: $API_URL"
echo "Users Table: $USERS_TABLE"
echo "Tasks Table: $TASKS_TABLE"
echo ""
echo "Test endpoints:"
echo "  Register: POST $API_URL/auth/register"
echo "  Login:    POST $API_URL/auth/login"
echo "  Tasks:    GET  $API_URL/tasks (requires Authorization header)"
echo ""
echo "Environment: $ENVIRONMENT"
echo "Region: $AWS_REGION"
echo "========================================"
