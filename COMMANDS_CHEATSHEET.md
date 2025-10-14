# 📋 Todo Task Manager - Command Cheat Sheet

Quick reference for all essential commands.

## 🎯 Most Common Commands

### Start Development Environment
```bash
# Terminal 1: Frontend
cd todo-task-manager && npm start

# Terminal 2: Documentation
python3 serve-docs.py

# Terminal 3: Available for backend work
cd todo-backend-java
```

## 🎨 Frontend Commands (React)

| Command | Purpose | Example |
|---------|---------|---------|
| `npm install` | Install dependencies | `cd todo-task-manager && npm install` |
| `npm start` | Start dev server (port 3000) | Development with hot reload |
| `npm run build` | Create production build | For deployment |
| `npm test` | Run tests | Test suite execution |
| `npm install <package>` | Add new dependency | `npm install axios` |

## ☕ Backend Commands (Java/Maven)

| Command | Purpose | Time | Output |
|---------|---------|------|--------|
| `mvn clean` | Delete target folder | 2s | Cleans build artifacts |
| `mvn compile` | Compile Java code | 10s | Classes in target/classes |
| `mvn package` | Build JAR file | 30s | target/todo-backend.jar (18MB) |
| `mvn test` | Run unit tests | 15s | Test results in console |
| `mvn clean package` | Full clean build | 45s | Fresh JAR file |
| `mvn package -DskipTests` | Build without tests | 20s | Faster build |

## ☁️ AWS Deployment Commands

| Command | Purpose | Time | What it creates |
|---------|---------|------|-----------------|
| `aws configure` | Setup AWS credentials | 1min | ~/.aws/credentials |
| `terraform init` | Initialize Terraform | 30s | Downloads providers |
| `terraform plan` | Preview changes | 10s | Shows what will be created |
| `terraform apply` | Deploy to AWS | 3-5min | 45+ AWS resources |
| `terraform destroy` | Delete AWS resources | 2min | Removes everything |
| `terraform output` | Show deployment info | 2s | API URLs, table names |

## 📂 Git Commands

| Command | Purpose | When to use |
|---------|---------|-------------|
| `git status` | Check file status | Before any git operation |
| `git add .` | Stage all changes | Before committing |
| `git commit -m "message"` | Save changes | After testing |
| `git push` | Upload to GitHub | Share changes |
| `git pull` | Download changes | Get latest updates |
| `git log --oneline` | View commit history | Check what changed |

## 📖 Documentation Commands

| Command | Purpose | URL |
|---------|---------|-----|
| `python3 serve-docs.py` | Start docs server | http://localhost:8080/api-docs.html |
| `python3 -m http.server 8080` | Simple file server | http://localhost:8080/ |

## 🧪 Testing Commands

### Frontend Testing
```bash
cd todo-task-manager
npm test                    # Run all tests
npm test -- --watch        # Watch mode
npm test -- --coverage     # With coverage report
```

### Backend Testing
```bash
cd todo-backend-java
mvn test                    # Run all tests
mvn test -Dtest=UserServiceTest  # Run specific test
```

### API Testing
```bash
# Test registration endpoint
curl -X POST https://your-api-url/dev/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123","name":"Test"}'

# Test with authentication
curl -X GET https://your-api-url/dev/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🔧 Troubleshooting Commands

### Port Issues
```bash
lsof -ti:3000              # Check what's using port 3000
kill -9 $(lsof -ti:3000)   # Kill process on port 3000
```

### Node.js Issues
```bash
rm -rf node_modules package-lock.json  # Delete dependencies
npm cache clean --force                # Clear npm cache
npm install                           # Reinstall everything
```

### AWS Issues
```bash
aws sts get-caller-identity   # Check AWS credentials
aws configure list            # Show current AWS config
terraform validate           # Check Terraform syntax
```

### Git Issues
```bash
git reset --hard HEAD        # Discard all changes
git clean -fd               # Remove untracked files
git stash                   # Temporarily save changes
```

## 📊 Monitoring Commands

### Check AWS Resources
```bash
# List your Lambda functions
aws lambda list-functions --query 'Functions[?starts_with(FunctionName, `dev-todo`)].FunctionName'

# List DynamoDB tables  
aws dynamodb list-tables --query 'TableNames[?starts_with(@, `dev-todo`)]'

# Check API Gateway
aws apigateway get-rest-apis --query 'items[?name==`dev-todo-api`]'
```

### View Logs
```bash
# Lambda function logs
aws logs tail /aws/lambda/dev-todo-register --follow

# List all log groups
aws logs describe-log-groups --log-group-name-prefix "/aws/lambda/dev-todo"
```

## 🚨 Emergency Commands

### Stop Everything
```bash
# Kill all Node.js processes
pkill -f "npm start"
pkill -f node

# Kill Python docs server
pkill -f "serve-docs.py"
```

### Reset Everything
```bash
# Reset Git repository
git reset --hard origin/main
git clean -fd

# Clean Node.js
rm -rf node_modules package-lock.json
npm install

# Clean Java build
mvn clean

# Destroy AWS resources (saves money!)
terraform destroy -auto-approve
```

## 📱 Development Workflow

### Daily Development
```bash
# 1. Start development environment
cd todo-task-manager && npm start &
python3 serve-docs.py &

# 2. Make changes and test locally

# 3. Commit changes
git add .
git commit -m "Feature: Add task filtering"

# 4. Deploy to AWS (if backend changes)
cd todo-backend-java && mvn package
cd infrastructure && terraform apply

# 5. Push to GitHub
git push
```

### Before Going Home
```bash
# Save work
git add . && git commit -m "WIP: End of day save"
git push

# Optional: Destroy AWS resources to save money
terraform destroy -auto-approve
```

## 🔗 Useful URLs

- **Frontend**: http://localhost:3000
- **API Docs**: http://localhost:8080/api-docs.html
- **AWS Console**: https://console.aws.amazon.com
- **GitHub**: https://github.com/your-username/todo-task-manager
- **Swagger Editor**: https://editor.swagger.io

## 💡 Pro Tips

1. **Always check `git status` before committing**
2. **Use `terraform plan` before `terraform apply`**
3. **Keep the documentation server running** for API reference
4. **Use `npm start` in one terminal, keep another free** for commands
5. **Commit often with descriptive messages**
6. **Test locally before deploying to AWS**
7. **Monitor AWS costs** - destroy resources when not needed

---

**Need more details?** Check the full [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
