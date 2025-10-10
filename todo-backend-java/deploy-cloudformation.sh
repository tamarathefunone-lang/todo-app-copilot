#!/bin/bash

# Build and Deploy Todo Task Manager Backend
# This script builds the Java application and deploys it using CloudFormation

set -e

# Configuration
ENVIRONMENT=${1:-dev}
AWS_REGION=${2:-us-east-1}
STACK_NAME="todo-backend-${ENVIRONMENT}"
BUCKET_NAME="${ENVIRONMENT}-todo-lambda-artifacts"

echo "Starting deployment for environment: $ENVIRONMENT"
echo "AWS Region: $AWS_REGION"
echo "Stack Name: $STACK_NAME"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install it first."
    exit 1
fi

# Build the Java application
echo "Building Java application..."
mvn clean package -q

# Check if JAR file was created
JAR_FILE="target/todo-backend.jar"
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

# Check if parameter file exists
PARAMS_FILE="infrastructure/cloudformation-params.json"
if [ ! -f "$PARAMS_FILE" ]; then
    echo "Error: Parameter file not found at $PARAMS_FILE"
    echo "Please copy cloudformation-params.example.json to cloudformation-params.json and customize it."
    exit 1
fi

# Deploy CloudFormation stack
echo "Deploying CloudFormation stack..."
aws cloudformation deploy \
    --template-file infrastructure/cloudformation-template.yaml \
    --stack-name "$STACK_NAME" \
    --parameter-overrides file://"$PARAMS_FILE" \
    --capabilities CAPABILITY_NAMED_IAM \
    --region "$AWS_REGION"

# Get stack outputs
echo "Getting stack outputs..."
API_URL=$(aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION" \
    --query 'Stacks[0].Outputs[?OutputKey==`ApiGatewayUrl`].OutputValue' \
    --output text)

USERS_TABLE=$(aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION" \
    --query 'Stacks[0].Outputs[?OutputKey==`UsersTableName`].OutputValue' \
    --output text)

TASKS_TABLE=$(aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION" \
    --query 'Stacks[0].Outputs[?OutputKey==`TasksTableName`].OutputValue' \
    --output text)

echo ""
echo "========================================"
echo "Deployment completed successfully!"
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
echo "Stack: $STACK_NAME"
echo "========================================"
