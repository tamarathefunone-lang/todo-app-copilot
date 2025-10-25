#!/usr/bin/env python3
"""
Test Data Loader for Todo Task Manager
=====================================

This script helps you load test data into your Todo Task Manager application.
It will register test users and create sample tasks for testing purposes.

Usage:
    python3 load-test-data.py [--api-url YOUR_API_URL]

Requirements:
    pip install requests

"""

import json
import requests
import sys
import argparse
from datetime import datetime
import time

# Default API URL (update this with your deployed API URL)
DEFAULT_API_URL = "https://2gg5dchmo6.execute-api.us-east-1.amazonaws.com/dev"

def load_test_data():
    """Load test data from test-data.json file"""
    try:
        with open('test-data.json', 'r') as f:
            return json.load(f)
    except FileNotFoundError:
        print("❌ Error: test-data.json file not found!")
        print("   Make sure you're running this script from the project root directory.")
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"❌ Error: Invalid JSON in test-data.json: {e}")
        sys.exit(1)

def register_user(api_url, user):
    """Register a single user"""
    try:
        response = requests.post(
            f"{api_url}/auth/register",
            json=user,
            headers={'Content-Type': 'application/json'}
        )
        
        if response.status_code == 201:
            print(f"✅ Registered user: {user['email']}")
            return True
        elif response.status_code == 409:
            print(f"⚠️  User already exists: {user['email']}")
            return True
        else:
            print(f"❌ Failed to register {user['email']}: {response.status_code} - {response.text}")
            return False
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Network error registering {user['email']}: {e}")
        return False

def login_user(api_url, email, password):
    """Login user and return JWT token"""
    try:
        response = requests.post(
            f"{api_url}/auth/login",
            json={"email": email, "password": password},
            headers={'Content-Type': 'application/json'}
        )
        
        if response.status_code == 200:
            data = response.json()
            token = data.get('token')
            if token:
                print(f"✅ Logged in: {email}")
                return token
            else:
                print(f"❌ No token received for {email}")
                return None
        else:
            print(f"❌ Failed to login {email}: {response.status_code} - {response.text}")
            return None
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Network error logging in {email}: {e}")
        return None

def create_task(api_url, token, task):
    """Create a single task"""
    try:
        headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {token}'
        }
        
        response = requests.post(
            f"{api_url}/tasks",
            json=task,
            headers=headers
        )
        
        if response.status_code == 201:
            print(f"✅ Created task: {task['title']}")
            return True
        else:
            print(f"❌ Failed to create task '{task['title']}': {response.status_code} - {response.text}")
            return False
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Network error creating task '{task['title']}': {e}")
        return False

def main():
    parser = argparse.ArgumentParser(description='Load test data into Todo Task Manager')
    parser.add_argument('--api-url', default=DEFAULT_API_URL, 
                       help=f'API URL (default: {DEFAULT_API_URL})')
    parser.add_argument('--user-index', type=int, default=0,
                       help='Index of user to create tasks for (default: 0)')
    
    args = parser.parse_args()
    api_url = args.api_url.rstrip('/')
    
    print("🚀 Loading test data into Todo Task Manager")
    print(f"📡 API URL: {api_url}")
    print("=" * 60)
    
    # Load test data
    test_data = load_test_data()
    users = test_data['users']
    tasks = test_data['tasks']
    
    print(f"📊 Found {len(users)} users and {len(tasks)} tasks in test data")
    print()
    
    # Register all users
    print("👥 Registering users...")
    registered_users = 0
    for user in users:
        if register_user(api_url, user):
            registered_users += 1
        time.sleep(0.5)  # Small delay to avoid rate limiting
    
    print(f"✅ Successfully processed {registered_users}/{len(users)} users")
    print()
    
    # Login with the first user (or specified user) to create tasks
    if args.user_index >= len(users):
        print(f"❌ Error: User index {args.user_index} is out of range (0-{len(users)-1})")
        sys.exit(1)
    
    target_user = users[args.user_index]
    print(f"🔑 Logging in as {target_user['email']} to create tasks...")
    
    token = login_user(api_url, target_user['email'], target_user['password'])
    if not token:
        print("❌ Could not login to create tasks")
        sys.exit(1)
    
    # Create tasks
    print("📝 Creating tasks...")
    created_tasks = 0
    for task in tasks:
        if create_task(api_url, token, task):
            created_tasks += 1
        time.sleep(0.3)  # Small delay to avoid rate limiting
    
    print(f"✅ Successfully created {created_tasks}/{len(tasks)} tasks")
    print()
    
    # Summary
    print("🎉 Test data loading completed!")
    print("=" * 60)
    print("📋 Summary:")
    print(f"   👥 Users registered: {registered_users}")
    print(f"   📝 Tasks created: {created_tasks}")
    print(f"   🔑 Tasks created for: {target_user['email']}")
    print()
    print("🌐 You can now test your application with this data!")
    print(f"   Frontend: http://localhost:3000")
    print(f"   API Docs: http://localhost:8080/api-docs.html")
    print()
    print("💡 Login credentials for testing:")
    for i, user in enumerate(users):
        marker = "👈 (has tasks)" if i == args.user_index else ""
        print(f"   📧 {user['email']} / 🔐 {user['password']} {marker}")

if __name__ == "__main__":
    main()
