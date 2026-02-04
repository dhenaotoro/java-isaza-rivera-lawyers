# Podman Compose Setup - Isaza Rivera Lawyers API

## Quick Start

### Prerequisites
- Podman installed on your system
- Podman Compose plugin installed

### Build and Start Services

```bash
# Start the containers
podman-compose -f podman-compose.yml up -d

# View logs
podman-compose -f podman-compose.yml logs -f

# Stop the containers
podman-compose -f podman-compose.yml down

# Stop and remove volumes (cleanup)
podman-compose -f podman-compose.yml down -v
```

## Services

### MySQL Database
- **Container Name**: isaza-mysql
- **Port**: 3306 (localhost:3306)
- **Database**: legaldb
- **Username**: legal_user
- **Password**: legal_password
- **Root Password**: root_password

### Java API
- **Container Name**: isaza-api
- **Port**: 8081 (http://localhost:8081)
- **Spring Profile**: docker

## Accessing Services

### API
```
http://localhost:8081
http://localhost:8081/h2 (H2 Console - if enabled)
```

### MySQL Database
```bash
# Connect to MySQL from host
mysql -h localhost -u legal_user -p legaldb
# Password: legal_password

# Or connect from within the container
podman-compose exec mysql mysql -u legal_user -p legaldb
```

## Configuration

The application uses environment variables defined in `podman-compose.yml`:
- `SPRING_DATASOURCE_URL`: MySQL connection string
- `SPRING_DATASOURCE_USERNAME`: Database user
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (docker)

Configuration file: `src/app/main/resources/application-docker.yml`

## Network

Both services communicate through the `legal-network` bridge network.

## Data Persistence

MySQL data is stored in the `mysql_data` named volume, persisting between container restarts.

## Health Checks

The MySQL service includes health checks that verify connectivity before the API starts.

## Troubleshooting

### Check service status
```bash
podman-compose ps
```

### View service logs
```bash
podman-compose logs mysql
podman-compose logs api
```

### Rebuild the image
```bash
podman-compose build --no-cache
```

### Clean up completely
```bash
podman-compose down -v
podman image rm isaza-api mysql:8.0
```
