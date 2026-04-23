# Especificación: Integración de IA Local (Ollama) para Insights de Hábitos

## 1. Contexto y Objetivo
El objetivo de esta feature es dotar a la aplicación de capacidades de Inteligencia Artificial para analizar los registros diarios (`DailyLog`) de los usuarios y sugerir nuevos hábitos o dar feedback personalizado. 
Para mantener los costos en cero y garantizar la privacidad de los datos, se descartan APIs externas (OpenAI, Claude). Se utilizará **Ollama** alojado en la misma infraestructura (idealmente un Free Tier de Oracle Cloud con CPU ARM y 24GB RAM) ejecutando un Small Language Model (SLM).

## 2. Decisiones de Infraestructura (Docker)
Se extenderá el `docker-compose.yml` actual para incluir un contenedor dedicado a la IA.

- **Servicio:** `ollama/ollama:latest`
- **Red:** Mismo bridge network que el backend (`smart-backend`). No se exponen puertos al exterior (solo el backend puede hablar con Ollama).
- **Volumen:** Se requiere un volumen persistente (`ollama_data:/root/.ollama`) para no descargar los modelos en cada reinicio.
- **Modelo Elegido:** `phi3` o `qwen:1.5b`. Son modelos altamente eficientes diseñados para correr en CPU con consumos menores a 2GB de RAM, ofreciendo tiempos de respuesta aceptables sin GPU.
- **Entrypoint:** Se configurará un script de inicio para que ejecute `ollama pull phi3` automáticamente si el modelo no existe en el volumen.

## 3. Implementación en el Backend (Arquitectura Hexagonal)

La integración respetará estrictamente la arquitectura limpia actual del proyecto `smart-backend`.

### 3.1. Infraestructura (Dependencias y Configuración)
- **Librería:** Se integrará **Spring AI** (`spring-ai-ollama-spring-boot-starter`).
- **Configuración (`application.yml`):**
  ```yaml
  spring:
    ai:
      ollama:
        base-url: http://ollama:11434
        chat:
          options:
            model: phi3
            temperature: 0.7
  ```

### 3.2. Capa de Aplicación (Ports)
- **Puerto de Salida (`application/port/out/AiAssistantPort.java`):**
  Interfaz que define el contrato, ej: `String generateInsightBasedOnLogs(List<DailyLog> logs);`
- **Puerto de Entrada / Caso de Uso (`application/port/in/GenerateHabitInsightsUseCase.java`):**
  Orquesta la lógica: busca los logs del usuario en la base de datos (vía `DailyLogRepositoryPort`) y se los pasa al `AiAssistantPort`.

### 3.3. Capa de Infraestructura (Adapters)
- **Adaptador de Salida (`infrastructure/adapter/OllamaAiAdapter.java`):**
  Implementa `AiAssistantPort`. Aquí se inyecta el `ChatClient` de Spring AI. Este adaptador es el único que sabe que estamos usando Ollama. Construye el "System Prompt" (ej: "Eres un coach de hábitos. Analiza estos registros...") y realiza la llamada.

## 4. Flujo de Ejecución (Secuencia)
1. El frontend solicita insights para el usuario X.
2. El `HabitInsightController` recibe la petición HTTP.
3. Llama a `GenerateHabitInsightsUseCase`.
4. El caso de uso recupera los últimos `DailyLog` del usuario.
5. El caso de uso llama a `AiAssistantPort.generateInsightBasedOnLogs()`.
6. El `OllamaAiAdapter` estructura el prompt y hace la petición REST al contenedor de Docker (`http://ollama:11434`).
7. Ollama procesa la inferencia en CPU y devuelve la respuesta.
8. El texto fluye de regreso por las capas y se devuelve al frontend como un DTO.

## 5. Riesgos y Mitigaciones
- **Tiempos de respuesta (Timeouts):** La inferencia en CPU es lenta (puede tardar entre 5 y 15 segundos). *Mitigación:* Implementar llamadas asíncronas, webhooks, o al menos aumentar los timeouts en el cliente HTTP del frontend para esta ruta específica. Mostrar un skeleton loader o animación de "La IA está pensando..." en Angular.
- **Alucinaciones del modelo:** Los SLMs pueden inventar cosas. *Mitigación:* Diseñar un System Prompt muy restrictivo (Few-shot prompting) y limitar la temperatura (`temperature: 0.3`) si se buscan respuestas más deterministas.