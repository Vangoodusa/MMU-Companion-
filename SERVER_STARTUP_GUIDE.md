# AECI MMU Companion Server Startup Guide

## Overview
Your AECI MMU Companion server is a Node.js/Express application that serves as the backend for your mobile companion app. This guide will help you navigate to the server folder and start it properly.

## Prerequisites
✅ **Node.js v22.16.0** - Already installed and available
✅ **npm v10.9.2** - Already installed and available
✅ **Dependencies** - Already installed in node_modules

## Navigation Commands

### 1. Navigate to your workspace
```bash
cd /workspace
```

### 2. Navigate to the server folder
```bash
cd AECIMMUCompanion/server
```

### 3. Alternative: Navigate directly from anywhere
```bash
cd /workspace/AECIMMUCompanion/server
```

## Important First-Time Setup

**If this is your first time running the server on this system, you need to rebuild SQLite3:**

```bash
cd /workspace/AECIMMUCompanion/server
npm rebuild sqlite3
```

This fixes compatibility issues with the native SQLite3 module.

## Server Startup Commands

### Development Mode (Recommended for testing)
```bash
npm run dev
```
This uses `nodemon` which automatically restarts the server when you make changes to the code.

### Production Mode
```bash
npm start
```
or
```bash
node server.js
```

## Server Configuration

### Current Settings (from .env file):
- **Port**: 3000
- **JWT Secret**: Configured for authentication
- **SMTP Settings**: Ready for email functionality
- **Database**: SQLite (aeci_mmu.db)

### Default Admin User (Created automatically):
- **Username**: admin
- **Password**: AECIAdmin2025!
- **Email**: admin@aeci.com

### Server Features:
- ✅ Express.js web server
- ✅ CORS enabled for cross-origin requests
- ✅ JWT authentication
- ✅ BCrypt password hashing
- ✅ Email capabilities (nodemailer)
- ✅ SQLite database
- ✅ File upload support (PDF, Excel, CSV)
- ✅ Security middleware (helmet, rate limiting)
- ✅ Report generation system

## Quick Start Instructions

1. **Open your terminal/command prompt**

2. **Navigate to the server directory:**
   ```bash
   cd /workspace/AECIMMUCompanion/server
   ```

3. **First-time setup (rebuild SQLite3):**
   ```bash
   npm rebuild sqlite3
   ```

4. **Start the server in development mode:**
   ```bash
   npm run dev
   ```

5. **Verify the server is running:**
   - You should see output like:
     ```
     🚀 AECI MMU Companion Server running on port 3000
     📋 Health check: http://localhost:3000/api/health
     🔐 Login endpoint: http://localhost:3000/api/auth/login
     ```
   - The server will be available at `http://localhost:3000`
   - For Android emulator: `http://10.0.2.2:3000`

## Server Structure
```
AECIMMUCompanion/server/
├── server.js              # Main server file
├── package.json           # Dependencies and scripts
├── .env                   # Environment configuration
├── aeci_mmu.db           # SQLite database
├── reports/              # Generated reports directory
├── node_modules/         # Installed dependencies
└── README.md             # Additional documentation
```

## Testing the Server

Once running, you can test these endpoints:

### Health Check
```bash
curl http://localhost:3000/api/health
```

### Login Test
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"AECIAdmin2025!"}'
```

## Troubleshooting

### SQLite3 Error (Invalid ELF header):
```bash
npm rebuild sqlite3
```

### If you get permission errors:
```bash
sudo npm run dev
```

### If port 3000 is busy:
1. Edit the `.env` file to change the PORT value
2. Or kill processes using port 3000:
   ```bash
   sudo lsof -t -i:3000 | xargs kill -9
   ```

### If dependencies are missing:
```bash
npm install
```

## Stopping the Server
- Press `Ctrl + C` in the terminal where the server is running
- Or close the terminal window

## Server Endpoints
Your server includes endpoints for:
- User authentication (`/api/auth/*`)
- Form management (`/api/forms/*`)
- Equipment tracking (`/api/equipment/*`)
- Report generation (`/api/reports/*`)
- Job card management (`/api/jobcards/*`)
- File uploads and downloads (`/api/upload/*`)
- Health monitoring (`/api/health`)

## Complete Command Summary

```bash
# Navigate to server directory
cd /workspace/AECIMMUCompanion/server

# First-time setup
npm rebuild sqlite3

# Start server (development mode)
npm run dev

# Or start server (production mode)
npm start
```

The server is ready to run with all dependencies installed and configured!