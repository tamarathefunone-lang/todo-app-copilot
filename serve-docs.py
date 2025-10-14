#!/usr/bin/env python3
"""
Simple HTTP server to serve Swagger documentation locally.
Run this script to view your API documentation at http://localhost:8080
"""

import http.server
import socketserver
import webbrowser
import os
import sys

# Configuration
PORT = 8080
DIRECTORY = "."

class CustomHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)
    
    def end_headers(self):
        # Add CORS headers
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

def serve_docs():
    """Start the documentation server"""
    print(f"🚀 Starting Todo Task Manager API Documentation Server...")
    print(f"📚 Documentation will be available at: http://localhost:{PORT}")
    print(f"📁 Serving files from: {os.path.abspath(DIRECTORY)}")
    print(f"🌐 Opening documentation in your browser...")
    print(f"⏹️  Press Ctrl+C to stop the server")
    print("=" * 60)
    
    try:
        with socketserver.TCPServer(("", PORT), CustomHTTPRequestHandler) as httpd:
            # Open browser automatically
            webbrowser.open(f'http://localhost:{PORT}/api-docs.html')
            
            print(f"✅ Server started successfully!")
            print(f"📖 Documentation: http://localhost:{PORT}/api-docs.html")
            print(f"📄 Swagger YAML: http://localhost:{PORT}/swagger.yaml")
            
            # Serve until interrupted
            httpd.serve_forever()
            
    except KeyboardInterrupt:
        print("\n🛑 Server stopped by user")
        sys.exit(0)
    except OSError as e:
        if e.errno == 48:  # Address already in use
            print(f"❌ Error: Port {PORT} is already in use!")
            print(f"💡 Try using a different port or stop the existing server")
        else:
            print(f"❌ Error starting server: {e}")
        sys.exit(1)

if __name__ == "__main__":
    serve_docs()
