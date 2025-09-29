@echo off
echo 🚀 Preparing Finance Research Chatbot for Deployment...

REM Backend preparation
echo 📦 Building backend...
cd backend
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ❌ Backend build failed!
    pause
    exit /b 1
)
echo ✅ Backend built successfully!

REM Frontend preparation  
echo 📦 Building frontend...
cd ..\frontend
call npm install
if %errorlevel% neq 0 (
    echo ❌ Frontend npm install failed!
    pause
    exit /b 1
)
call npm run build
if %errorlevel% neq 0 (
    echo ❌ Frontend build failed!
    pause
    exit /b 1
)
echo ✅ Frontend built successfully!

cd ..

echo.
echo 🎉 Build complete! Ready for deployment.
echo.
echo Next steps:
echo 1. Push to GitHub: git add . && git commit -m "Ready for deployment" && git push
echo 2. Deploy backend to Railway: Connect GitHub repo to Railway
echo 3. Deploy frontend to Netlify: Connect GitHub repo to Netlify  
echo 4. Configure environment variables as per DEPLOYMENT.md
echo.
echo 📖 See DEPLOYMENT.md for detailed instructions
pause