# 📚 Todo Task Manager - Complete Setup & Deployment Guide

This comprehensive guide walks you through setting up, running, and deploying your full-stack Todo Task Manager application.

## 📋 Table of Contents

1. [Project Overview](#-project-overview)
2. [Prerequisites](#-prerequisites)
3. [Frontend Setup (React)](#-frontend-setup-react)
4. [Backend Setup (Java)](#-backend-setup-java)
5. [AWS Deployment](#-aws-deployment)
6. [Git Version Control](#-git-version-control)
7. [API Documentation (Swagger)](#-api-documentation-swagger)
8. [Testing & Validation](#-testing--validation)
9. [Troubleshooting](#-troubleshooting)
10. [Production Deployment](#-production-deployment)

## 🏗️ Project Overview

**Architecture:**
```
React Frontend (Port 3000) 
    ↓ HTTP Requests
AWS API Gateway 
    ↓ Invokes
Lambda Functions (Java) 
    ↓ Reads/Writes
DynamoDB Tables
```

**Components:**
- **Frontend**: React.js application with authentication and task management
- **Backend**: Java serverless functions on AWS Lambda
- **Database**: DynamoDB for user and task data
- **Authentication**: JWT tokens with BCrypt password hashing
- **API**: RESTful endpoints with CORS support

---

## 🔧 Prerequisites

### Required Software

```bash
# Check if you have these installed:

# 1. Node.js (for React frontend)
node --version          # Should be 16.x or higher
npm --version           # Should be 8.x or higher

# 2. Java (for backend development)
java --version          # Should be Java 11 or 17

# 3. Maven (for Java build management)
mvn --version           # Should be 3.6 or higher

# 4. AWS CLI (for cloud deployment)
aws --version           # Should be 2.x

# 5. Terraform (for infrastructure)
terraform --version     # Should be 1.0 or higher

# 6. Git (for version control)
git --version           # Any recent version

# 7. Python (for documentation server)
python3 --version       # Should be 3.7 or higher
```

### Installation Commands

If any are missing, install them:

```bash
# macOS (using Homebrew)
brew install node          # Installs Node.js and npm
brew install openjdk@17     # Installs Java 17
brew install maven          # Installs Maven
brew install awscli         # Installs AWS CLI
brew install terraform      # Installs Terraform

# Verify installations
which node && which java && which mvn && which aws && which terraform
```

---

## 🎨 Frontend Setup (React)

### 1. Navigate to Frontend Directory

```bash
cd /path/to/SummerProject-CoPilot/todo-task-manager
```
**What it does**: Changes directory to the React application folder

### 2. Install Dependencies

```bash
npm install
```
**What it does**: 
- Reads `package.json` file
- Downloads all required JavaScript libraries (React, React Router, Axios, etc.)
- Creates `node_modules` folder with dependencies
- Creates `package-lock.json` for version locking

### 3. Install Additional Packages

```bash
npm install react-router-dom axios
```
**What it does**:
- `react-router-dom`: Enables client-side routing (navigation between pages)
- `axios`: HTTP client for making API calls to the backend

### 4. Configure Backend URL

Edit `src/services/api.js`:
```javascript
// For local development
const API_BASE_URL = 'http://localhost:8080/api';

// For AWS deployment
const API_BASE_URL = 'https://your-api-gateway-url.amazonaws.com/dev';
```
**What it does**: Sets the base URL for all API calls to your backend

### 5. Start Development Server

```bash
npm start
```
**What it does**:
- Starts Webpack development server
- Compiles React code
- Opens browser at `http://localhost:3000`
- Enables hot reloading (auto-refresh on code changes)
- Shows compilation errors in terminal and browser

### 6. Build for Production

```bash
npm run build
```
**What it does**:
- Creates optimized production build
- Minifies and compresses all files
- Generates `build/` folder
- Ready for deployment to web servers

### 7. Run Tests

```bash
npm test
```
**What it does**:
- Runs Jest test runner
- Executes all `.test.js` files
- Shows test results and coverage
- Watches for changes in test mode

---

## ☕ Backend Setup (Java)

### 1. Navigate to Backend Directory

```bash
cd /path/to/SummerProject-CoPilot/todo-backend-java
```

### 2. Clean Previous Builds

```bash
mvn clean
```
**What it does**:
- Deletes the `target/` directory
- Removes all compiled classes and JAR files
- Ensures fresh build environment

### 3. Compile and Package

```bash
mvn package
```
**What it does**:
- Compiles all Java source code in `src/main/java/`
- Runs unit tests in `src/test/java/`
- Creates JAR files in `target/` directory
- `todo-backend.jar` (18MB) - Fat JAR with all dependencies
- `todo-backend-java-1.0.0.jar` (61KB) - Slim JAR without dependencies

### 4. Skip Tests (if needed)

```bash
mvn package -DskipTests
```
**What it does**:
- Compiles and packages without running tests
- Useful when tests are failing but you need to build
- Faster build process

### 5. Run Specific Maven Goals

```bash
mvn compile                    # Only compile, don't package
mvn test                      # Run tests only
mvn clean compile test package # Full build process step by step
```

### 6. Check Dependencies

```bash
mvn dependency:tree
```
**What it does**:
- Shows all project dependencies in tree format
- Helps identify version conflicts
- Useful for debugging classpath issues

### 7. Local Testing Setup

```bash
./setup-local.sh
```
**What it does**:
- Sets up local DynamoDB (if using DynamoDB Local)
- Creates necessary tables
- Configures environment variables

---

## ☁️ AWS Deployment

### Pre-Deployment: Reminder Services Setup

#### 📧 Set Up AWS SES (Simple Email Service)

**1. Verify Email Address for Reminders**
```bash
# Replace with your email address
aws ses verify-email-identity --email-address your-email@domain.com
```
**What it does**:
- Registers your email with AWS SES
- Sends verification email to your inbox
- Required for sending email reminders

**2. Check Verification Status**
```bash
aws ses list-verified-email-addresses
```
**What it does**:
- Shows all verified email addresses
- Confirms your email is ready for sending

**3. Update Terraform Variables**
```bash
# Edit terraform.tfvars file
echo 'reminder_sender_email = "your-verified-email@domain.com"' >> terraform.tfvars
```

#### 📱 Set Up AWS SNS (Simple Notification Service)

**1. Verify SMS Permissions**
```bash
aws sns get-sms-attributes
```
**What it does**:
- Shows current SMS sending limits
- Verifies SNS is available in your region

**2. Request SMS Spending Limit Increase (if needed)**
- Go to AWS Console → SNS → Text messaging (SMS) → Preferences
- Request limit increase for production use
- Default limit: $1/month (about 100 messages)

### 1. Configure AWS Credentials

```bash
aws configure
```
**What it does**:
- Prompts for AWS Access Key ID
- Prompts for AWS Secret Access Key
- Sets default region (e.g., us-east-1)
- Stores credentials in `~/.aws/credentials`

### 2. Verify AWS Access

```bash
aws sts get-caller-identity
```
**What it does**:
- Shows your AWS account ID, user ARN, and user ID
- Confirms AWS CLI is properly configured
- Verifies you have valid credentials

### 3. Grant Required Permissions

```bash
aws iam attach-user-policy --user-name YOUR_USERNAME --policy-arn arn:aws:iam::aws:policy/AdministratorAccess
```
**What it does**:
- Grants full AWS permissions to your user
- Required for creating Lambda functions, API Gateway, DynamoDB tables
- **Note**: Use least-privilege in production

### 4. Initialize Terraform

```bash
cd todo-backend-java/infrastructure
terraform init
```
**What it does**:
- Downloads AWS provider plugins
- Creates `.terraform/` directory
- Creates `.terraform.lock.hcl` file
- Prepares Terraform workspace

### 5. Plan Deployment

```bash
terraform plan
```
**What it does**:
- Reads `main.tf` configuration file
- Compares desired state with current AWS resources
- Shows what resources will be created/modified/destroyed
- **Does not make any changes** - just shows the plan

### 6. Deploy Infrastructure

```bash
terraform apply
```
**What it does**:
- Executes the deployment plan
- Creates AWS resources:
  - 8 Lambda functions (auth and task handlers)
  - API Gateway with endpoints
  - DynamoDB tables (users and tasks)
  - IAM roles and policies
- Shows progress and final outputs (API URL, table names)

### 7. Auto-approve Deployment

```bash
terraform apply -auto-approve
```
**What it does**:
- Same as `terraform apply` but skips confirmation prompt
- Useful for automated deployments
- **Use carefully** - no chance to review changes

### 8. View Infrastructure Status

```bash
terraform show              # Show current state
terraform output            # Show output values
terraform state list       # List all resources
```

### 9. Destroy Infrastructure

```bash
terraform destroy
```
**What it does**:
- Removes all AWS resources created by Terraform
- **Permanent deletion** of Lambda functions, API Gateway, DynamoDB tables
- Useful for cleanup or cost savings
- Prompts for confirmation unless `-auto-approve` is used

### 10. Alternative CloudFormation Deployment

```bash
./deploy-cloudformation.sh
```
**What it does**:
- Uses AWS CloudFormation instead of Terraform
- Reads `cloudformation-template.yaml`
- Creates S3 bucket for deployment artifacts
- Uploads JAR file to S3
- Deploys CloudFormation stack

---

## 📂 Git Version Control

### 1. Initialize Repository

```bash
git init
```
**What it does**:
- Creates `.git` directory
- Initializes empty Git repository
- Enables version control for the project

### 2. Configure Git User

```bash
git config user.name "Your Name"
git config user.email "your.email@example.com"
```
**What it does**:
- Sets your identity for commit messages
- Required before making commits
- Can be set globally with `--global` flag

### 3. Add Files to Staging

```bash
git add .                           # Add all files
git add specific-file.js           # Add specific file
git add src/                       # Add entire directory
```
**What it does**:
- Stages files for commit
- Files must be staged before committing
- `.gitignore` file excludes certain files automatically

### 4. Check Status

```bash
git status
```
**What it does**:
- Shows which files are staged, unstaged, or untracked
- Displays current branch
- Shows if you're ahead/behind remote repository

### 5. Commit Changes

```bash
git commit -m "Initial commit: Todo Task Manager application"
```
**What it does**:
- Creates a snapshot of staged changes
- Requires a commit message describing changes
- Generates unique commit hash for reference

### 6. View Commit History

```bash
git log                            # Full commit history
git log --oneline                  # Condensed view
git log --graph                    # Visual branch structure
```

### 7. Connect to Remote Repository

```bash
git remote add origin https://github.com/username/repo-name.git
```
**What it does**:
- Links local repository to GitHub/remote repository
- `origin` is the default name for main remote
- Enables push/pull operations

### 8. Push to Remote

```bash
git push -u origin main            # First push, sets upstream
git push                          # Subsequent pushes
```
**What it does**:
- Uploads local commits to remote repository
- `-u` sets upstream tracking for the branch
- Makes code available on GitHub/remote platform

### 9. Pull from Remote

```bash
git pull
```
**What it does**:
- Downloads changes from remote repository
- Merges changes into current branch
- Keeps local repository synchronized

### 10. Branch Management

```bash
git branch feature-new-auth        # Create new branch
git checkout feature-new-auth      # Switch to branch
git checkout -b feature-ui         # Create and switch to new branch
git merge feature-new-auth         # Merge branch into current branch
```

### 11. Useful Git Commands

```bash
git diff                          # Show unstaged changes
git diff --staged                 # Show staged changes
git reset HEAD file.js            # Unstage file
git checkout -- file.js           # Discard changes to file
git stash                         # Temporarily save changes
git stash pop                     # Restore stashed changes
```

---

## 📖 API Documentation (Swagger)

### 1. Start Documentation Server

```bash
python3 serve-docs.py
```
**What it does**:
- Starts HTTP server on port 8080
- Serves Swagger UI interface
- Opens browser automatically
- Enables CORS for API testing

### 2. Access Documentation

- **Full Documentation**: http://localhost:8080/api-docs.html
- **Raw Swagger YAML**: http://localhost:8080/swagger.yaml

### 3. Alternative Simple Server

```bash
python3 -m http.server 8080
```
**What it does**:
- Starts basic HTTP server
- Serves files from current directory
- Manual navigation to `api-docs.html`

### 4. Using Different Port

```bash
# Edit serve-docs.py and change PORT = 8080 to desired port
PORT = 9000  # Use port 9000 instead
```

### 5. Generate OpenAPI Client

```bash
# Install OpenAPI Generator
npm install -g @openapitools/openapi-generator-cli

# Generate JavaScript client
openapi-generator-cli generate -i swagger.yaml -g javascript -o client/
```
**What it does**:
- Creates client SDK from Swagger definition
- Generates typed API calls
- Available for multiple programming languages

---

## 🧪 Testing & Validation

### 1. Test Backend Endpoints

```bash
# Test user registration
curl -X POST https://your-api-url/dev/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","name":"Test User"}'

# Test user login
curl -X POST https://your-api-url/dev/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Test task creation (requires JWT token)
curl -X POST https://your-api-url/dev/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"title":"Test Task","description":"Test Description","priority":"HIGH"}'

# Test task with email reminder
curl -X POST https://your-api-url/dev/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Email Reminder Test",
    "description": "Test task with email reminder",
    "priority": "HIGH",
    "dueDate": "2025-10-18T10:00:00Z",
    "reminderType": "EMAIL",
    "reminderTime": "2025-10-18T09:00:00Z"
  }'

# Test task with SMS reminder
curl -X POST https://your-api-url/dev/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "SMS Reminder Test",
    "description": "Test task with SMS reminder",
    "priority": "MEDIUM",
    "reminderType": "SMS",
    "reminderTime": "2025-10-18T14:00:00Z",
    "phoneNumber": "+1-555-123-4567"
  }'
```

### 2. Test Frontend Integration

```bash
# Start frontend development server
cd todo-task-manager
npm start

# Open browser and test:
# 1. Navigate to http://localhost:3000
# 2. Register new account
# 3. Login with credentials
# 4. Create, edit, delete tasks
# 5. Test reminder functionality:
#    - Create task with email reminder
#    - Create task with SMS reminder  
#    - Create task with browser alarm
#    - Verify reminder fields are saved
# 6. Test logout functionality
```

### 3. Check AWS Resources

```bash
# List Lambda functions
aws lambda list-functions --query 'Functions[?starts_with(FunctionName, `dev-todo`)]'

# List DynamoDB tables
# List DynamoDB tables
aws dynamodb list-tables --query 'TableNames[?starts_with(@, `dev-todo`)]'

# Check EventBridge rules (for reminders)
aws events list-rules --name-prefix "reminder-"

# Check SES verified emails
aws ses list-verified-email-addresses

# Check SNS topics
aws sns list-topics --query 'Topics[?contains(TopicArn, `sms-reminders`)]'
```

### 4. Test Reminder Functionality

```bash
# 1. Create a task with reminder set for 2 minutes in the future
# 2. Check EventBridge rules are created:
aws events list-rules --name-prefix "reminder-"

# 3. Check Lambda logs for reminder processing:
aws logs tail /aws/lambda/dev-todo-reminder-processor --follow

# 4. Verify reminder delivery:
# - Email: Check your email inbox
# - SMS: Check your phone for text message
# - Alarm: Check browser notifications when app is open

# 5. Verify reminder was marked as sent:
# - Get the task and check isReminderSent = true
```

# Get API Gateway details
aws apigateway get-rest-apis --query 'items[?name==`dev-todo-api`]'
```

### 4. Monitor Logs

```bash
# View Lambda function logs
aws logs describe-log-groups --log-group-name-prefix "/aws/lambda/dev-todo"

# Tail specific function logs
aws logs tail /aws/lambda/dev-todo-register --follow
```

---

## 🔧 Troubleshooting

### Common Frontend Issues

```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Check for port conflicts
lsof -ti:3000  # Shows process using port 3000
kill -9 $(lsof -ti:3000)  # Kill process on port 3000
```

### Common Backend Issues

```bash
# Check Java version compatibility
java -version
javac -version

# Verify Maven settings
mvn help:effective-settings

# Debug Maven build
mvn package -X  # Verbose output
mvn clean compile -Dmaven.compiler.debug=true
```

### AWS Issues

```bash
# Check AWS credentials
aws configure list

# Test AWS connectivity
aws sts get-caller-identity

# Check region configuration
aws configure get region

# Validate Terraform syntax
terraform validate
terraform fmt  # Format files
```

### Git Issues

```bash
# Reset to last commit
git reset --hard HEAD

# Fix merge conflicts
git status  # See conflicted files
# Edit files to resolve conflicts
git add .
git commit -m "Resolve merge conflicts"

# Undo last commit (keep changes)
git reset --soft HEAD~1
```

---

## 🚀 Production Deployment

### 1. Frontend Production Build

```bash
cd todo-task-manager

# Create optimized build
npm run build

# Serve production build locally (testing)
npx serve -s build -l 3000
```

### 2. Deploy Frontend to AWS S3 + CloudFront

```bash
# Create S3 bucket for frontend
aws s3 mb s3://your-todo-app-frontend

# Upload build files
aws s3 sync build/ s3://your-todo-app-frontend --delete

# Enable static website hosting
aws s3 website s3://your-todo-app-frontend --index-document index.html --error-document index.html
```

### 3. Environment-Specific Configuration

```bash
# Create production Terraform variables
cp terraform.tfvars.example terraform.prod.tfvars

# Edit production values
cat terraform.prod.tfvars
environment = "prod"
aws_region = "us-east-1" 
jwt_secret = "your-super-secure-production-secret"

# Deploy to production
terraform workspace new prod
terraform apply -var-file="terraform.prod.tfvars"
```

### 4. Domain and SSL Setup

```bash
# Register domain in Route53
aws route53 create-hosted-zone --name yourdomain.com --caller-reference $(date +%s)

# Request SSL certificate
aws acm request-certificate --domain-name yourdomain.com --validation-method DNS
```

### 5. Monitoring and Alerts

```bash
# Enable CloudWatch monitoring for Lambda
aws logs create-log-group --log-group-name /aws/lambda/prod-todo-api

# Set up CloudWatch alarms
aws cloudwatch put-metric-alarm --alarm-name "HighErrorRate" --alarm-description "Lambda error rate too high" --metric-name Errors --namespace AWS/Lambda --statistic Sum --period 300 --threshold 10 --comparison-operator GreaterThanThreshold
```

---

## 📝 Quick Reference Commands

### Daily Development Workflow

```bash
# 1. Start frontend development
cd todo-task-manager && npm start

# 2. Start documentation server
python3 serve-docs.py

# 3. Make code changes and test

# 4. Commit changes
git add .
git commit -m "Feature: Add task filtering"
git push

# 5. Deploy updates to AWS
cd todo-backend-java && mvn package
cd infrastructure && terraform apply
```

### Emergency Commands

```bash
# Stop all local servers
pkill -f "npm start"
pkill -f "serve-docs.py"

# Destroy AWS resources (cost savings)
terraform destroy -auto-approve

# Reset local repository
git reset --hard origin/main
git clean -fd
```

---

## 📞 Support and Resources

- **AWS Documentation**: https://docs.aws.amazon.com/
- **React Documentation**: https://reactjs.org/docs/
- **Maven Documentation**: https://maven.apache.org/guides/
- **Terraform Documentation**: https://www.terraform.io/docs/
- **Swagger/OpenAPI**: https://swagger.io/docs/

**Remember**: Always test changes in development before deploying to production! 🚀
