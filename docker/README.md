# Docker Configuration for Rihua API

This directory contains Docker configuration files for the Rihua Community Platform API.

## Directory Structure

```
docker/
├── nginx/
│   ├── nginx.conf          # Main Nginx configuration
│   └── conf.d/
│       └── rihua-api.conf  # API-specific Nginx configuration
├── postgres/
│   └── init/
│       └── 01-init.sql     # Database initialization script
├── redis/
│   └── redis.conf          # Redis configuration
└── README.md               # This file
```

## Services

### PostgreSQL Database
- **Image**: postgres:15-alpine
- **Port**: 5432
- **Database**: rihua_dev
- **User**: rihua_user
- **Password**: rihua_password

### Redis Cache
- **Image**: redis:7-alpine
- **Port**: 6379
- **Configuration**: Custom redis.conf with optimized settings

### MinIO (S3 Compatible Storage)
- **Image**: minio/minio:latest
- **Ports**: 9000 (API), 9001 (Console)
- **Credentials**: minioadmin / minioadmin123

### MailHog (Email Testing)
- **Image**: mailhog/mailhog:latest
- **Ports**: 1025 (SMTP), 8025 (Web UI)

### Nginx (Reverse Proxy)
- **Image**: nginx:alpine
- **Ports**: 80, 443
- **Features**: Rate limiting, CORS, SSL termination

## Usage

### Development Environment

```bash
# Start all services
docker-compose up -d

# Start with development overrides
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# View logs
docker-compose logs -f rihua-api

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Production Environment

```bash
# Build and start production services
docker-compose -f docker-compose.yml up -d --build

# Scale API instances
docker-compose up -d --scale rihua-api=3
```

## Environment Variables

### Required for Production

- `DATABASE_URL`: PostgreSQL connection URL
- `DATABASE_USERNAME`: Database username
- `DATABASE_PASSWORD`: Database password
- `REDIS_HOST`: Redis host
- `REDIS_PASSWORD`: Redis password
- `JWT_SECRET`: JWT signing secret
- `AWS_S3_BUCKET`: S3 bucket name
- `AWS_ACCESS_KEY`: AWS access key
- `AWS_SECRET_KEY`: AWS secret key
- `STRIPE_API_KEY`: Stripe API key
- `EXPO_ACCESS_TOKEN`: Expo push notification token

### Optional

- `CORS_ALLOWED_ORIGINS`: Comma-separated list of allowed origins
- `MAIL_HOST`: SMTP host
- `MAIL_USERNAME`: SMTP username
- `MAIL_PASSWORD`: SMTP password

## Health Checks

All services include health checks:

- **PostgreSQL**: `pg_isready`
- **Redis**: `redis-cli ping`
- **MinIO**: HTTP health endpoint
- **API**: Spring Boot Actuator health endpoint

## Monitoring

### Available Endpoints

- **API Health**: http://localhost:8080/actuator/health
- **API Metrics**: http://localhost:8080/actuator/metrics
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Database Admin**: http://localhost:8081 (Adminer)
- **Redis Admin**: http://localhost:8082 (Redis Commander)
- **Email Testing**: http://localhost:8025 (MailHog)
- **MinIO Console**: http://localhost:9001

### Logs

```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs rihua-api
docker-compose logs postgres
docker-compose logs redis

# Follow logs in real-time
docker-compose logs -f rihua-api
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 5432, 6379, 8080, etc. are not in use
2. **Memory issues**: Increase Docker memory allocation if needed
3. **Permission issues**: Ensure Docker has proper permissions

### Reset Everything

```bash
# Stop all containers and remove volumes
docker-compose down -v

# Remove all images
docker-compose down --rmi all

# Rebuild from scratch
docker-compose build --no-cache
docker-compose up -d
```

## Security Notes

- Default passwords are for development only
- Use strong passwords and secrets in production
- Enable SSL/TLS for production deployments
- Regularly update Docker images for security patches