# 🚀 Free Deployment Guide for Finance Research Chatbot

## 📋 Prerequisites
- GitHub account
- Railway account (sign up at railway.app)
- Netlify account (sign up at netlify.com)

## 🗃️ Required API Keys
Before deployment, gather these free API keys:
- **Groq API Key**: Sign up at https://console.groq.com/
- **Tavily API Key**: Sign up at https://app.tavily.com/
- **JWT Secret**: Generate a random 256-bit string

## 🚂 Step 1: Deploy Backend to Railway

### 1.1 Prepare Repository
```bash
# Push your code to GitHub first
git add .
git commit -m "Prepare for Railway deployment"
git push origin main
```

### 1.2 Deploy to Railway
1. Go to [Railway.app](https://railway.app)
2. Click "Login with GitHub"
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your `finance-research-chatbot` repository
5. Railway will detect the Spring Boot app in the `backend` folder

### 1.3 Configure Environment Variables
In Railway dashboard, go to your project → Variables tab and add:

```env
# Database (Railway will auto-provide DATABASE_URL)
SPRING_PROFILES_ACTIVE=production

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-256-bits-long

# API Keys
GROQ_API_KEY=your-groq-api-key
TAVILY_API_KEY=your-tavily-api-key

# CORS Origins (update after frontend deployment)
FRONTEND_URL=https://your-app-name.netlify.app
```

### 1.4 Add Database & Redis
1. In Railway dashboard, click "New" → "Add PostgreSQL"
2. Click "New" → "Add Redis"
3. Railway will automatically set `DATABASE_URL` and `REDIS_URL`

## 🌐 Step 2: Deploy Frontend to Netlify

### 2.1 Prepare Frontend
Update the API URL in your frontend code after Railway deployment:
```bash
# In frontend/.env.production, update:
REACT_APP_API_URL=https://your-railway-app-url.railway.app
```

### 2.2 Deploy to Netlify
1. Go to [Netlify.com](https://netlify.com)
2. Click "Add new site" → "Import an existing project"
3. Connect to GitHub and select your repository
4. Configure build settings:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `frontend/build`

### 2.3 Environment Variables in Netlify
In Netlify dashboard → Site settings → Environment variables:
```env
REACT_APP_API_URL=https://your-railway-app-url.railway.app
REACT_APP_ENVIRONMENT=production
```

## 🔧 Step 3: Update CORS Configuration

After both deployments, update Railway environment variables:
```env
FRONTEND_URL=https://your-app-name.netlify.app,http://localhost:3000
```

## 🎯 Alternative Deployment Options

### Option 2: Render (All-in-One)
- **Backend**: Render Web Service (Free tier)
- **Frontend**: Render Static Site (Free tier)
- **Database**: Render PostgreSQL (Free tier)

### Option 3: Vercel + Railway
- **Backend**: Railway (Free tier)
- **Frontend**: Vercel (Free tier)
- **Database**: Railway PostgreSQL

## 💰 Cost Breakdown (All FREE!)

### Railway Free Tier:
- $5 monthly credit (usually enough for small apps)
- 512MB RAM
- PostgreSQL + Redis included
- Custom domains

### Netlify Free Tier:
- Unlimited bandwidth
- 100GB data transfer
- Global CDN
- Custom domains

## 🔍 Troubleshooting

### Common Issues:
1. **CORS Errors**: Ensure FRONTEND_URL includes your Netlify domain
2. **Database Connection**: Check DATABASE_URL is set in Railway
3. **Build Failures**: Ensure PostgreSQL dependency is in pom.xml
4. **API Not Found**: Verify REACT_APP_API_URL points to Railway URL

### Health Check Endpoints:
- Backend health: `https://your-railway-app.railway.app/actuator/health`
- Test API: `https://your-railway-app.railway.app/api/health`

## 🚀 Go Live!

1. **Backend URL**: `https://your-app-name.railway.app`
2. **Frontend URL**: `https://your-app-name.netlify.app`
3. **Total Cost**: $0 per month! 🎉

## 📱 Features Available After Deployment:
- ✅ Real-time AI streaming responses
- ✅ Financial research capabilities
- ✅ Source citations and tracking
- ✅ Report generation and export
- ✅ User authentication and sessions
- ✅ Responsive mobile design
- ✅ Global CDN for fast loading

Your Finance Research Chatbot will be live and accessible worldwide! 🌍