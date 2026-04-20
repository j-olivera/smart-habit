# Spec: Docker Compose - Smart Habit

## Meta
- **Change**: infrastructure-setup
- **Component**: Docker Compose
- **Version**: 1.0
- **Status**: Draft
- **Dependencies**: INFRASTRUCTURE-db.md

---

## 1. Servicios

| Servicio | Nombre | Imagen/Build | Puerto |
|----------|--------|--------------|--------|
| PostgreSQL | smart-habit-db | postgres:16-alpine | 5432 |
| Backend | smart-habit-backend | ./smart-backend | 8080 |
| Frontend | smart-habit-frontend | ./smart-habit-frontend | 4200 |
| Ollama | smart-habit-ai | ollama/ollama:latest | 11434 |

---

## 2. Red

```yaml
networks:
  smart-network:
    driver: bridge
```

Todos los servicios dentro de `smart-network`.

---

## 3. Volúmenes

| Volumen | Servicio | Descripción |
|---------|----------|-------------|
| postgres-data | postgres | Persistencia de datos PostgreSQL |
| ollama-data | ollama | Modelos descargados |

---

## 4. Configuración de Servicios

### 4.1 PostgreSQL

```yaml
services:
  smart-habit-db:
    image: postgres:16-alpine
    container_name: smart-habit-db
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - smart-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped
```

### 4.2 Backend

```yaml
services:
  smart-habit-backend:
    build:
      context: ./smart-backend
      dockerfile: Dockerfile
    container_name: smart-habit-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://smart-habit-db:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      OLLAMA_BASE_URL: http://smart-habit-ai:11434
      OLLAMA_MODEL: ${OLLAMA_MODEL}
    ports:
      - "8080:8080"
    networks:
      - smart-network
    depends_on:
      smart-habit-db:
        condition: service_healthy
      smart-habit-ai:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped
```

**Dependencias requeridas en pom.xml**:
- `spring-boot-starter-actuator` (para health endpoint)
- `flyway-core` (para migraciones)

### 4.3 Frontend

```yaml
services:
  smart-habit-frontend:
    build:
      context: ./smart-habit-frontend
      dockerfile: Dockerfile
    container_name: smart-habit-frontend
    environment:
      NG_HOST: 0.0.0.0
      NG_PORT: 4200
      API_URL: http://localhost:8080
    ports:
      - "4200:4200"
    networks:
      - smart-network
    depends_on:
      - smart-habit-backend
    restart: unless-stopped
```

**Notas**:
- Ejecuta `ng serve` para desarrollo
- Expone puerto 4200 para acceso desde host

### 4.4 Ollama

```yaml
services:
  smart-habit-ai:
    image: ollama/ollama:latest
    container_name: smart-habit-ai
    ports:
      - "11434:11434"
    volumes:
      - ollama-data:/root/.ollama
    networks:
      - smart-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:11434/api/tags"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
    restart: unless-stopped
    deploy:
      resources:
        limits:
          memory: 4G
```

---

## 5. Variables de Entorno (.env)

```env
# Database
POSTGRES_DB=smarthabit
POSTGRES_USER=postgres
POSTGRES_PASSWORD=strong_password

# JWT
JWT_SECRET=super_secret_jwt_key_for_development

# Ollama
OLLAMA_MODEL=llama3
```

---

## 6. Healthchecks Resumidos

| Servicio | Healthcheck | Interval | Timeout | Retries | Start Period |
|----------|--------------|----------|---------|---------|--------------|
| postgres | pg_isready | 10s | 5s | 5 | - |
| backend | curl /actuator/health | 30s | 10s | 3 | 40s |
| ollama | curl /api/tags | 30s | 10s | 5 | 60s |
| frontend | - | - | - | - | - |

---

## 7. docker-compose.yml Completo

```yaml
services:
  smart-habit-db:
    image: postgres:16-alpine
    container_name: smart-habit-db
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - smart-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  smart-habit-backend:
    build:
      context: ./smart-backend
      dockerfile: Dockerfile
    container_name: smart-habit-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://smart-habit-db:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      OLLAMA_BASE_URL: http://smart-habit-ai:11434
      OLLAMA_MODEL: ${OLLAMA_MODEL}
    ports:
      - "8080:8080"
    networks:
      - smart-network
    depends_on:
      smart-habit-db:
        condition: service_healthy
      smart-habit-ai:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped

  smart-habit-frontend:
    build:
      context: ./smart-habit-frontend
      dockerfile: Dockerfile
    container_name: smart-habit-frontend
    environment:
      NG_HOST: 0.0.0.0
      NG_PORT: 4200
      API_URL: http://localhost:8080
    ports:
      - "4200:4200"
    networks:
      - smart-network
    depends_on:
      - smart-habit-backend
    restart: unless-stopped

  smart-habit-ai:
    image: ollama/ollama:latest
    container_name: smart-habit-ai
    ports:
      - "11434:11434"
    volumes:
      - ollama-data:/root/.ollama
    networks:
      - smart-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:11434/api/tags"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
    restart: unless-stopped
    deploy:
      resources:
        limits:
          memory: 4G

networks:
  smart-network:
    driver: bridge

volumes:
  postgres-data:
  ollama-data:
```

---

## 8. Dockerfiles Requeridos

### 8.1 Dockerfile (Backend)

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 8.2 Dockerfile (Frontend)

```dockerfile
FROM node:21-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:21-alpine
WORKDIR /app
COPY --from=build /app/dist/smart-habit-frontend/browser .
RUN npm install -g @angular/cli
EXPOSE 4200
CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "4200"]
```

---

## 9. Criterios de Éxito

- [ ] `docker compose up --build` levanta todos los servicios
- [ ] PostgreSQL responde a healthcheck
- [ ] Backend se conecta a PostgreSQL
- [ ] Frontend accesible en http://localhost:4200
- [ ] Ollama responde a healthcheck
- [ ] Servicios se comunican entre sí por red interna
- [ ] Volúmenes persisten datos al reiniciar