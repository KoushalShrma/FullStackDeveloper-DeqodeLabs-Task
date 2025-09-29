@echo off
REM Production Deployment Script for Finance Research Chatbot (Windows)
REM This script deploys the application to production environment

echo 🚀 Starting Finance Research Chatbot Production Deployment

REM Configuration
set COMPOSE_FILE=docker-compose.prod.yml
set ENV_FILE=.env.prod

REM Check prerequisites
echo 📋 Checking prerequisites...

if not exist "%ENV_FILE%" (
    echo ❌ Error: %ENV_FILE% not found
    echo Please copy .env.prod.example to .env.prod and configure your environment variables
    exit /b 1
)

docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Docker is not installed or not in PATH
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Docker Compose is not installed or not in PATH
    exit /b 1
)

echo ✅ Prerequisites check passed

REM Create SSL certificates (self-signed for development)
echo 🔒 Setting up SSL certificates...
if not exist "nginx\ssl" mkdir nginx\ssl

if not exist "nginx\ssl\cert.pem" (
    echo Creating self-signed SSL certificate...
    echo ⚠️  WARNING: Using self-signed certificate. Replace with real SSL certificates for production!
    REM For Windows, you might need to install OpenSSL or use PowerShell
    powershell -Command "New-SelfSignedCertificate -DnsName 'localhost' -CertStoreLocation 'cert:\LocalMachine\My' -FriendlyName 'Finance Chatbot SSL'"
)

echo ✅ SSL setup completed

REM Build and deploy
echo 🏗️  Building and deploying application...

REM Pull latest images
docker-compose -f %COMPOSE_FILE% --env-file %ENV_FILE% pull

REM Build custom images
docker-compose -f %COMPOSE_FILE% --env-file %ENV_FILE% build --no-cache

REM Stop existing containers
docker-compose -f %COMPOSE_FILE% --env-file %ENV_FILE% down

REM Start new containers
docker-compose -f %COMPOSE_FILE% --env-file %ENV_FILE% up -d

echo ✅ Deployment completed

REM Wait for services to start
echo 🏥 Performing health checks...
timeout /t 30 /nobreak

REM Show deployment status
echo 📊 Deployment Status:
docker-compose -f %COMPOSE_FILE% --env-file %ENV_FILE% ps

echo.
echo 🌐 Application URLs:
echo Frontend: http://localhost (redirects to HTTPS)
echo Frontend (HTTPS): https://localhost
echo Backend API: https://localhost/api
echo Health Check: https://localhost/health

echo.
echo 📝 Logs:
echo View logs with: docker-compose -f %COMPOSE_FILE% --env-file %ENV_FILE% logs -f [service]

echo.
echo 🎉 Production deployment completed successfully!
echo Your Finance Research Chatbot is now running at https://localhost

pause