@echo off
echo 🚀 Starting Finance Research Chatbot Development Environment...

:: Check if .env file exists
if not exist .env (
    echo 📋 Creating .env file from template...
    copy .env.example .env
    echo ⚠️  Please edit .env file with your API keys before proceeding!
    pause
)

:: Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not running. Please start Docker and try again.
    pause
    exit /b 1
)

echo 🐳 Starting database and cache services...
docker-compose up -d mysql redis

echo ⏳ Waiting for database to be ready...
:wait_db
docker-compose exec mysql mysqladmin ping -h"localhost" --silent >nul 2>&1
if errorlevel 1 (
    echo Waiting for database connection...
    timeout /t 2 >nul
    goto wait_db
)

echo ✅ Database is ready!

echo 🏗️  Building and starting backend...
cd backend
start "Backend" cmd /k "mvn clean install -DskipTests && mvn spring-boot:run"
cd ..

echo ⏳ Waiting for backend to start...
:wait_backend
curl -f http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo Waiting for backend...
    timeout /t 3 >nul
    goto wait_backend
)

echo ✅ Backend is ready!

echo 🎨 Starting frontend...
cd frontend
start "Frontend" cmd /k "npm install && npm start"
cd ..

echo 🎉 Development environment is ready!
echo.
echo 📱 Frontend: http://localhost:3000
echo ⚙️  Backend API: http://localhost:8080
echo 🗄️  Database: localhost:3306
echo 📦 Redis: localhost:6379
echo.
echo Press any key to open the application in your browser...
pause >nul

start http://localhost:3000

echo.
echo To stop all services:
echo 1. Close the Backend and Frontend command windows
echo 2. Run: docker-compose stop
pause