# 🚀 QUICK START GUIDE

## Get Running in 5 Minutes! ⏱️

### Prerequisites ✅
- Docker Desktop installed
- Web browser
- 2 free API keys (instructions below)

### Step 1: Get API Keys (FREE) 🔑

#### Groq API Key:
1. Go to [console.groq.com](https://console.groq.com/)
2. Sign up with email
3. Go to "API Keys" → "Create API Key"
4. Copy the key (starts with `gsk_`)

#### Tavily API Key:
1. Go to [app.tavily.com](https://app.tavily.com/)
2. Sign up with email
3. Find "API Key" in dashboard
4. Copy the key

### Step 2: Setup Environment 📝

```bash
# Copy environment template
cp .env.example .env

# Edit .env file (use Notepad, VS Code, or any editor)
# Replace these two lines:
GROQ_API_KEY=YOUR_GROQ_API_KEY_HERE      # ← Put your Groq key here
TAVILY_API_KEY=YOUR_TAVILY_API_KEY_HERE  # ← Put your Tavily key here
```

### Step 3: Start Application 🚀

```bash
# Start all services
docker-compose up -d

# Wait 2-3 minutes for startup
# Open browser: http://localhost:3000
```

### Step 4: Use the App 🎯

1. **Register** a new account
2. **Login** with your credentials
3. **Create** a new research thread
4. **Ask** financial questions like:
   - "What's Apple's current stock price?"
   - "Analyze Tesla's Q3 earnings"
   - "Compare Microsoft vs Google revenue"

### Troubleshooting 🔧

**App not starting?**
```bash
# Check Docker is running
docker --version

# Check container status
docker-compose ps

# View logs
docker-compose logs
```

**Need detailed help?** 📖  
Check the full **README.md** for comprehensive instructions.

---

**🎉 Enjoy researching with AI!**