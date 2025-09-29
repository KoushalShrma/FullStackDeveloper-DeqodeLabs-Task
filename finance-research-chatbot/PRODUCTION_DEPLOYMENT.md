# Finance Research Chatbot - Production Deployment Guide

## 🚀 Deployment Options

### Option 1: Local Production Deployment (Recommended for Testing)

1. **Prerequisites**
   - Docker and Docker Compose installed
   - At least 4GB RAM available
   - Ports 80, 443 available

2. **Quick Deployment**
   ```bash
   # Copy environment configuration
   cp .env.prod.example .env.prod
   
   # Edit .env.prod with your API keys and secure passwords
   nano .env.prod
   
   # Deploy (Linux/macOS)
   chmod +x deploy-prod.sh
   ./deploy-prod.sh
   
   # Deploy (Windows)
   deploy-prod.bat
   ```

3. **Access Your Application**
   - Frontend: https://localhost
   - API: https://localhost/api
   - Health Check: https://localhost/health

---

### Option 2: AWS EC2 Deployment

1. **Launch EC2 Instance**
   ```bash
   # Recommended: t3.medium or larger
   # OS: Ubuntu 22.04 LTS
   # Security Groups: Allow ports 22, 80, 443
   ```

2. **Server Setup**
   ```bash
   # Connect to your EC2 instance
   ssh -i your-key.pem ubuntu@your-ec2-ip
   
   # Update system
   sudo apt update && sudo apt upgrade -y
   
   # Install Docker
   curl -fsSL https://get.docker.com -o get-docker.sh
   sh get-docker.sh
   sudo usermod -aG docker ubuntu
   
   # Install Docker Compose
   sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   
   # Reboot for Docker group changes
   sudo reboot
   ```

3. **Deploy Application**
   ```bash
   # Clone repository
   git clone https://github.com/your-username/finance-research-chatbot.git
   cd finance-research-chatbot
   
   # Configure environment
   cp .env.prod.example .env.prod
   nano .env.prod  # Add your API keys and secure passwords
   
   # Deploy
   chmod +x deploy-prod.sh
   ./deploy-prod.sh
   ```

4. **Domain Setup**
   - Point your domain to EC2 public IP
   - Update nginx/nginx.conf with your domain name
   - Get SSL certificate from Let's Encrypt:
   ```bash
   sudo apt install certbot python3-certbot-nginx
   sudo certbot --nginx -d your-domain.com
   ```

---

### Option 3: Digital Ocean Droplet

1. **Create Droplet**
   - Size: 2GB RAM minimum (4GB recommended)
   - OS: Ubuntu 22.04
   - Add SSH key

2. **Setup (Same as AWS EC2)**
   ```bash
   # Follow the same steps as AWS EC2 deployment
   ```

---

### Option 4: Google Cloud Platform (GCP)

1. **Create VM Instance**
   ```bash
   # Using gcloud CLI
   gcloud compute instances create finance-chatbot \
     --image-family=ubuntu-2204-lts \
     --image-project=ubuntu-os-cloud \
     --machine-type=e2-standard-2 \
     --boot-disk-size=20GB \
     --tags=http-server,https-server
   
   # Allow HTTP/HTTPS traffic
   gcloud compute firewall-rules create allow-http --allow tcp:80 --source-ranges 0.0.0.0/0 --target-tags http-server
   gcloud compute firewall-rules create allow-https --allow tcp:443 --source-ranges 0.0.0.0/0 --target-tags https-server
   ```

2. **Deploy (Same process as above)**

---

### Option 5: Docker Swarm (Multi-node)

1. **Initialize Swarm**
   ```bash
   # On manager node
   docker swarm init
   
   # On worker nodes
   docker swarm join --token SWMTKN-... manager-ip:2377
   ```

2. **Deploy Stack**
   ```bash
   # Create production stack file
   cp docker-compose.prod.yml docker-stack.yml
   
   # Deploy
   docker stack deploy -c docker-stack.yml finance-chatbot
   ```

