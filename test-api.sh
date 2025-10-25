#!/bin/bash
# Manual API Testing Script for Todo Task Manager
# ==============================================
# 
# This script provides curl commands to manually test your API endpoints.
# Update the API_URL variable with your deployed API URL.

# Configuration
API_URL="https://2gg5dchmo6.execute-api.us-east-1.amazonaws.com/dev"
TEST_EMAIL="john.doe@example.com"
TEST_PASSWORD="password123"

echo "🧪 Todo Task Manager - Manual API Testing"
echo "=========================================="
echo "API URL: $API_URL"
echo ""

# Function to print section headers
print_section() {
    echo ""
    echo "🔹 $1"
    echo "$(printf '%.0s-' {1..40})"
}

# Function to pause and wait for user input
pause() {
    echo ""
    read -p "Press Enter to continue..." -n1 -s
    echo ""
}

print_section "1. Register a new user"
echo "curl -X POST $API_URL/auth/register \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"name\":\"John Doe\",\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}'"

echo ""
echo "📋 Execute this command? (y/n)"
read -r response
if [[ "$response" == "y" ]]; then
    curl -X POST "$API_URL/auth/register" \
      -H "Content-Type: application/json" \
      -d "{\"name\":\"John Doe\",\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}" \
      | python3 -m json.tool 2>/dev/null || echo "Response received"
fi

pause

print_section "2. Login user"
echo "curl -X POST $API_URL/auth/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}'"

echo ""
echo "📋 Execute this command? (y/n)"
read -r response
if [[ "$response" == "y" ]]; then
    LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\"}")
    
    echo "$LOGIN_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "Response: $LOGIN_RESPONSE"
    
    # Extract token for next requests
    TOKEN=$(echo "$LOGIN_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('token', ''))" 2>/dev/null || echo "")
    
    if [[ -n "$TOKEN" ]]; then
        echo ""
        echo "✅ Token extracted for next requests"
    fi
fi

pause

if [[ -z "$TOKEN" ]]; then
    echo "⚠️  No token available. Please login first or set TOKEN manually:"
    echo "   export TOKEN='your_jwt_token_here'"
    echo ""
    read -p "Enter JWT token manually (or press Enter to skip): " TOKEN
fi

print_section "3. Create a task"
echo "curl -X POST $API_URL/tasks \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -H 'Authorization: Bearer \$TOKEN' \\"
echo "  -d '{\"title\":\"Test Task\",\"description\":\"A test task\",\"priority\":\"HIGH\",\"dueDate\":\"2025-10-20T18:00:00Z\"}'"

echo ""
echo "📋 Execute this command? (y/n)"
read -r response
if [[ "$response" == "y" ]] && [[ -n "$TOKEN" ]]; then
    curl -X POST "$API_URL/tasks" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"title":"Test Task","description":"A test task created via curl","priority":"HIGH","dueDate":"2025-10-20T18:00:00Z"}' \
      | python3 -m json.tool 2>/dev/null || echo "Response received"
elif [[ -z "$TOKEN" ]]; then
    echo "❌ Cannot execute - no token available"
fi

pause

print_section "4. Get all tasks"
echo "curl -X GET $API_URL/tasks \\"
echo "  -H 'Authorization: Bearer \$TOKEN'"

echo ""
echo "📋 Execute this command? (y/n)"
read -r response
if [[ "$response" == "y" ]] && [[ -n "$TOKEN" ]]; then
    curl -X GET "$API_URL/tasks" \
      -H "Authorization: Bearer $TOKEN" \
      | python3 -m json.tool 2>/dev/null || echo "Response received"
elif [[ -z "$TOKEN" ]]; then
    echo "❌ Cannot execute - no token available"
fi

pause

print_section "5. Get user profile"
echo "curl -X GET $API_URL/auth/profile \\"
echo "  -H 'Authorization: Bearer \$TOKEN'"

echo ""
echo "📋 Execute this command? (y/n)"
read -r response
if [[ "$response" == "y" ]] && [[ -n "$TOKEN" ]]; then
    curl -X GET "$API_URL/auth/profile" \
      -H "Authorization: Bearer $TOKEN" \
      | python3 -m json.tool 2>/dev/null || echo "Response received"
elif [[ -z "$TOKEN" ]]; then
    echo "❌ Cannot execute - no token available"
fi

print_section "Testing completed!"
echo ""
echo "🎯 Quick Test Commands:"
echo "----------------------"
echo ""
echo "# Register user:"
echo "curl -X POST $API_URL/auth/register -H 'Content-Type: application/json' -d '{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"pass123\"}'"
echo ""
echo "# Login:"
echo "curl -X POST $API_URL/auth/login -H 'Content-Type: application/json' -d '{\"email\":\"test@example.com\",\"password\":\"pass123\"}'"
echo ""
echo "# Create task (replace YOUR_TOKEN):"
echo "curl -X POST $API_URL/tasks -H 'Content-Type: application/json' -H 'Authorization: Bearer YOUR_TOKEN' -d '{\"title\":\"Quick Test\",\"description\":\"Test task\",\"priority\":\"MEDIUM\"}'"
echo ""
echo "# Get tasks:"
echo "curl -X GET $API_URL/tasks -H 'Authorization: Bearer YOUR_TOKEN'"
echo ""
echo "💡 Pro tip: Use the Python script for easier bulk data loading:"
echo "   python3 load-test-data.py"
echo ""
