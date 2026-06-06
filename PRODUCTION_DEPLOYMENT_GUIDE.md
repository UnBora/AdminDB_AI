# Admin Dashboard Platform - Production Deployment Guide

## Overview
This guide provides comprehensive instructions for deploying the Admin Dashboard Platform to production environments.

## Pre-Deployment Checklist

### 1. Environment Requirements
- **Java**: OpenJDK 21 or later
- **PostgreSQL**: 14 or later
- **Maven**: 3.8 or later
- **Memory**: Minimum 2GB RAM, Recommended 4GB+
- **Disk Space**: Minimum 5GB for application and logs

### 2. Security Configuration

#### Environment Variables (Set before deployment)
```bash
# Database Configuration
export DB_HOST=your-postgres-host
export DB_PORT=5432
export DB_NAME=admin_dashboard_prod
export DB_USER=admin_user
export DB_PASSWORD=secure_password_here

# Application Configuration  
export APP_URL=https://admin.yourdomain.com
export APP_SECRET_KEY=$(openssl rand -base64 32)
export JAVA_OPTS="-Xmx4g -Xms2g"

# Security
export CSRF_HEADER_NAME=X-CSRF-TOKEN
export CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Email/SMTP
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=app_specific_password
export SMTP_FROM=noreply@yourdomain.com
```

### 3. Database Preparation

#### Create PostgreSQL Database and User
```sql
-- Create database
CREATE DATABASE admin_dashboard_prod
    WITH OWNER postgres
    ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';

-- Create user with secure password
CREATE USER admin_user WITH ENCRYPTED PASSWORD 'secure_password_here';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE admin_dashboard_prod TO admin_user;

-- Connect to the database and grant schema privileges
\c admin_dashboard_prod postgres
GRANT ALL PRIVILEGES ON SCHEMA public TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO admin_user;
```

#### Enable PostgreSQL Extensions (if needed)
```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
```

### 4. Application Configuration

#### Create `application-prod.yml`
```yaml
spring:
  application:
    name: Admin Dashboard Platform
  
  # Database Configuration
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  # JPA/Hibernate
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: none  # CRITICAL: Never use 'update' in production
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20
          fetch_size: 50
        order_inserts: true
        order_updates: true
  
  # Liquibase Migration
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
    contexts: prod
    labels: prod
    drop-first: false
  
  # Thymeleaf
  thymeleaf:
    mode: HTML
    encoding: UTF-8
    cache: true
    check-template: true
    prefix: classpath:/templates/
    suffix: .html
  
  # Security
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://${APP_URL}
  
  # Mail Configuration
  mail:
    host: ${SMTP_HOST}
    port: ${SMTP_PORT}
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000

# Server Configuration
server:
  port: 8443
  servlet:
    context-path: /
    session:
      timeout: 30m
      cookie:
        secure: true
        http-only: true
        same-site: strict
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
  tomcat:
    threads:
      max: 200
      min-spare: 20
    connection-timeout: 20000
    max-connections: 10000
    accesslog:
      enabled: true
      directory: /var/log/tomcat
      pattern: "%h %l %u %t \"%r\" %s %b %D"

# Logging Configuration
logging:
  level:
    root: WARN
    com.platform: INFO
    org.springframework: WARN
    org.springframework.security: DEBUG
    org.hibernate: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/admin-dashboard/application.log
    max-size: 100MB
    max-history: 30
    total-size-cap: 5GB

# Application Custom Properties
app:
  name: Admin Dashboard Platform
  version: 1.0.0
  url: ${APP_URL}
  upload:
    dir: /var/data/admin-dashboard/uploads
    max-size: 52428800  # 50MB in bytes
  jwt:
    secret: ${APP_SECRET_KEY}
    expiration: 86400000  # 24 hours in milliseconds
    refresh-expiration: 604800000  # 7 days
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS}
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
    max-age: 3600
```

### 5. SSL/TLS Certificate Setup

#### Generate Self-Signed Certificate (for testing only)
```bash
keytool -genkeypair \
  -alias admin-dashboard \
  -keyalg RSA \
  -keysize 2048 \
  -keystore keystore.p12 \
  -keypass changeit \
  -storepass changeit \
  -storetype PKCS12 \
  -dname "CN=admin.yourdomain.com, OU=IT, O=Company, L=City, ST=State, C=Country" \
  -validity 365
```

