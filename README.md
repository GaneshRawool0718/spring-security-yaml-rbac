# Okta RBAC POC - Docker Setup

This POC demonstrates YAML-based Role-Based Access Control (RBAC) using Spring Security and Okta authentication.

## Architecture

- **okta-rbac-api**: Spring Boot REST API with JWT authentication and YAML-driven permissions
- **okta-rbac-spa**: React SPA frontend with Okta login

## Prerequisites

- Docker & Docker Compose
- Okta Developer Account
- Your Okta credentials (Issuer URL, Client ID, API Token)

## Quick Start

### 1. Configure Environment Variables

Copy the example environment file and fill in your Okta credentials:

```bash
cp .env.example .env
```

Edit `.env` and add your Okta configuration:

```env
ISSUER_URL=https://your-domain.okta.com/oauth2/default
CLIENT_ID=your-client-id-here
OKTA_API_TOKEN=your-api-token-here
```

### 2. Build and Run

```bash
# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up -d --build
```

### 3. Access the Application

- **Frontend (SPA)**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Health Check**: http://localhost:8080/actuator/health

### 4. Test Authorization

Login with different Okta users:

| User Role | Can Access `/api/user` | Can Access `/api/admin` |
|-----------|------------------------|-------------------------|
| `ROLE_USER` | ✅ Yes | ❌ No (403) |
| `ROLE_ADMIN` | ✅ Yes | ✅ Yes |

## Docker Commands

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clears logs)
docker-compose down -v

# View logs
docker-compose logs -f

# View API logs only
docker-compose logs -f api

# View SPA logs only
docker-compose logs -f spa

# Rebuild specific service
docker-compose up -d --build api
```

## View Authorization Logs

Authorization logs are stored in a Docker volume. To view them:

```bash
# Access the API container
docker exec -it okta-rbac-api sh

# View authorization logs
cat /app/logs/authorization.log

# Tail logs in real-time
tail -f /app/logs/authorization.log
```

## Architecture Details

### Permission Configuration (`permissions.yaml`)

```yaml
security:
  permissions:
    roles:
      ROLE_ADMIN:
        - API_ADMIN_READ
        - API_USER_READ
        - API_USER_WRITE
        - API_APP_USER_READ
        - API_APP_USER_WRITE
      ROLE_USER:
        - API_USER_READ
        - API_USER_WRITE
        - API_APP_USER_READ
        - API_APP_USER_WRITE
```

### How It Works

1. User logs in via Okta → receives JWT with `groups` claim
2. `SecurityConfig` expands roles to permissions from `permissions.yaml`
3. Controllers use `@PreAuthorize` to check authorities
4. Authorization decisions logged to `logs/authorization.log`

## Troubleshooting

### API fails to start

Check environment variables are set correctly:
```bash
docker-compose config
```

### Cannot connect to API from SPA

Ensure both services are on the same network:
```bash
docker network ls
docker network inspect okta-rbac-poc_okta-network
```

### Build fails

Clear Docker cache and rebuild:
```bash
docker-compose down
docker system prune -a
docker-compose up --build
```

## Development Mode (Without Docker)

### API
```bash
cd okta-rbac-api
mvn spring-boot:run
```

### SPA
```bash
cd okta-rbac-spa
npm install
npm run dev
```

## Project Structure

```
okta-rbac-poc/
├── okta-rbac-api/          # Spring Boot API
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── okta-rbac-spa/          # React SPA
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml      # Docker orchestration
├── .env.example           # Environment template
└── README.md
```

## Security Features

✅ JWT-based authentication with Okta  
✅ YAML-driven role-to-permission mapping  
✅ Native Spring Security `@PreAuthorize` annotations  
✅ Comprehensive authorization logging  
✅ 401/403 error handling with detailed logs  
✅ CORS configuration for SPA-API communication

## License

MIT