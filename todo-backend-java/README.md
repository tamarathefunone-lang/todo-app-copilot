# Todo Task Manager - Java Backend

A serverless Java backend for a Todo Task Manager application built with AWS Lambda, API Gateway, and DynamoDB.

## Features

- **User Authentication**: JWT-based authentication with secure password hashing
- **Task Management**: Full CRUD operations for tasks (Create, Read, Update, Delete)
- **Task Filtering**: Filter tasks by status, priority, and creation date
- **Security**: Lambda authorizer for API Gateway with JWT validation
- **Serverless Architecture**: Built with AWS Lambda for scalability and cost-effectiveness
- **Infrastructure as Code**: Both CloudFormation and Terraform templates included

## Architecture

```
Frontend (React) → API Gateway → Lambda Authorizer → Lambda Functions → DynamoDB
```

### Components

- **API Gateway**: RESTful API endpoints with CORS support
- **Lambda Functions**: Serverless compute for business logic
- **DynamoDB**: NoSQL database for users and tasks
- **Lambda Authorizer**: JWT token validation for secured endpoints
- **IAM Roles**: Least-privilege security policies

## API Endpoints

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - User login

### Tasks (Authenticated)
- `GET /tasks` - List all tasks for authenticated user
- `POST /tasks` - Create a new task
- `GET /tasks/{taskId}` - Get specific task
- `PUT /tasks/{taskId}` - Update task
- `DELETE /tasks/{taskId}` - Delete task

### Query Parameters for Task Listing
- `status` - Filter by task status (TODO, IN_PROGRESS, COMPLETED)
- `priority` - Filter by priority (LOW, MEDIUM, HIGH)
- `sortBy` - Sort by field (createdAt, updatedAt, priority)
- `sortOrder` - Sort order (asc, desc)

## Project Structure

```
todo-backend-java/
├── src/main/java/com/todoapp/
│   ├── dto/                    # Data Transfer Objects
│   │   ├── ApiResponse.java
│   │   ├── AuthResponse.java
│   │   ├── CreateTaskRequest.java
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── UpdateTaskRequest.java
│   ├── lambda/                 # Lambda function handlers
│   │   ├── AuthorizerHandler.java
│   │   ├── CreateTaskHandler.java
│   │   ├── DeleteTaskHandler.java
│   │   ├── GetTaskHandler.java
│   │   ├── ListTasksHandler.java
│   │   ├── LoginHandler.java
│   │   ├── RegisterHandler.java
│   │   └── UpdateTaskHandler.java
│   ├── model/                  # Domain models
│   │   ├── Task.java
│   │   └── User.java
│   ├── repository/             # Data access layer
│   │   ├── TaskRepository.java
│   │   └── UserRepository.java
│   ├── service/                # Business logic
│   │   ├── AuthService.java
│   │   └── JwtService.java
│   └── util/                   # Utility classes
│       ├── LambdaUtils.java
│       └── ServiceFactory.java
├── infrastructure/             # Infrastructure as Code
│   ├── cloudformation-template.yaml
│   ├── main.tf
│   ├── terraform.tfvars.example
│   └── cloudformation-params.example.json
├── deploy-cloudformation.sh    # CloudFormation deployment script
├── deploy-terraform.sh         # Terraform deployment script
├── setup-local.sh             # Local development setup
├── pom.xml                    # Maven configuration
└── README.md
```

## Prerequisites

- **Java 11** or later
- **Maven 3.6** or later
- **AWS CLI** configured with appropriate credentials
- **Terraform** (if using Terraform deployment)
- **AWS Account** with permissions for:
  - Lambda functions
  - API Gateway
  - DynamoDB
  - IAM roles and policies
  - S3 (for deployment artifacts)

## Quick Start

### 1. Local Development Setup

```bash
# Clone and navigate to the project
cd todo-backend-java

# Run local setup script
./setup-local.sh
```

### 2. Configure Deployment Parameters

#### For Terraform:
```bash
# Copy and customize Terraform variables
cp infrastructure/terraform.tfvars.example infrastructure/terraform.tfvars

# Edit terraform.tfvars with your values
# - Generate JWT secret: openssl rand -base64 32
# - Set environment (dev/staging/prod)
# - Set AWS region
```

