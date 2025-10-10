# Todo Task Manager

A full-stack todo task management application built with React frontend and Java serverless backend on AWS.

## 🏗️ Architecture

- **Frontend**: React.js with modern hooks and routing
- **Backend**: Java serverless functions on AWS Lambda
- **Database**: DynamoDB for scalable NoSQL storage
- **API**: RESTful API with JWT authentication
- **Infrastructure**: Infrastructure as Code with Terraform

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
- **Security**: Password hashing with BCrypt
- **Serverless**: AWS Lambda functions for scalability
- **Database**: DynamoDB with Enhanced Client
- **API Gateway**: RESTful endpoints with CORS support

### Frontend Features
- **Modern React**: Hooks-based components
- **Routing**: React Router for navigation
- **Authentication**: JWT token management
- **Responsive Design**: Mobile-friendly UI
- **API Integration**: Axios for HTTP requests

## 🛠️ Technology Stack

### Backend
- **Language**: Java 11/17
- **Framework**: AWS Lambda
- **Database**: Amazon DynamoDB
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Infrastructure**: Terraform

### Frontend
- **Language**: JavaScript (ES6+)
- **Framework**: React 18
- **Routing**: React Router
- **HTTP Client**: Axios
- **Styling**: CSS3

## 📋 Prerequisites

- **Java**: JDK 11 or higher
- **Node.js**: 16 or higher
- **Maven**: 3.6 or higher
- **AWS CLI**: Configured with appropriate permissions
- **Terraform**: 1.0 or higher

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

### Tasks (Requires Authentication)
- `GET /tasks` - Get all user tasks
- `POST /tasks` - Create new task
- `GET /tasks/{id}` - Get specific task
- `PUT /tasks/{id}` - Update task
- `DELETE /tasks/{id}` - Delete task

## 🔐 Environment Variables

### Backend
```
ENVIRONMENT=dev
JWT_SECRET=your-jwt-secret
USERS_TABLE=dev-todo-users
TASKS_TABLE=dev-todo-tasks
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
- React frontend with routing
- DynamoDB data persistence
- Infrastructure as Code with Terraform
- Maven build system
- Shaded JAR packaging

🔄 **In Progress:**
- AWS deployment (pending permissions)
- Frontend-backend integration
- Production optimizations

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