---

### Option 6: Kubernetes Deployment

1. **Create Kubernetes Manifests**
   ```yaml
   # See k8s/ directory for complete manifests
   kubectl apply -f k8s/
   ```

---

## 🔧 Configuration

### Required Environment Variables

```bash
# Database
MYSQL_ROOT_PASSWORD=your-secure-password
MYSQL_USER=finance_user
MYSQL_PASSWORD=your-db-password

# Redis
REDIS_PASSWORD=your-redis-password

# JWT
JWT_SECRET=your-256-bit-secret

# AI APIs
GROQ_API_KEY=your-groq-key
OPENAI_API_KEY=your-openai-key

# Search APIs
TAVILY_API_KEY=your-tavily-key
SERPAPI_KEY=your-serp-key

# Domain
DOMAIN_NAME=your-domain.com
```

### SSL Certificate Setup

**Option A: Let's Encrypt (Recommended for production)**
```bash
sudo certbot --nginx -d your-domain.com
```

**Option B: Self-signed (Development only)**
```bash
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/ssl/private.key \
  -out nginx/ssl/cert.pem
```

### Monitoring and Logging

1. **View Logs**
   ```bash
   # All services
   docker-compose -f docker-compose.prod.yml logs -f
   
   # Specific service
   docker-compose -f docker-compose.prod.yml logs -f backend
   ```

2. **Monitor Resources**
   ```bash
   # Container stats
   docker stats
   
   # System resources
   htop
   ```

### Backup and Recovery

1. **Database Backup**
   ```bash
   # Manual backup
   docker exec finance-chatbot-mysql-prod mysqldump -u root -p finance_chatbot > backup.sql
   
   # Automated backup (add to cron)
   0 2 * * * /path/to/backup-script.sh
   ```

2. **Redis Backup**
   ```bash
   docker exec finance-chatbot-redis-prod redis-cli BGSAVE
   ```

### Security Checklist

- [ ] Change all default passwords
- [ ] Use strong JWT secret (256-bit)
- [ ] Enable SSL/TLS certificates
- [ ] Configure firewall rules
- [ ] Enable container security scanning
- [ ] Set up monitoring and alerting
- [ ] Regular security updates
- [ ] Backup strategy in place

### Scaling

1. **Horizontal Scaling**
   - Use load balancer (nginx, HAProxy)
   - Multiple backend instances
   - Shared database and Redis

2. **Vertical Scaling**
   - Increase server resources
   - Optimize JVM settings
   - Database tuning

### Troubleshooting

1. **Container Issues**
   ```bash
   # Check container status
   docker-compose ps
   
   # View logs
   docker-compose logs [service]
   
   # Restart service
   docker-compose restart [service]
   ```

2. **Database Connection Issues**
   ```bash
   # Test MySQL connection
   docker exec -it finance-chatbot-mysql-prod mysql -u root -p
   
   # Check Redis
   docker exec -it finance-chatbot-redis-prod redis-cli ping
   ```

3. **SSL Issues**
   ```bash
   # Check certificate
   openssl x509 -in nginx/ssl/cert.pem -text -noout
   
   # Test SSL
   curl -k https://localhost/health
   ```

## 🎯 Performance Optimization

### JVM Tuning
```bash
JAVA_OPTS="-Xmx2048m -Xms1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Database Optimization
```sql
-- MySQL configuration optimizations
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
SET GLOBAL max_connections = 200;
```

### Nginx Optimization
```nginx
worker_processes auto;
worker_connections 1024;
keepalive_timeout 65;
client_max_body_size 50M;
```

---

## 📞 Support

For deployment support:
1. Check logs first: `docker-compose logs -f`
2. Verify environment variables in `.env.prod`
3. Ensure all required ports are open
4. Check API key validity
5. Review security group/firewall rules