#### For CloudFormation:
```bash
# Copy and customize CloudFormation parameters
cp infrastructure/cloudformation-params.example.json infrastructure/cloudformation-params.json

# Edit cloudformation-params.json with your values
```

### 3. Deploy to AWS

#### Using Terraform (Recommended):
```bash
./deploy-terraform.sh [environment] [region]

# Example:
./deploy-terraform.sh dev us-east-1
```

#### Using CloudFormation:
```bash
./deploy-cloudformation.sh [environment] [region]

# Example:
./deploy-cloudformation.sh dev us-east-1
```

## Configuration

### Environment Variables

The Lambda functions use the following environment variables:

- `USERS_TABLE`: DynamoDB users table name
- `TASKS_TABLE`: DynamoDB tasks table name
- `JWT_SECRET`: Secret key for JWT token signing
- `ENVIRONMENT`: Deployment environment (dev/staging/prod)

### DynamoDB Tables

#### Users Table
- **Primary Key**: `email` (String)
- **Attributes**: `email`, `password`, `name`, `createdAt`

#### Tasks Table
- **Primary Key**: `userId` (Hash), `taskId` (Range)
- **Global Secondary Indexes**:
  - `StatusIndex`: `userId` + `status`
  - `PriorityIndex`: `userId` + `priority`
  - `CreatedAtIndex`: `userId` + `createdAt`
- **Attributes**: `userId`, `taskId`, `title`, `description`, `status`, `priority`, `dueDate`, `createdAt`, `updatedAt`

## Testing

### Unit Tests
```bash
mvn test
```

### API Testing with curl

1. **Register a user:**
```bash
curl -X POST https://your-api-url/dev/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test User"
  }'
```

2. **Login:**
```bash
curl -X POST https://your-api-url/dev/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

3. **Create a task:**
```bash
curl -X POST https://your-api-url/dev/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Complete project",
    "description": "Finish the todo app",
    "priority": "HIGH",
    "dueDate": "2024-12-31"
  }'
```

4. **List tasks:**
```bash
curl -X GET https://your-api-url/dev/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Security

### JWT Authentication
- Tokens expire after 24 hours
- Secure secret key (minimum 32 characters)
- Token validation on every protected endpoint

### Password Security
- BCrypt hashing with salt rounds
- No plaintext password storage

### AWS Security
- Least-privilege IAM roles
- API Gateway authorizer for token validation
- VPC endpoints for DynamoDB (optional)

## Monitoring and Logging

### CloudWatch Logs
- All Lambda functions log to CloudWatch
- Structured logging with correlation IDs
- Error tracking and debugging information

### CloudWatch Metrics
- Lambda function metrics (duration, errors, invocations)
- API Gateway metrics (latency, 4xx/5xx errors)
- DynamoDB metrics (read/write capacity, throttling)

## Deployment Environments

### Development (`dev`)
- Lower resource allocation
- Relaxed CORS policies
- Debug logging enabled

### Staging (`staging`)
- Production-like configuration
- Integration testing environment
- Performance monitoring

### Production (`prod`)
- Optimized performance settings
- Enhanced security policies
- Full monitoring and alerting

## Cost Optimization

- **Pay-per-request** DynamoDB billing
- **Lambda** charges only for execution time
- **API Gateway** charges per request
- **S3** for deployment artifacts (minimal cost)

## Troubleshooting

### Common Issues

1. **Lambda timeout**: Increase timeout in infrastructure templates
2. **DynamoDB throttling**: Switch to on-demand billing or increase capacity
3. **CORS errors**: Check API Gateway CORS configuration
4. **JWT validation fails**: Verify JWT secret consistency across functions

### Debugging

1. Check CloudWatch logs for Lambda functions
2. Use AWS X-Ray for distributed tracing
3. Enable API Gateway logging for request/response debugging

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review CloudWatch logs
3. Open an issue in the repository

## Next Steps

- Add email verification for user registration
- Implement password reset functionality
- Add task sharing and collaboration features
- Integrate with external calendar services
- Add push notifications for task reminders