#### Using Let's Encrypt (Production Recommended)
```bash
# Install Certbot
sudo apt-get install certbot python3-certbot-nginx

# Generate certificate
sudo certbot certonly --standalone -d admin.yourdomain.com

# Convert to PKCS12
openssl pkcs12 -export \
  -in /etc/letsencrypt/live/admin.yourdomain.com/fullchain.pem \
  -inkey /etc/letsencrypt/live/admin.yourdomain.com/privkey.pem \
  -out keystore.p12 \
  -name admin-dashboard
```

### 6. File System Setup

#### Create Required Directories
```bash
# Application directories
mkdir -p /var/data/admin-dashboard/uploads
mkdir -p /var/log/admin-dashboard
mkdir -p /var/log/tomcat

# Set permissions
chmod 755 /var/data/admin-dashboard
chmod 755 /var/log/admin-dashboard
chown tomcat:tomcat /var/data/admin-dashboard
chown tomcat:tomcat /var/log/admin-dashboard
```

## Build & Deployment

### 1. Build Application
```bash
# Clean build
mvn clean package -P prod

# Output: target/admin-dashboard-platform-1.0.0.jar
```

### 2. Systemd Service Setup

#### Create Service File: `/etc/systemd/system/admin-dashboard.service`
```ini
[Unit]
Description=Admin Dashboard Platform
After=network.target postgresql.service

[Service]
Type=simple
User=tomcat
Group=tomcat

# Environment variables
EnvironmentFile=/etc/admin-dashboard/dashboard.env

# Application startup
ExecStart=/usr/bin/java \
  -Xmx4g -Xms2g \
  -Dspring.profiles.active=prod \
  -jar /opt/admin-dashboard/admin-dashboard-platform-1.0.0.jar

# Restart policy
Restart=on-failure
RestartSec=10

# Timeouts
StartLimitInterval=300
StartLimitBurst=5

# Logging
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

#### Environment File: `/etc/admin-dashboard/dashboard.env`
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=admin_dashboard_prod
DB_USER=admin_user
DB_PASSWORD=secure_password
APP_URL=https://admin.yourdomain.com
JAVA_OPTS="-Xmx4g -Xms2g"
```

### 3. Enable and Start Service
```bash
# Enable service to start on boot
sudo systemctl enable admin-dashboard

# Start service
sudo systemctl start admin-dashboard

# Check status
sudo systemctl status admin-dashboard

# View logs
sudo journalctl -u admin-dashboard -f
```

### 4. Nginx Reverse Proxy Setup

#### Nginx Configuration: `/etc/nginx/sites-available/admin-dashboard.conf`
```nginx
server {
    listen 80;
    server_name admin.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name admin.yourdomain.com;

    # SSL Certificates
    ssl_certificate /etc/letsencrypt/live/admin.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/admin.yourdomain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Permissions-Policy "geolocation=(), microphone=(), camera=()" always;

    # Logging
    access_log /var/log/nginx/admin-dashboard-access.log combined;
    error_log /var/log/nginx/admin-dashboard-error.log warn;

    # File upload limits
    client_max_body_size 52M;

    # Proxy settings
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Buffering
        proxy_buffering off;
    }

    # Static content caching
    location ~* \.(css|js|jpg|jpeg|png|gif|ico|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://localhost:8080;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

#### Enable Nginx Site
```bash
sudo ln -s /etc/nginx/sites-available/admin-dashboard.conf /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## Health Checks & Monitoring

### 1. Application Health Endpoint
```bash
# Health check
curl https://admin.yourdomain.com/actuator/health

# Response:
# {"status":"UP"}
```

### 2. Metrics Collection
```bash
# Prometheus metrics
curl https://admin.yourdomain.com/actuator/prometheus
```

### 3. Database Connection Monitoring
```sql
-- Check active connections
SELECT datname, usename, application_name, state, query_start
FROM pg_stat_activity
WHERE datname = 'admin_dashboard_prod';

-- Check slow queries
SELECT query, calls, total_time, mean_time
FROM pg_stat_statements
WHERE mean_time > 1000
ORDER BY mean_time DESC
LIMIT 10;
```

