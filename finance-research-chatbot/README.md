# 🤖💰 Finance Research Chatbot

A powerful AI-powered application that conducts deep financial research, provides real-time streaming responses, and generates comprehensive reports with citations.

![Finance Chatbot Demo](https://img.shields.io/badge/Status-Production%20Ready-brightgreen) ![Real-time Streaming](https://img.shields.io/badge/Feature-Real--time%20Streaming-blue) ![Free Deployment](https://img.shields.io/badge/Deployment-100%25%20FREE-gold)

## 🌟 What This Application Does

### For Users:
- 💬 **Chat with AI about finance** - Ask questions about stocks, markets, companies
- 🔍 **Get researched answers** - AI searches the web and provides cited sources
- 📊 **Download reports** - Export conversations as Markdown or HTML
- ⚡ **Real-time responses** - See AI typing responses live, word by word
- 🧵 **Multiple conversations** - Create different research threads
- 🔐 **Secure & Private** - Your data is protected with authentication

### For Developers:
- 🚀 **Modern tech stack** - React + Spring Boot + PostgreSQL/MySQL + Redis
- 🌊 **Streaming architecture** - Server-Sent Events for real-time communication
- 🐳 **Containerized** - Easy deployment with Docker
- ☁️ **Cloud-ready** - Deploy for free on Railway + Netlify

---

## 📋 Table of Contents

1. [Quick Start (5 Minutes)](#-quick-start-5-minutes)
2. [Prerequisites](#-prerequisites)
3. [Step-by-Step Installation](#-step-by-step-installation)
4. [Configuration](#-configuration)
5. [Running the Application](#-running-the-application)
6. [Using the Application](#-using-the-application)
7. [Troubleshooting](#-troubleshooting)
8. [Deployment to Cloud](#-deployment-to-cloud-100-free)
9. [Architecture](#-architecture)
10. [Contributing](#-contributing)

---

## 🚀 Quick Start (5 Minutes)

**Want to run this immediately? Follow these steps:**

### Step 1: Install Docker Desktop
- **Windows/Mac**: Download from [docker.com](https://www.docker.com/products/docker-desktop/)
- **Linux**: Follow [Docker installation guide](https://docs.docker.com/engine/install/)
- ✅ **Verify**: Open terminal and run `docker --version`

### Step 2: Get API Keys (FREE)
1. **Groq API** (for AI): 
   - Go to [console.groq.com](https://console.groq.com/)
   - Sign up and get your API key
   - Copy it somewhere safe

2. **Tavily API** (for web search):
   - Go to [app.tavily.com](https://app.tavily.com/)
   - Sign up and get your API key
   - Copy it somewhere safe

### Step 3: Download & Run
```bash
# 1. Download the project
git clone https://github.com/your-username/finance-research-chatbot.git
cd finance-research-chatbot

# 2. Create environment file
cp .env.example .env

# 3. Edit .env file and add your API keys
# (Use Notepad, VS Code, or any text editor)
# Replace YOUR_GROQ_API_KEY_HERE with your actual Groq key
# Replace YOUR_TAVILY_API_KEY_HERE with your actual Tavily key

# 4. Start the application
docker-compose up -d

# 5. Wait 2-3 minutes for everything to start
# 6. Open http://localhost:3000 in your browser
```

**🎉 That's it! The application should be running.**

---

## 📋 Prerequisites

### Required Software:
1. **Docker Desktop** - For running the application
   - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
   - Minimum 4GB RAM recommended

2. **Web Browser** - For using the application
   - Chrome, Firefox, Safari, or Edge

3. **Text Editor** - For editing configuration files
   - Notepad (Windows), TextEdit (Mac), or VS Code

### Required API Keys (FREE):
1. **Groq API Key** - For AI responses
2. **Tavily API Key** - For web search

### Optional (for development):
- Git (for cloning the repository)
- Node.js 18+ (for frontend development)
- Java 17+ (for backend development)

---

## 🔧 Step-by-Step Installation

### Step 1: Install Docker Desktop

#### Windows:
1. Download Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop/)
2. Run the installer
3. Follow the setup wizard
4. Restart your computer when prompted
5. Open Command Prompt and verify: `docker --version`

#### Mac:
1. Download Docker Desktop for Mac
2. Drag Docker.app to Applications folder
3. Launch Docker Desktop
4. Open Terminal and verify: `docker --version`

#### Linux (Ubuntu/Debian):
```bash
# Update package index
sudo apt update

# Install Docker
sudo apt install docker.io docker-compose

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add your user to docker group
sudo usermod -aG docker $USER

# Restart your session or reboot
# Verify installation
docker --version
```

### Step 2: Get Your API Keys

#### Groq API Key (AI Provider):
1. Visit [console.groq.com](https://console.groq.com/)
2. Click "Sign Up" or "Sign In"
3. Complete registration (email required)
4. Go to "API Keys" section
5. Click "Create API Key"
6. Copy the key (starts with `gsk_`)
7. Save it somewhere safe

#### Tavily API Key (Web Search):
1. Visit [app.tavily.com](https://app.tavily.com/)
2. Sign up with your email
3. Complete email verification
4. In dashboard, find "API Key" section
5. Copy your API key
6. Save it somewhere safe

### Step 3: Download the Project

#### Option A: Using Git (Recommended)
```bash
git clone https://github.com/your-username/finance-research-chatbot.git
cd finance-research-chatbot
```

#### Option B: Download ZIP
1. Go to the GitHub repository
2. Click "Code" → "Download ZIP"
3. Extract the ZIP file
4. Open terminal in the extracted folder

### Step 4: Configure Environment Variables

1. **Copy the example environment file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit the .env file** using any text editor:
   
   **Windows (Notepad):**
   ```bash
   notepad .env
   ```
   
   **Mac (TextEdit):**
   ```bash
   open -e .env
   ```
   
   **Linux (nano):**
   ```bash
   nano .env
   ```

3. **Update these values in the .env file:**
   ```properties
   # Replace with your actual Groq API key
   GROQ_API_KEY=gsk_your_actual_groq_key_here
   
   # Replace with your actual Tavily API key
   TAVILY_API_KEY=tvly-your_actual_tavily_key_here
   
   # Keep these as they are (they work out of the box)
   DB_HOST=mysql
   DB_PORT=3306
   DB_NAME=finance_chatbot
   DB_USERNAME=chatbot_user
   DB_PASSWORD=chatbot_password
   
   REDIS_HOST=redis
   REDIS_PORT=6379
   
   JWT_SECRET=da98b37073876db5d188513d0b056e8fe5ba3b5f3b63ca9d8efd22e6a85c660f
   JWT_EXPIRATION=86400000
   
   LLM_PROVIDER=groq
   SEARCH_PROVIDER=tavily
   ```

4. **Save the file** (Ctrl+S or Cmd+S)

---

## 🏃‍♂️ Running the Application

### Method 1: Using Docker Compose (Recommended)

1. **Start all services:**
   ```bash
   docker-compose up -d
   ```

2. **Wait for startup** (2-3 minutes):
   ```bash
   # Check if all containers are running
   docker-compose ps
   ```

3. **Open your browser** and go to:
   ```
   http://localhost:3000
   ```

### Method 2: Using Start Scripts

#### Windows:
```cmd
start-dev.bat
```

#### Linux/Mac:
```bash
./start-dev.sh
```

### Verify Everything is Working

1. **Check container status:**
   ```bash
   docker-compose ps
   ```
   All services should show "Up" status.

2. **Check logs if there are issues:**
   ```bash
   # Backend logs
   docker logs finance-chatbot-backend
   
   # Frontend logs
   docker logs finance-chatbot-frontend
   
   # Database logs
   docker logs finance-chatbot-mysql
   ```

3. **Access the application:**
   - **Frontend**: http://localhost:3000
   - **Backend API**: http://localhost:8080/api
   - **Database**: localhost:3307 (external access)

---

## ⚙️ Configuration

### Environment Variables Explained

| Variable | Description | Example Value |
|----------|-------------|---------------|
| `GROQ_API_KEY` | Your Groq API key for AI responses | `gsk_abc123...` |
| `TAVILY_API_KEY` | Your Tavily API key for web search | `tvly-xyz789...` |
| `DB_HOST` | Database hostname | `mysql` (for Docker) |
| `DB_PORT` | Database port | `3306` |
| `DB_NAME` | Database name | `finance_chatbot` |
| `DB_USERNAME` | Database username | `chatbot_user` |
| `DB_PASSWORD` | Database password | `chatbot_password` |
| `REDIS_HOST` | Redis hostname | `redis` (for Docker) |
| `REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | Secret key for JWT tokens | (auto-generated) |
| `LLM_PROVIDER` | AI provider to use | `groq` |
| `SEARCH_PROVIDER` | Search provider to use | `tavily` |

### Port Configuration

| Service | Internal Port | External Port | URL |
|---------|---------------|---------------|-----|
| Frontend | 80 | 3000 | http://localhost:3000 |
| Backend | 8080 | 8080 | http://localhost:8080 |
| MySQL | 3306 | 3307 | localhost:3307 |
| Redis | 6379 | 6379 | localhost:6379 |

### Advanced Configuration

For production or custom setups, you can modify:

- **docker-compose.yml** - Container configuration
- **backend/src/main/resources/application.properties** - Spring Boot settings
- **frontend/src/config/** - React configuration

---

## 📱 Using the Application

### First Time Setup

1. **Open the application** at http://localhost:3000
2. **Register a new account:**
   - Click "Sign Up"
   - Enter your email and password
   - Click "Create Account"

3. **Login:**
   - Use your email and password
   - Click "Sign In"

### Using the Chat Interface

1. **Create a new research thread:**
   - Click "New Research Thread"
   - Give it a descriptive name

2. **Ask financial questions:**
   ```
   Examples:
   • "What is the current stock price of Apple?"
   • "Analyze Tesla's financial performance this quarter"
   • "Compare Microsoft vs Google revenue growth"
   • "What are the latest trends in cryptocurrency?"
   ```

3. **Watch real-time responses:**
   - 🟦 Blue loading bar appears when processing
   - ⏳ Hourglass animation while waiting
   - ✨ Sparkle animation when AI is responding
   - 🟢 "LIVE" indicator during streaming

4. **Review sources:**
   - Check the right sidebar for web sources
   - Click links to verify information
   - See citations in the AI responses

5. **Export reports:**
   - Click "MD" for Markdown format
   - Click "HTML" for HTML format
   - Files download automatically

### Features Walkthrough

#### Real-time Streaming
- See AI responses appear word-by-word as they're generated
- Loading indicators show processing status
- No waiting for complete responses

#### Multiple Threads
- Create separate conversations for different topics
- Switch between threads easily
- Each thread maintains its own history

#### Source Verification
- All AI responses include web sources
- Click source links to verify information
- Sources are automatically deduplicated

#### Export Capabilities
- Download entire conversations
- Markdown format for documentation
- HTML format for presentations

---

## 🚨 Troubleshooting

### Common Issues and Solutions

#### Issue 1: "Command not found: docker"
**Problem:** Docker not installed or not in PATH
**Solution:** 
- Reinstall Docker Desktop
- Restart your terminal/computer
- Verify with `docker --version`

#### Issue 2: "Cannot connect to Docker daemon"
**Problem:** Docker Desktop not running
**Solution:**
- Start Docker Desktop application
- Wait for it to fully load (whale icon in system tray)
- Try command again

#### Issue 3: "Port 3000 already in use"
**Problem:** Another application using port 3000
**Solution:**
```bash
# Find what's using port 3000
netstat -an | findstr 3000  # Windows
lsof -i :3000               # Mac/Linux

# Stop the conflicting service or use different port
docker-compose down
docker-compose up -d
```

#### Issue 4: "Invalid API key" errors
**Problem:** Incorrect or missing API keys
**Solution:**
1. Check your .env file has correct API keys
2. Verify keys work by testing them directly:
   - Groq: [console.groq.com](https://console.groq.com/)
   - Tavily: [app.tavily.com](https://app.tavily.com/)
3. Restart containers: `docker-compose restart`

#### Issue 5: Containers keep restarting
**Problem:** Configuration or resource issues
**Solution:**
```bash
# Check logs for specific error
docker logs finance-chatbot-backend
docker logs finance-chatbot-frontend
docker logs finance-chatbot-mysql

# Common fixes:
# 1. Increase Docker memory limit (4GB+)
# 2. Check .env file format
# 3. Wait longer for database initialization
```

#### Issue 6: "Loading animations not showing"
**Problem:** Frontend cache or build issues
**Solution:**
```bash
# Rebuild frontend
docker-compose build frontend

# Hard refresh browser (Ctrl+F5)
# Clear browser cache
# Try incognito/private browsing mode
```

#### Issue 7: "Database connection failed"
**Problem:** MySQL container not ready
**Solution:**
```bash
# Wait for database to initialize (can take 2-3 minutes)
docker logs finance-chatbot-mysql

# Look for "ready for connections" message
# If still failing, restart:
docker-compose down
docker-compose up -d
```

### Getting Help

1. **Check the logs** first:
   ```bash
   docker-compose logs
   ```

2. **Search existing issues** in the GitHub repository

3. **Create a new issue** with:
   - Your operating system
   - Docker version (`docker --version`)
   - Error messages from logs
   - Steps to reproduce

### Performance Tips

- **Minimum RAM:** 4GB for Docker Desktop
- **Recommended RAM:** 8GB+ for smooth operation
- **Disk Space:** ~2GB for all images and data
- **Internet:** Stable connection for AI API calls

---

## ☁️ Deployment to Cloud (100% FREE!)

### Option 1: Railway + Netlify (Recommended)

#### Prerequisites:
- GitHub account
- Railway account (free)
- Netlify account (free)

#### Step 1: Prepare for Deployment
```bash
# Build production assets
./deploy.sh  # Linux/Mac
# or
deploy.bat   # Windows

# Commit changes
git add .
git commit -m "Ready for production deployment"
git push origin main
```

#### Step 2: Deploy Backend to Railway
1. Go to [railway.app](https://railway.app/)
2. Sign up with GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your forked repository
5. Railway will auto-detect and deploy the backend
6. Add these environment variables in Railway dashboard:
   ```
   GROQ_API_KEY=your_groq_key
   TAVILY_API_KEY=your_tavily_key
   SPRING_PROFILES_ACTIVE=production
   ```
7. Add PostgreSQL and Redis add-ons (both free)
8. Note your backend URL (e.g., `your-app-name.railway.app`)

#### Step 3: Deploy Frontend to Netlify
1. Go to [netlify.com](https://netlify.com/)
2. Sign up with GitHub
3. Click "New site from Git"
4. Select your repository
5. Set build settings:
   - **Build command:** `cd frontend && npm run build`
   - **Publish directory:** `frontend/build`
6. Add environment variable:
   ```
   REACT_APP_API_URL=https://your-backend-url.railway.app
   ```
7. Deploy and note your frontend URL

### Option 2: Heroku (Alternative)
```bash
# Install Heroku CLI
# Create Heroku apps
heroku create your-backend-name
heroku create your-frontend-name

# Deploy backend
git subtree push --prefix=backend heroku main

# Deploy frontend
git subtree push --prefix=frontend heroku main
```

### Option 3: DigitalOcean App Platform
1. Connect GitHub repository
2. Select "Web Service" for backend
3. Select "Static Site" for frontend
4. Configure environment variables
5. Deploy with one click

### Cost Breakdown (Monthly)
- **Railway**: $0 (free tier: 512MB RAM, 1GB disk)
- **Netlify**: $0 (free tier: 100GB bandwidth)
- **PostgreSQL**: $0 (Railway free tier)
- **Redis**: $0 (Railway free tier)
- **Total**: **$0/month** ✨

---

## 🏗️ Architecture

### System Overview
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │ Spring Boot     │    │   PostgreSQL    │
│   (Port 3000)   │◄──►│   Backend       │◄──►│   Database      │
│                 │    │   (Port 8080)   │    │   (Port 3306)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐             │
         │              │     Redis       │             │
         └──────────────►│     Cache       │◄────────────┘
                        │   (Port 6379)   │
                        └─────────────────┘
                                 │
                        ┌─────────────────┐
                        │   External APIs │
                        │   • Groq AI     │
                        │   • Tavily      │
                        └─────────────────┘
```

### Technology Stack

#### Frontend (React)
- **React 18** - Modern UI framework
- **Material-UI** - Component library
- **EventSource** - Real-time streaming
- **Axios** - HTTP client
- **React Router** - Navigation

#### Backend (Spring Boot)
- **Spring Boot 3.2** - Application framework
- **Spring Security** - Authentication & authorization
- **Server-Sent Events** - Real-time streaming
- **JPA/Hibernate** - Database ORM
- **JWT** - Token-based authentication

#### Database Layer
- **PostgreSQL** - Primary database (production)
- **MySQL** - Development database
- **Redis** - Session cache and temporary storage

#### External Services
- **Groq API** - Large Language Model (llama-3.1-8b-instant)
- **Tavily API** - Web search and crawling

### Data Flow

1. **User Input** → Frontend captures message
2. **Authentication** → JWT token validates user
3. **API Request** → Frontend sends to backend
4. **AI Processing** → Backend calls Groq API
5. **Web Search** → Backend searches via Tavily
6. **Streaming Response** → SSE streams data to frontend
7. **Real-time UI** → Frontend updates progressively
8. **Persistence** → Final data saved to database

### Security Features

- **JWT Authentication** - Secure user sessions
- **CORS Configuration** - Controlled cross-origin requests
- **Input Validation** - Sanitized user inputs
- **Rate Limiting** - Prevents API abuse
- **Environment Variables** - Sensitive data protection

---

## 🤝 Contributing

### Development Setup

1. **Clone and setup:**
   ```bash
   git clone <repository-url>
   cd finance-research-chatbot
   cp .env.example .env
   # Edit .env with your API keys
   ```

2. **Start development environment:**
   ```bash
   docker-compose up -d
   ```

3. **For local development (optional):**
   ```bash
   # Backend (requires Java 17+)
   cd backend
   ./mvnw spring-boot:run
   
   # Frontend (requires Node.js 18+)
   cd frontend
   npm install
   npm start
   ```

### Project Structure
```
finance-research-chatbot/
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml             # Maven dependencies
├── frontend/               # React application
│   ├── src/                # React source code
│   ├── public/             # Static assets
│   └── package.json        # Node.js dependencies
├── database/               # Database initialization
├── nginx/                  # Reverse proxy config
├── docker-compose.yml      # Container orchestration
├── .env.example           # Environment template
└── README.md              # This file
```

### Making Contributions

1. **Fork the repository**
2. **Create feature branch:** `git checkout -b feature-name`
3. **Make changes and test**
4. **Commit changes:** `git commit -m "Add feature"`
5. **Push to branch:** `git push origin feature-name`
6. **Create Pull Request**

### Code Style

- **Backend:** Follow Java conventions, Spring Boot best practices
- **Frontend:** Use ESLint + Prettier configuration
- **Documentation:** Update README for significant changes
- **Testing:** Add tests for new features

---

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Groq** - Lightning-fast AI inference
- **Tavily** - Comprehensive web search
- **Spring Boot** - Robust backend framework
- **React** - Modern frontend library
- **Material-UI** - Beautiful component library
- **Docker** - Simplified deployment

---

## 📞 Support

- **Documentation**: Check this README and guides in `/docs`
- **Issues**: [GitHub Issues](https://github.com/your-username/finance-research-chatbot/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-username/finance-research-chatbot/discussions)
- **Email**: your-email@example.com

---

**🎉 Happy researching! Feel free to ask the AI about any financial topics you're curious about.**

**📖 Detailed Instructions:** See [DEPLOYMENT.md](./DEPLOYMENT.md) for complete step-by-step guide.

## 💻 Local Development

### Prerequisites
- Docker and Docker Compose
- Node.js 18+ (for local development)
- Java 17+ (for local development)

### Setup
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd finance-research-chatbot
   ```

2. **Configure environment**
   ```bash
   cp .env.example .env
   # Edit .env with your API keys
   ```

3. **Start with Docker**
   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

### Local Development

1. **Start infrastructure**
   ```bash
   docker-compose up mysql redis -d
   ```

2. **Run backend**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

3. **Run frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

## Database Schema

The application uses MySQL with the following main tables:
- `users` - User accounts and authentication
- `sessions` - JWT session management
- `threads` - Chat conversation threads
- `messages` - Individual chat messages
- `memories` - Long-term memory storage
- `sources` - Web research sources and citations

## API Documentation

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Chat Threads
- `GET /api/threads` - List user's threads
- `POST /api/threads` - Create new thread
- `GET /api/threads/{id}/messages` - Get thread messages
- `POST /api/threads/{id}/messages` - Send message (streaming)

### Research & Export
- `GET /api/threads/{id}/sources` - Get research sources
- `GET /api/threads/{id}/export` - Export thread report

## Demo Script

To test the HDFC Bank research example:

1. Register/Login to the application
2. Create a new chat thread
3. Ask: "Is HDFC Bank undervalued vs peers in last 2 quarters?"
4. Watch the AI conduct web research and stream reasoning
5. Review the cited sources in the source panel
6. Export the final report with citations

## Testing

Run the test suite:

```bash
# Backend tests
cd backend
./mvnw test

# E2E tests
npm run test:e2e
```

## Development

### Project Structure

```
finance-research-chatbot/
├── backend/               # Spring Boot application
├── frontend/             # React application
├── database/             # SQL migrations
├── docker-compose.yml    # Development environment
└── .env.example         # Configuration template
```

### Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

MIT License - see LICENSE file for details