# 🚀 Complete API Documentation & Deployment Checklist

## 📋 **ALL API ENDPOINTS**

### 🔐 **Authentication APIs**
Base URL: `/api/auth`

#### 1. **POST** `/api/auth/register`
**Purpose**: Create new user account
**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "fullName": "John Doe"
}
```
**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "John Doe"
  }
}
```

#### 2. **POST** `/api/auth/login`
**Purpose**: User login
**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```
**Response**: Same as register

#### 3. **POST** `/api/auth/refresh`
**Purpose**: Refresh JWT token
**Request Body**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```
**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

#### 4. **POST** `/api/auth/logout`
**Purpose**: User logout
**Headers**: `Authorization: Bearer <token>`
**Response**: `200 OK`

---

### 💬 **Chat Thread APIs**
Base URL: `/api/threads`
**All endpoints require JWT authentication**

#### 1. **GET** `/api/threads`
**Purpose**: Get user's chat threads
**Headers**: `Authorization: Bearer <token>`
**Response**:
```json
[
  {
    "id": 1,
    "title": "HDFC Bank Analysis",
    "messageCount": 5,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T11:45:00Z"
  }
]
```

#### 2. **POST** `/api/threads`
**Purpose**: Create new chat thread
**Headers**: `Authorization: Bearer <token>`
**Request Body**:
```json
{
  "title": "New Research Thread"
}
```
**Response**:
```json
{
  "id": 2,
  "title": "New Research Thread",
  "messageCount": 0,
  "createdAt": "2024-01-15T12:00:00Z",
  "updatedAt": "2024-01-15T12:00:00Z"
}
```

#### 3. **GET** `/api/threads/{threadId}`
**Purpose**: Get specific thread details
**Headers**: `Authorization: Bearer <token>`
**Response**: Same as thread object above

#### 4. **GET** `/api/threads/{threadId}/messages`
**Purpose**: Get messages in a thread
**Headers**: `Authorization: Bearer <token>`
**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 50)

**Response**:
```json
{
  "content": [
    {
      "id": 1,
      "content": "What is HDFC Bank's current financial position?",
      "role": "user",
      "timestamp": "2024-01-15T10:30:00Z",
      "reasoningTrace": null,
      "sources": []
    },
    {
      "id": 2,
      "content": "HDFC Bank is one of India's largest private sector banks...",
      "role": "assistant",
      "timestamp": "2024-01-15T10:31:00Z",
      "reasoningTrace": "Let me analyze HDFC Bank's financial position...",
      "sources": [
        {
          "id": 1,
          "url": "https://www.hdfcbank.com/annual-report",
          "title": "HDFC Bank Annual Report 2024",
          "snippet": "Financial highlights and performance metrics..."
        }
      ]
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "currentPage": 0
}
```

#### 5. **POST** `/api/threads/{threadId}/messages`
**Purpose**: Send message to thread
**Headers**: `Authorization: Bearer <token>`
**Request Body**:
```json
{
  "content": "What is HDFC Bank's ROE for 2024?",
  "showThinking": true
}
```
**Response**: Same as message object above

---

### 🏥 **Health Check API**

#### 1. **GET** `/api/health`
**Purpose**: Check service health
**Response**:
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T12:00:00Z",
  "service": "Finance Research Chatbot API"
}
```

---

## 🔑 **REQUIRED API KEYS & CREDENTIALS**

### **1. JWT Configuration**
```bash
# Generate with: openssl rand -base64 64
JWT_SECRET=your-super-secure-256-bit-secret-here
JWT_EXPIRATION=86400000  # 24 hours in milliseconds
```

### **2. LLM Provider (Choose ONE)**

#### Groq (Recommended - Fast & Free)
```bash
LLM_PROVIDER=groq
GROQ_API_KEY=gsk-your-groq-key-here
```
- **Get Key**: https://console.groq.com/keys
- **Cost**: Free tier with generous limits
- **Models**: Llama 3.1, Mixtral, Gemma

#### Google Gemini (Alternative)
```bash
LLM_PROVIDER=gemini
GEMINI_API_KEY=AIza-your-gemini-key-here
```
- **Get Key**: https://ai.google.dev/
- **Cost**: Free tier available
- **Models**: Gemini Pro, Gemini Flash
```bash
LLM_PROVIDER=gemini
GEMINI_API_KEY=AIza-your-gemini-key-here
```
- **Get Key**: https://ai.google.dev/
- **Cost**: Free tier available
- **Models**: Gemini Pro, Gemini Flash

#### Anthropic Claude (Alternative)
```bash
LLM_PROVIDER=claude
CLAUDE_API_KEY=sk-ant-your-claude-key-here
```
- **Get Key**: https://console.anthropic.com/
- **Cost**: Pay per use
- **Models**: Claude 3.5 Sonnet, Claude 3 Haiku

### **3. Web Search Provider (Choose ONE)**

#### Tavily (Recommended)
```bash
SEARCH_PROVIDER=tavily
TAVILY_API_KEY=tvly-your-tavily-key-here
```
- **Get Key**: https://app.tavily.com/
- **Free Tier**: 1,000 searches/month
- **Features**: AI-optimized search results

#### Brave Search
```bash
SEARCH_PROVIDER=brave
BRAVE_API_KEY=BSA-your-brave-key-here
```
- **Get Key**: https://api.search.brave.com/
- **Free Tier**: 2,000 queries/month
- **Features**: Privacy-focused search

#### SerpAPI
```bash
SEARCH_PROVIDER=serper
SERPER_API_KEY=your-serper-key-here
```
- **Get Key**: https://serpapi.com/
- **Free Tier**: 100 searches/month
- **Features**: Google search results

---

## 🐳 **DEPLOYMENT REQUIREMENTS**

### **1. Infrastructure Requirements**

#### **Docker & Docker Compose**
- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 4GB RAM
- 10GB free disk space

#### **Database Requirements**
- MySQL 8.0
- Redis 7.0
- Persistent storage volumes

#### **Network Requirements**
- Ports: 3000 (frontend), 8080 (backend), 3306 (mysql), 6379 (redis)
- Internet access for API calls

### **2. Environment Configuration**

Create `.env` file with:
```bash
# CRITICAL - MUST SET
JWT_SECRET=your-generated-secret-here
GROQ_API_KEY=gsk-your-groq-key
TAVILY_API_KEY=tvly-your-tavily-key
LLM_PROVIDER=groq
SEARCH_PROVIDER=tavily

