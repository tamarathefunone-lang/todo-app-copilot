# 🚀 Todo Task Manager

A full-stack todo task management application built with React frontend and Java serverless backend on AWS.

## 🏗️ Architecture

- **Frontend**: React.js with modern hooks and routing
- **Backend**: Java serverless functions on AWS Lambda
- **Database**: DynamoDB for scalable NoSQL storage
- **API**: RESTful API with JWT authentication
- **Infrastructure**: Infrastructure as Code with Terraform

## 🚀 Quick Start

### Prerequisites
```bash
node --version    # 16.x or higher
java --version    # 11 or 17
mvn --version     # 3.6 or higher
aws --version     # 2.x
terraform --version # 1.0 or higher
```

### 1. Frontend (React)
```bash
cd todo-task-manager
npm install
npm start
# Opens http://localhost:3000
```

### 2. Backend (Java + AWS)
```bash
cd todo-backend-java
mvn clean package
cd infrastructure
terraform init
terraform apply
# Deploys to AWS and shows API URL
```

### 3. API Documentation
```bash
python3 serve-docs.py
# Opens http://localhost:8080/api-docs.html
# Includes complete reminder API documentation
```

### 4. Git Operations
```bash
git add .
git commit -m "Your changes"
git push
```

## 📚 Detailed Documentation

For complete setup instructions, troubleshooting, and deployment guide, see:
👉 **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)**

For current deployment status and testing information, see:
🚀 **[DEPLOYMENT_STATUS.md](DEPLOYMENT_STATUS.md)**

## 📁 Project Structure

```
├── todo-backend-java/          # Java serverless backend
│   ├── src/main/java/          # Java source code
│   ├── infrastructure/         # Terraform infrastructure
│   ├── pom.xml                 # Maven configuration
│   └── deploy-*.sh             # Deployment scripts
├── todo-task-manager/          # React frontend
│   ├── src/                    # React source code
│   ├── public/                 # Static assets
│   └── package.json            # Node.js dependencies
└── Project_requirements.pdf    # Original requirements
```

## 🚀 Features

### Backend Features
- **User Authentication**: Register and login with JWT tokens
- **Task Management**: Full CRUD operations (Create, Read, Update, Delete)
- **Smart Reminders**: Email, SMS, and browser alarm notifications
- **Scheduled Processing**: EventBridge-powered reminder scheduling
- **Security**: Password hashing with BCrypt
- **Serverless**: AWS Lambda functions for scalability
- **Database**: DynamoDB with Enhanced Client
- **API Gateway**: RESTful endpoints with CORS support
- **Email Service**: AWS SES integration for email reminders
- **SMS Service**: AWS SNS integration for text reminders

### Frontend Features
- **Modern React**: Hooks-based components
- **Routing**: React Router for navigation
- **Authentication**: JWT token management
- **Responsive Design**: Mobile-friendly UI
- **API Integration**: Axios for HTTP requests
- **Reminder Management**: Intuitive reminder setup with validation
- **Real-time Notifications**: Browser-based alarm system

## 🛠️ Technology Stack

### Backend
- **Language**: Java 17
- **Framework**: AWS Lambda
- **Database**: Amazon DynamoDB
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Infrastructure**: Terraform
- **Email Service**: Amazon SES
- **SMS Service**: Amazon SNS
- **Scheduling**: Amazon EventBridge
- **Notifications**: Multi-channel reminder system

### Frontend
- **Language**: JavaScript (ES6+)
- **Framework**: React 18
- **Routing**: React Router
- **HTTP Client**: Axios
- **Styling**: CSS3 with responsive design
- **UI Components**: Enhanced forms with reminder management

## 📋 Prerequisites

- **Java**: JDK 17 or higher
- **Node.js**: 16 or higher
- **Maven**: 3.6 or higher
- **AWS CLI**: Configured with appropriate permissions
- **Terraform**: 1.0 or higher
- **AWS SES**: Email address verified for sending reminders
- **AWS SNS**: SMS permissions for text reminders

## 🔧 Setup and Installation

### Backend Setup
```bash
cd todo-backend-java
mvn clean package
```

### Frontend Setup
```bash
cd todo-task-manager
npm install
npm start
```

### Infrastructure Deployment
```bash
cd todo-backend-java/infrastructure
terraform init
terraform plan
terraform apply
```

## 📡 API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - User login
- `GET /auth/profile` - Get user profile

### Tasks (Requires Authentication)
- `GET /tasks` - Get all user tasks
- `POST /tasks` - Create new task (with optional reminders)
- `GET /tasks/{id}` - Get specific task
- `PUT /tasks/{id}` - Update task (including reminder settings)
- `DELETE /tasks/{id}` - Delete task

### Reminder Types
- **📧 Email**: Sent to registered email address
- **📱 SMS**: Sent to specified phone number  
- **🔔 Alarm**: Browser push notification

## 🔐 Environment Variables

### Backend
```
ENVIRONMENT=dev
JWT_SECRET=your-jwt-secret
USERS_TABLE=dev-todo-users
TASKS_TABLE=dev-todo-tasks
REMINDER_LAMBDA_ARN=arn:aws:lambda:region:account:function:reminder-processor
SENDER_EMAIL=noreply@yourdomain.com
SNS_TOPIC_ARN=arn:aws:sns:region:account:sms-reminders
```

### Frontend
```
REACT_APP_API_URL=https://your-api-gateway-url.amazonaws.com/dev
```

## 🚀 Deployment

The application is designed for AWS serverless deployment:

1. **Build the Java application**: `mvn clean package`
2. **Deploy infrastructure**: `terraform apply`
3. **Build and deploy frontend**: Can be deployed to S3/CloudFront

## 🧪 Testing

### Backend Testing
```bash
cd todo-backend-java
mvn test
```

### Frontend Testing
```bash
cd todo-task-manager
npm test
```

## 📊 Project Status

✅ **Completed Features:**
- User registration and authentication
- JWT token-based security
- Complete task CRUD operations
- Smart reminder system (Email/SMS/Alarm)
- React frontend with reminder management
- DynamoDB data persistence
- Infrastructure as Code with Terraform
- AWS Lambda serverless deployment
- SES email integration
- SNS SMS integration
- EventBridge scheduling
- Complete API documentation
- Maven build system
- Shaded JAR packaging

� **Ready for Production:**
- Full-stack application deployed on AWS
- Multi-channel notification system
- Comprehensive documentation
- Test data and examples

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 🙏 Acknowledgments

- AWS Documentation for Lambda and DynamoDB best practices
- React community for excellent documentation
- Maven and Terraform communities

## 📞 Support

For support, please open an issue in the GitHub repository or contact the development team.

---

**Built with ❤️ using modern serverless technologies**
