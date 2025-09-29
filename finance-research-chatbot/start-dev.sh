#!/bin/bash

# Finance Research Chatbot - Development Startup Script

echo "🚀 Starting Finance Research Chatbot Development Environment..."

# Check if .env file exists
if [ ! -f .env ]; then
    echo "📋 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  Please edit .env file with your API keys before proceeding!"
    read -p "Press Enter after editing .env file..."
fi

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

echo "🐳 Starting database and cache services..."
docker-compose up -d mysql redis

echo "⏳ Waiting for database to be ready..."
until docker-compose exec mysql mysqladmin ping -h"localhost" --silent; do
    echo "Waiting for database connection..."
    sleep 2
done

echo "✅ Database is ready!"

echo "🏗️  Building and starting backend..."
cd backend
mvn clean install -DskipTests
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

echo "⏳ Waiting for backend to start..."
until curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; do
    echo "Waiting for backend..."
    sleep 3
done

echo "✅ Backend is ready!"

echo "🎨 Starting frontend..."
cd frontend
npm install
npm start &
FRONTEND_PID=$!
cd ..

echo "🎉 Development environment is ready!"
echo ""
echo "📱 Frontend: http://localhost:3000"
echo "⚙️  Backend API: http://localhost:8080"
echo "🗄️  Database: localhost:3306"
echo "📦 Redis: localhost:6379"
echo ""
echo "Press Ctrl+C to stop all services"

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "🛑 Stopping services..."
    kill $BACKEND_PID 2>/dev/null
    kill $FRONTEND_PID 2>/dev/null
    docker-compose stop
    echo "✅ All services stopped"
    exit 0
}

# Trap Ctrl+C
trap cleanup SIGINT

# Wait for user to stop
wait