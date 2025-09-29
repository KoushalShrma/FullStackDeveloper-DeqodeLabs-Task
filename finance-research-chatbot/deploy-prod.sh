#!/bin/bash

# Production Deployment Script for Finance Research Chatbot
# This script deploys the application to production environment

set -e

echo "🚀 Starting Finance Research Chatbot Production Deployment"

# Configuration
COMPOSE_FILE="docker-compose.prod.yml"
ENV_FILE=".env.prod"

# Check prerequisites
check_prerequisites() {
    echo "📋 Checking prerequisites..."
    
    if [ ! -f "$ENV_FILE" ]; then
        echo "❌ Error: $ENV_FILE not found"
        echo "Please copy .env.prod.example to .env.prod and configure your environment variables"
        exit 1
    fi
    
    if ! command -v docker &> /dev/null; then
        echo "❌ Error: Docker is not installed"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Error: Docker Compose is not installed"
        exit 1
    fi
    
    echo "✅ Prerequisites check passed"
}

# Create SSL certificates (self-signed for development, replace with real certs for production)
setup_ssl() {
    echo "🔒 Setting up SSL certificates..."
    
    mkdir -p nginx/ssl
    
    if [ ! -f "nginx/ssl/cert.pem" ]; then
        echo "Creating self-signed SSL certificate..."
        openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
            -keyout nginx/ssl/private.key \
            -out nginx/ssl/cert.pem \
            -subj "/C=US/ST=State/L=City/O=Organization/OU=OrgUnit/CN=localhost"
        echo "⚠️  WARNING: Using self-signed certificate. Replace with real SSL certificates for production!"
    fi
    
    echo "✅ SSL setup completed"
}

# Backup existing data
backup_data() {
    echo "💾 Creating data backup..."
    
    BACKUP_DIR="backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$BACKUP_DIR"
    
    # Backup MySQL data if container exists
    if docker container inspect finance-chatbot-mysql-prod >/dev/null 2>&1; then
        echo "Backing up MySQL data..."
        docker exec finance-chatbot-mysql-prod mysqldump -u root -p$(grep MYSQL_ROOT_PASSWORD $ENV_FILE | cut -d '=' -f2) --all-databases > "$BACKUP_DIR/mysql_backup.sql"
    fi
    
    # Backup Redis data if container exists
    if docker container inspect finance-chatbot-redis-prod >/dev/null 2>&1; then
        echo "Backing up Redis data..."
        docker exec finance-chatbot-redis-prod redis-cli --rdb /data/backup.rdb
        docker cp finance-chatbot-redis-prod:/data/backup.rdb "$BACKUP_DIR/redis_backup.rdb"
    fi
    
    echo "✅ Backup completed: $BACKUP_DIR"
}

# Build and deploy
deploy() {
    echo "🏗️  Building and deploying application..."
    
    # Pull latest images
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" pull
    
    # Build custom images
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" build --no-cache
    
    # Stop existing containers
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" down
    
    # Start new containers
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up -d
    
    echo "✅ Deployment completed"
}

# Health checks
health_check() {
    echo "🏥 Performing health checks..."
    
    # Wait for services to start
    sleep 30
    
    # Check MySQL
    if docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec mysql mysqladmin ping -h localhost &>/dev/null; then
        echo "✅ MySQL is healthy"
    else
        echo "❌ MySQL health check failed"
        return 1
    fi
    
    # Check Redis
    if docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec redis redis-cli ping &>/dev/null; then
        echo "✅ Redis is healthy"
    else
        echo "❌ Redis health check failed"
        return 1
    fi
    
    # Check Backend
    if curl -f http://localhost:8080/actuator/health &>/dev/null; then
        echo "✅ Backend is healthy"
    else
        echo "❌ Backend health check failed"
        return 1
    fi
    
    # Check Frontend via Nginx
    if curl -f http://localhost/health &>/dev/null; then
        echo "✅ Frontend is healthy"
    else
        echo "❌ Frontend health check failed"
        return 1
    fi
    
    echo "✅ All health checks passed"
}

# Show deployment status
show_status() {
    echo "📊 Deployment Status:"
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" ps
    
    echo ""
    echo "🌐 Application URLs:"
    echo "Frontend: http://localhost (redirects to HTTPS)"
    echo "Frontend (HTTPS): https://localhost"
    echo "Backend API: https://localhost/api"
    echo "Health Check: https://localhost/health"
    
    echo ""
    echo "📝 Logs:"
    echo "View logs with: docker-compose -f $COMPOSE_FILE --env-file $ENV_FILE logs -f [service]"
}

# Main deployment flow
main() {
    check_prerequisites
    setup_ssl
    
    if [ "$1" = "--with-backup" ]; then
        backup_data
    fi
    
    deploy
    health_check
    show_status
    
    echo ""
    echo "🎉 Production deployment completed successfully!"
    echo "Your Finance Research Chatbot is now running at https://localhost"
}

# Handle script arguments
case "$1" in
    --help|-h)
        echo "Usage: $0 [--with-backup] [--help]"
        echo "  --with-backup: Create backup before deployment"
        echo "  --help: Show this help message"
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac