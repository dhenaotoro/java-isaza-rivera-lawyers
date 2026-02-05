<!-- Configuración de Podman Compose - API Isaza Rivera Abogados -->
# Configuración de Podman Compose - API Isaza Rivera Abogados

## Inicio Rápido

### Requisitos Previos
- Podman instalado en tu sistema
- Plugin de Podman Compose instalado

### Construir e Iniciar Servicios

```bash
# Inicia los contenedores
podman-compose -f podman-compose.yml up -d

# Ver registros
podman-compose -f podman-compose.yml logs -f

# Detener los contenedores
podman-compose -f podman-compose.yml down

# Detener y eliminar volúmenes (limpiar)
podman-compose -f podman-compose.yml down -v
```

## Servicios

### Base de Datos MySQL
- **Nombre del Contenedor**: isaza-mysql
- **Puerto**: 3306 (localhost:3306)
- **Base de Datos**: legaldb
- **Usuario**: legal_user
- **Contraseña**: legal_password
- **Contraseña de Root**: root_password

### API Java
- **Nombre del Contenedor**: isaza-api
- **Puerto**: 8081 (http://localhost:8081)
- **Perfil de Spring**: docker

## Acceso a los Servicios

### API
```
http://localhost:8081
http://localhost:8081/h2 (Consola H2 - si está habilitada)
```

### Base de Datos MySQL
```bash
# Conectarse a MySQL desde tu máquina
mysql -h localhost -u legal_user -p legaldb
# Contraseña: legal_password

# O conectarse desde dentro del contenedor
podman-compose exec mysql mysql -u legal_user -p legaldb
```

## Configuración

La aplicación utiliza variables de entorno definidas en `podman-compose.yml`:
- `SPRING_DATASOURCE_URL`: Cadena de conexión a MySQL
- `SPRING_DATASOURCE_USERNAME`: Usuario de la base de datos
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de la base de datos
- `SPRING_PROFILES_ACTIVE`: Perfil activo de Spring (docker)

Archivo de configuración: `src/app/main/resources/application-docker.yml`

## Red

Ambos servicios se comunican a través de la red puente `legal-network`.

## Persistencia de Datos

Los datos de MySQL se almacenan en el volumen nombrado `mysql_data`, persistiendo entre reinicios de contenedores.

## Verificaciones de Salud

El servicio MySQL incluye verificaciones de salud que confirman la conectividad antes de iniciar la API.

## Solución de Problemas

### Verificar estado del servicio
```bash
podman-compose ps
```

### Ver registros del servicio
```bash
podman-compose logs mysql
podman-compose logs api
```

### Reconstruir la imagen
```bash
podman-compose build --no-cache
```

### Limpiar completamente
```bash
podman-compose down -v
podman image rm isaza-api mysql:8.0
```