## Backup & Recovery

### 1. Database Backup
```bash
# Full backup
pg_dump -U admin_user -h localhost admin_dashboard_prod | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz

# Schedule daily backup with cron
0 2 * * * /usr/local/bin/backup-database.sh
```

#### Backup Script: `/usr/local/bin/backup-database.sh`
```bash
#!/bin/bash
BACKUP_DIR="/var/backups/admin-dashboard"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/backup_$DATE.sql.gz"

mkdir -p $BACKUP_DIR
pg_dump -U admin_user -h localhost admin_dashboard_prod | gzip > $BACKUP_FILE

# Keep only last 30 days of backups
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +30 -delete

# Log
echo "$(date): Backup completed: $BACKUP_FILE" >> $BACKUP_DIR/backup.log
```

### 2. File Upload Backup
```bash
# Backup uploads directory
rsync -avz /var/data/admin-dashboard/uploads/ /mnt/backup/uploads_$(date +%Y%m%d)/
```

### 3. Disaster Recovery
```bash
# Restore database
zcat backup_20240115_020000.sql.gz | psql -U admin_user -h localhost admin_dashboard_prod

# Restore uploads
rsync -avz /mnt/backup/uploads_20240115/ /var/data/admin-dashboard/uploads/
```

## Performance Tuning

### 1. PostgreSQL Optimization
```sql
-- Analyze tables for query planning
ANALYZE;

-- Check index effectiveness
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;

-- Monitor table sizes
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### 2. Java Heap Configuration
```bash
# In /etc/admin-dashboard/dashboard.env
# For 8GB server
JAVA_OPTS="-Xmx6g -Xms4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+ParallelRefProcEnabled"

# For 4GB server
JAVA_OPTS="-Xmx3g -Xms2g -XX:+UseG1GC"
```

### 3. Connection Pool Tuning
```yaml
# In application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Troubleshooting

### Common Issues

#### 1. Connection Timeout
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Check application logs
sudo journalctl -u admin-dashboard -n 50

# Verify database credentials
psql -U admin_user -h localhost -d admin_dashboard_prod -c "SELECT 1;"
```

#### 2. Out of Memory
```bash
# Check current Java process memory
ps aux | grep java

# Increase heap size in dashboard.env
JAVA_OPTS="-Xmx8g -Xms4g"

# Restart application
sudo systemctl restart admin-dashboard
```

#### 3. Slow Queries
```sql
-- Find slow running queries
SELECT query, mean_time, max_time, calls
FROM pg_stat_statements
WHERE mean_time > 1000
ORDER BY mean_time DESC;

-- Create missing indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
```

## Security Hardening

### 1. Firewall Configuration
```bash
# UFW
sudo ufw allow 22/tcp
sudo ufw allow 443/tcp
sudo ufw allow 80/tcp
sudo ufw enable

# iptables
iptables -A INPUT -p tcp --dport 443 -j ACCEPT
iptables -A INPUT -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -p tcp --dport 22 -j ACCEPT
iptables -P INPUT DROP
```

### 2. Fail2Ban Setup
```bash
# Install fail2ban
sudo apt-get install fail2ban

# Create jail configuration
sudo cp /etc/fail2ban/jail.conf /etc/fail2ban/jail.local

# Enable and start
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

### 3. Regular Security Updates
```bash
# Enable automatic security updates
sudo apt-get install unattended-upgrades
sudo systemctl enable unattended-upgrades
```

## Support & Maintenance

### Regular Maintenance Tasks

- **Daily**: Monitor application logs, check health endpoints
- **Weekly**: Analyze slow queries, review disk space usage
- **Monthly**: Database maintenance (VACUUM, ANALYZE), security updates
- **Quarterly**: Performance tuning review, capacity planning
- **Annually**: Full security audit, disaster recovery testing

### Documentation & Contact

For support, contact: support@yourdomain.com
Documentation: https://docs.yourdomain.com
Issue Tracker: https://github.com/your-org/admin-dashboard/issues
