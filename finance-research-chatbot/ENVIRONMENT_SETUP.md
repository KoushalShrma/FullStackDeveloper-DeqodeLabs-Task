# 🔧 Environment Variables Configuration Guide

Copy this file to `.env` in your project root and fill in your API keys and configuration values.

```bash
# ==============================================================================
# REQUIRED ENVIRONMENT VARIABLES
# ==============================================================================

# ✅ JWT Security (REQUIRED)
# Generate a secure 256-bit secret key for JWT token signing
# You can generate one using: openssl rand -base64 64
JWT_SECRET=your-super-secure-256-bit-jwt-secret-key-change-this-in-production-make-it-very-long-and-random

# ✅ Database Configuration (REQUIRED for local development)
# For Docker setup, these are automatically configured
DB_HOST=localhost
DB_PORT=3306
DB_NAME=finance_research
DB_USERNAME=app_user
DB_PASSWORD=app_password

# ✅ Redis Configuration (REQUIRED for local development)
# For Docker setup, these are automatically configured
REDIS_HOST=localhost
REDIS_PORT=6379

# ==============================================================================
# AI/LLM PROVIDER CONFIGURATION (CHOOSE ONE)
# ==============================================================================

# 🤖 LLM Provider Selection
# Options: openai, gemini, claude
LLM_PROVIDER=openai

# 🔑 OpenAI API Key (RECOMMENDED)
# Get from: https://platform.openai.com/api-keys
# Format: sk-...
OPENAI_API_KEY=your-openai-api-key-here

# 🔑 Google Gemini API Key (ALTERNATIVE)
# Get from: https://ai.google.dev/
# Format: AI...
GEMINI_API_KEY=your-gemini-api-key-here

# 🔑 Anthropic Claude API Key (ALTERNATIVE)
# Get from: https://console.anthropic.com/
# Format: sk-ant-...
CLAUDE_API_KEY=your-claude-api-key-here

# ==============================================================================
# WEB SEARCH PROVIDER CONFIGURATION (CHOOSE ONE)
# ==============================================================================

# 🔍 Search Provider Selection
# Options: tavily, brave, serper
SEARCH_PROVIDER=tavily

# 🔑 Tavily API Key (RECOMMENDED for web search)
# Get from: https://app.tavily.com/
# Format: tvly-...
TAVILY_API_KEY=your-tavily-api-key-here

# 🔑 Brave Search API Key (ALTERNATIVE)
# Get from: https://api.search.brave.com/
# Format: BSA...
BRAVE_API_KEY=your-brave-search-api-key-here

# 🔑 SerpAPI Key (ALTERNATIVE)
# Get from: https://serpapi.com/
# Format: random string
SERPER_API_KEY=your-serper-api-key-here

# ==============================================================================
# OPTIONAL CONFIGURATION (Has sensible defaults)
# ==============================================================================

# ⚙️ Application Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
LOG_LEVEL=INFO
JWT_EXPIRATION=86400000

# 🕷️ Web Crawling Configuration
MAX_CRAWL_PAGES=10
CRAWL_TIMEOUT=30000

# 🧠 Memory Configuration
SHORT_TERM_MEMORY_TTL=3600
MAX_THREAD_MESSAGES=100

# 🎨 Frontend Configuration
REACT_APP_API_BASE_URL=http://localhost:8080

# ==============================================================================
# DOCKER-SPECIFIC OVERRIDES (Automatically set in docker-compose.yml)
# ==============================================================================

# These are automatically configured when using Docker:
# SPRING_PROFILES_ACTIVE=docker
# SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/finance_chatbot
# SPRING_DATASOURCE_USERNAME=chatbot_user
# SPRING_DATASOURCE_PASSWORD=chatbot_password
# SPRING_DATA_REDIS_HOST=redis
# SPRING_DATA_REDIS_PORT=6379

```

## 🚨 CRITICAL - MUST CONFIGURE

### 1. JWT_SECRET (REQUIRED)
**Purpose**: Signs and verifies JWT tokens for user authentication
**How to generate**:
```bash
# Option 1: OpenSSL
openssl rand -base64 64

# Option 2: Online generator
# Visit: https://generate-secret.vercel.app/64

# Option 3: Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

### 2. LLM API Key (REQUIRED for AI functionality)
**Choose ONE of these**:

#### OpenAI (Recommended)
- **Get Key**: https://platform.openai.com/api-keys
- **Format**: `sk-proj-...` or `sk-...`
- **Cost**: Pay-per-use, ~$0.002/1K tokens
- **Set**: `OPENAI_API_KEY=sk-your-key`

#### Google Gemini (Alternative)
- **Get Key**: https://ai.google.dev/
- **Format**: `AIza...`
- **Cost**: Free tier available
- **Set**: `GEMINI_API_KEY=AIza-your-key`

#### Anthropic Claude (Alternative)
- **Get Key**: https://console.anthropic.com/
- **Format**: `sk-ant-...`
- **Cost**: Pay-per-use
- **Set**: `CLAUDE_API_KEY=sk-ant-your-key`

### 3. Web Search API Key (REQUIRED for research functionality)
**Choose ONE of these**:

#### Tavily (Recommended)
- **Get Key**: https://app.tavily.com/
- **Format**: `tvly-...`
- **Features**: AI-optimized search results
- **Cost**: Free tier: 1000 searches/month
- **Set**: `TAVILY_API_KEY=tvly-your-key`

#### Brave Search (Alternative)
- **Get Key**: https://api.search.brave.com/
- **Format**: `BSA...`
- **Features**: Privacy-focused search
- **Cost**: Free tier: 2000 queries/month
- **Set**: `BRAVE_API_KEY=BSA-your-key`

#### SerpAPI (Alternative)
- **Get Key**: https://serpapi.com/
- **Format**: Random string
- **Features**: Google search results
- **Cost**: Free tier: 100 searches/month
- **Set**: `SERPER_API_KEY=your-key`

## 📝 Example .env File

```bash
# Minimal working configuration
JWT_SECRET=super-long-random-secret-key-generated-with-openssl-rand-base64-64-change-this
OPENAI_API_KEY=sk-proj-your-openai-key-here
TAVILY_API_KEY=tvly-your-tavily-key-here
LLM_PROVIDER=openai
SEARCH_PROVIDER=tavily
```

## 🧪 Testing Configuration

To test if your environment is properly configured:

1. **Start the services**:
   ```bash
   docker-compose up -d
   ```

2. **Check backend health**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

3. **Test API keys** (the backend will log warnings for missing keys)

## 🔒 Security Notes

- **Never commit** your `.env` file to version control
- **Generate a unique JWT_SECRET** for each environment
- **Rotate API keys** regularly
- **Use different secrets** for development/staging/production
- **Limit API key permissions** where possible

## 💰 Cost Estimates

With the recommended providers:
- **OpenAI**: ~$5-20/month for moderate usage
- **Tavily**: Free tier covers ~1000 research queries
- **Total**: ~$5-20/month for a personal research tool

## 🚨 Troubleshooting

**Backend won't start**: Check JWT_SECRET is set
**AI not responding**: Verify LLM_PROVIDER and API key
**No search results**: Verify SEARCH_PROVIDER and API key
**Database connection**: Check if MySQL/Redis containers are running