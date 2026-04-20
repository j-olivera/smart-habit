# Smart Frontend Dockerfile
# Build stage
FROM node:21-alpine AS build

WORKDIR /app

COPY package.json package-lock.json* ./

RUN npm ci --legacy-peer-deps || npm install --legacy-peer-deps

COPY . .

RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=build /app/dist/smart-habit-frontend/browser /usr/share/nginx/html

# Copy custom nginx config for Angular routing
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/http.d/default.conf

EXPOSE 4200

CMD ["nginx", "-g", "daemon off;"]