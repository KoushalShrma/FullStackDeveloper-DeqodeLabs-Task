# 🚀 Groq API Configuration Guide

## 🎯 **Why Groq?**

Groq offers **ultra-fast LLM inference** with excellent performance:

- ⚡ **Lightning Fast**: 100+ tokens/second inference speed
- 💰 **Cost Effective**: Very generous free tier
- 🤖 **Quality Models**: Llama 3.1, Mixtral, Gemma models
- 🔧 **Easy Integration**: OpenAI-compatible API

## 🔑 **Getting Your Groq API Key**

### **Step 1: Sign Up**
1. Go to: https://console.groq.com/
2. Click "Sign Up" 
3. Use your email or GitHub account

### **Step 2: Get API Key**
1. After login, go to: https://console.groq.com/keys
2. Click "Create API Key"
3. Give it a name (e.g., "Finance Chatbot")
4. Copy the key (starts with `gsk_`)

### **Step 3: Configure Environment**
Add to your `.env` file:
```bash
LLM_PROVIDER=groq
GROQ_API_KEY=gsk_your_actual_groq_api_key_here
```

## 🤖 **Available Models**

Groq supports these high-quality models:

### **Llama 3.1 (Recommended)**
- **Model ID**: `llama-3.1-8b-instant`
- **Best for**: General chat, reasoning, analysis
- **Speed**: Ultra-fast (100+ tokens/sec)

### **Mixtral**
- **Model ID**: `mixtral-8x7b-32768`
- **Best for**: Complex reasoning, longer context
- **Context**: 32K tokens

### **Gemma**
- **Model ID**: `gemma-7b-it`
- **Best for**: Lightweight, efficient responses
- **Speed**: Very fast

## 💰 **Pricing & Limits**

### **Free Tier (Perfect for Development)**
- **Rate Limits**: 30 requests/minute
- **Daily Limits**: Generous usage quotas
- **Models**: Access to all models
- **Cost**: $0

### **Pay-as-you-go (If needed)**
- **Llama 3.1**: $0.27/1M tokens
- **Mixtral**: $0.27/1M tokens  
- **Gemma**: $0.10/1M tokens

**Note**: The free tier is very generous and should be sufficient for development and moderate usage!

## ⚙️ **Configuration in Application**

The application will automatically use Groq when configured:

### **Backend Configuration**
```properties
# application.properties
app.llm.provider=groq
app.llm.groq.api-key=${GROQ_API_KEY}
```

### **Docker Configuration**
```yaml
# docker-compose.yml
environment:
  LLM_PROVIDER: groq
  GROQ_API_KEY: ${GROQ_API_KEY}
```

## 🔧 **Testing Your Setup**

### **Test API Key**
```bash
# Test with curl
curl -X POST "https://api.groq.com/openai/v1/chat/completions" \
  -H "Authorization: Bearer gsk_your_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "Hello!"}],
    "model": "llama-3.1-8b-instant"
  }'
```

### **Verify in Application**
1. Start your application: `docker-compose up -d`
2. Check backend logs: `docker-compose logs backend`
3. Look for: "LLM Provider: groq" in startup logs

## 🚀 **Performance Benefits**

### **Speed Comparison**
- **Groq**: 100+ tokens/second
- **OpenAI**: 20-40 tokens/second
- **Other providers**: 10-30 tokens/second

### **Cost Comparison** (per 1M tokens)
- **Groq**: $0.27 (after free tier)
- **OpenAI GPT-4**: $30
- **OpenAI GPT-3.5**: $2

### **Latency**
- **Groq**: <500ms first token
- **Others**: 1-3 seconds first token

## 💡 **Best Practices**

### **Model Selection**
- **Development**: Use `llama-3.1-8b-instant` for speed
- **Production**: Consider `mixtral-8x7b-32768` for quality
- **Cost-sensitive**: Use `gemma-7b-it` for efficiency

### **Rate Limiting**
- Free tier: 30 requests/minute
- Implement request queuing for high-volume usage
- Consider upgrading to paid tier if needed

### **Error Handling**
- Handle rate limit errors (429)
- Implement retry logic with exponential backoff
- Have fallback to other providers if needed

## 🔍 **Troubleshooting**

### **Common Issues**

#### **Invalid API Key**
```
Error: 401 Unauthorized
```
**Solution**: Check your API key format (should start with `gsk_`)

#### **Rate Limit Exceeded**
```
Error: 429 Too Many Requests
```
**Solution**: Wait a minute or upgrade to paid tier

#### **Model Not Found**
```
Error: 404 Model not found
```
**Solution**: Use supported model names like `llama-3.1-8b-instant`

### **Debug Steps**
1. Verify API key in Groq console
2. Test API key with curl command above
3. Check application logs for error messages
4. Ensure environment variables are set correctly

## ✅ **Complete Setup Example**

Your final `.env` file should look like:
```bash
# JWT Secret
JWT_SECRET=your-super-secure-jwt-secret-here

# Groq Configuration
LLM_PROVIDER=groq
GROQ_API_KEY=gsk_your_actual_groq_api_key_here

# Search Configuration  
SEARCH_PROVIDER=tavily
TAVILY_API_KEY=tvly_your_tavily_key_here

# Database (auto-configured for Docker)
DB_HOST=localhost
DB_PORT=3306
DB_NAME=finance_research
DB_USERNAME=app_user
DB_PASSWORD=app_password

# Redis (auto-configured for Docker)
REDIS_HOST=localhost
REDIS_PORT=6379
```

## 🎉 **You're Ready!**

With Groq configured, your Finance Research Chatbot will have:
- ⚡ **Lightning-fast responses**
- 💰 **Free tier usage**
- 🤖 **High-quality AI models**
- 🔧 **Easy scaling when needed**

Start your deployment with: `docker-compose up -d` 🚀