# AUTO-CONFIGURED FOR DOCKER
DB_HOST=localhost
DB_PORT=3306
DB_NAME=finance_research
DB_USERNAME=app_user
DB_PASSWORD=app_password
REDIS_HOST=localhost
REDIS_PORT=6379

# OPTIONAL SETTINGS
CORS_ALLOWED_ORIGINS=http://localhost:3000
LOG_LEVEL=INFO
MAX_CRAWL_PAGES=10
CRAWL_TIMEOUT=30000
SHORT_TERM_MEMORY_TTL=3600
MAX_THREAD_MESSAGES=100
```

### **3. Deployment Steps**

#### **Local Development**
```bash
# 1. Clone and setup
git clone <repo-url>
cd finance-research-chatbot
cp .env.template .env
# Edit .env with your API keys

# 2. Start services
docker-compose up -d

# 3. Verify deployment
curl http://localhost:8080/api/health
open http://localhost:3000
```

#### **Production Deployment**
```bash
# 1. Secure environment
export JWT_SECRET=$(openssl rand -base64 64)
export OPENAI_API_KEY=your-production-key
export TAVILY_API_KEY=your-production-key

# 2. Production compose
docker-compose -f docker-compose.prod.yml up -d

# 3. Setup SSL/TLS and reverse proxy
# Configure nginx/cloudflare for HTTPS
```

---

## ✅ **PRE-DEPLOYMENT CHECKLIST**

### **Before Starting Deployment**

- [ ] **API Keys Obtained**
  - [ ] OpenAI API key (or Gemini/Claude)
  - [ ] Tavily API key (or Brave/SerpAPI)
  - [ ] JWT secret generated

- [ ] **Infrastructure Ready**
  - [ ] Docker installed and running
  - [ ] Docker Compose available
  - [ ] Ports 3000, 8080, 3306, 6379 available
  - [ ] At least 4GB RAM available

- [ ] **Configuration Files**
  - [ ] `.env` file created with all required values
  - [ ] API keys tested and working
  - [ ] Database credentials set

### **During Deployment**

- [ ] **Services Start Successfully**
  - [ ] MySQL container healthy
  - [ ] Redis container healthy
  - [ ] Backend container healthy
  - [ ] Frontend container healthy

- [ ] **Health Checks Pass**
  - [ ] `curl http://localhost:8080/api/health` returns 200
  - [ ] Frontend loads at `http://localhost:3000`
  - [ ] Database schema initialized
  - [ ] Redis connection working

### **Post-Deployment Testing**

- [ ] **Authentication Flow**
  - [ ] User registration works
  - [ ] User login works
  - [ ] JWT tokens generated
  - [ ] Protected routes secured

- [ ] **Chat Functionality**
  - [ ] Thread creation works
  - [ ] Message sending works
  - [ ] Message history retrieval works
  - [ ] AI responses generated (when LLM integrated)

---

## 🚨 **TROUBLESHOOTING COMMON ISSUES**

### **Database Connection Failed**
```bash
# Check MySQL status
docker-compose ps mysql
docker-compose logs mysql

# Restart if needed
docker-compose restart mysql
```

### **Backend Won't Start**
```bash
# Check environment variables
docker-compose config

# Check backend logs
docker-compose logs backend

# Common issues:
# - Missing JWT_SECRET
# - Invalid database credentials
# - Port conflicts
```

### **Frontend Can't Connect**
```bash
# Check backend health
curl http://localhost:8080/api/health

# Check CORS settings
# Verify CORS_ALLOWED_ORIGINS includes frontend URL
```

### **API Key Issues**
```bash
# Test API keys manually
curl -H "Authorization: Bearer $OPENAI_API_KEY" https://api.openai.com/v1/models

# Check logs for API errors
docker-compose logs backend | grep -i error
```

---

## 💰 **COST ESTIMATION**

### **Monthly Costs (Moderate Usage)**
- **Groq API**: Free tier (very generous limits)
- **Tavily Search**: Free (1K searches)
- **Infrastructure**: $0 (local) or $10-50/month (cloud)
- **Total**: $0-50/month depending on usage (FREE to start!)

### **Free Tier Limits**
- **Groq**: Very generous free tier with high rate limits
- **Tavily**: 1,000 searches/month
- **Brave**: 2,000 searches/month
- **Gemini**: 15 requests/minute free

---

## 🎯 **READY FOR DEPLOYMENT!**

Once you have:
1. ✅ API keys configured
2. ✅ Environment variables set
3. ✅ Docker running

**Simply run**: `docker-compose up -d`

Your Finance Research Chatbot will be live at:
- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080

**All APIs are documented and ready to use!** 🚀