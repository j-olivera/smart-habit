# Feature 4 - user-register-habit-daily-backend

## 2 - Description
Lógica para que el usuario gestione sus hábitos y registre sus progresos diarios de forma atómica. 
El flujo consiste en:
1. Crear una entrada diaria (`DailyEntry`).
2. Registrar logs específicos para cada hábito asociado a ese día.

## 3 - EndPoints

### A. Gestión de Definiciones (Habit)
- `POST /api/habits` → Crear definición de hábito.
- `GET /api/habits` → Listar mis hábitos activos.
- `DELETE /api/habits/{id}` → Desactivar hábito (soft delete).

### B. Registro Diario (DailyEntry & Logs)
- `POST /api/habits/daily` → Crear entrada para un día específico.
- `GET /api/habits/daily/{date}` → Obtener el día con todos sus logs (formato agrupado).
- `POST /api/habits/daily/{entryId}/logs/exercise` → Registrar/reemplazar log de ejercicio.
- `POST /api/habits/daily/{entryId}/logs/study` → Registrar/reemplazar log de estudio.
- `POST /api/habits/daily/{entryId}/logs/sleep` → Registrar/reemplazar log de sueño.
- `POST /api/habits/daily/{entryId}/logs/nutrition` → Registrar/reemplazar log de nutrición.
- `POST /api/habits/daily/{entryId}/logs/mood` → Registrar/reemplazar log de ánimo.

---

### Request Bodies (Ejemplos clave)

#### POST /api/habits/daily
```json
{
  "date": "2026-04-21"
}
```

#### POST /api/habits/daily/{entryId}/logs/exercise
```json
{
  "habitId": 1,
  "exercised": true,
  "hours": 1.5,
  "muscularGroup": "CHEST",
  "energyLevel": 80
}
```

#### POST /api/habits/daily/{entryId}/logs/study
```json
{
  "habitId": 2,
  "studied": true,
  "hours": 2.0,
  "subject": "Java Hexagonal Architecture"
}
```

---

### ENUM Values (Coincidentes con Dominio)
- **muscularGroup**: CHEST, BACK, LEGS, ARMS, ABDOMEN, CARDIO
- **moodLevel**: SAD, DOWN, NEUTRAL, HAPPY, EUPHORIC
- **nutritionRating**: POOR, REGULAR, GOOD, EXCELLENT
- **sleepQuality**: BAD, REGULAR, GOOD, EXCELLENT
- **habitType**: STUDY, EXERCISE, NUTRITION, MOOD, SLEEP

---

### Response Format (Agrupado para GET /api/habits/daily/{date})
```json
{
  "success": true,
  "message": "Daily entry retrieved successfully",
  "data": {
    "id": 10,
    "date": "2026-04-21",
    "logs": {
      "exercise": { "id": 1, "hours": 1.5, ... },
      "study": null,
      "sleep": { "id": 5, "hours": 7.0, ... }
    }
  }
}
```

## 4 - Business Restrictions (Backend)
- **Seguridad:** El `userId` SIEMPRE se obtiene del contexto de seguridad (JWT). No se acepta en el body.
- **Unicidad:** Solo un `DailyEntry` por usuario por fecha.
- **Temporalidad:** Solo se pueden crear/modificar entradas de los últimos 7 días (ajustable).
- **Integridad:** Un log solo puede registrarse si el `habitId` pertenece al usuario y su `type` coincide con el endpoint.
- **Límites:** 
    - Horas de estudio/sueño: 0.1 a 12.
    - Horas de ejercicio/siesta: 0.1 a 4.
    - Nivel de energía: 1 a 100.

## 5 - Technical Guidelines
- **Arquitectura:** Hexagonal / Clean. DTOs en `application.dto`.
- **Persistencia:** Los logs se guardan en sus tablas específicas referenciando al `DailyEntry` y al `Habit`.
- **Mapeo:** Usar mappers en la capa de aplicación para convertir de Entidad a DTO de respuesta.

## 6 - Use Cases
*(Mantenemos los definidos anteriormente, ya que coinciden con este enfoque atómico)*
- **UC-01:** Crear definición de hábito
- **UC-02:** Listar habitos activos del user
- **UC-03:** Desactivar habito
- **UC-04:** Crear entrada base (DailyEntry).
- **UC-05:** Registrar un log específico (Valida pertenencia y tipo).
- **UC-06:** Recuperar la "foto" completa del día.
- **UC-07:** Obtener entradas de la semana (insumo para feature #5)