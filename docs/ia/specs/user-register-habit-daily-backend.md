# Feature 4 - user-register-habit-daily-backend

## 2 - Description
Logica para que el usuario registre un DailyLog por dia, con habitos opcionales.

## 3 - EndPoint
- POST /api/habits/daily → registro de hábitos de un día específico
- GET  /api/habits/daily/{date} → hábitos cargados de un día
- GET  /api/habits/weekly → hábitos de la semana actual
- GET  /api/habits/summary → resumen semanal (IA pendiente de especificar)

### Request Body
 - Para casos COMPLETADOS
```json
{
  "day": "MONDAY",
  "training": {
    "completed": true,
    "hours": 2,
    "muscularGroup": "BACK",
    "energy": 75
  },
  "study": {
    "completed": true,
    "hours": 2,
    "theme": "Angular"
  },
  "food": {
    "expectation": "GOOD",
    "observation": {
      "completed": true,
      "considerations": "Yes, very good"
    }
  },
  "mood": {
    "feeling": "HAPPY",
    "observation": {
      "completed": true,
      "dayActions": "I meet a girl"
    },
    "socialAction": {
      "completed": true,
      "whoPerson": "with a girl of my university"
    }
  },
  "sleep": {
    "hours": 6.5,
    "feeling": "BAD",
    "nap": {
      "completed": true,
      "hours": 1
    }
  }
}
```
 - Casos de NO COMPLETADOS
```json
{
  "day": "MONDAY",
  "training": {
    "completed": false,
    "reason": "I had no time today"
  },
  "study": {
    "completed": false,
    "reason": "I was too tired after work"
  },
  "food": {
    "expectation": "REGULAR",
    "observation": {
      "completed": false
    }
  },
  "mood": {
    "feeling": "NEUTRAL",
    "observation": {
      "completed": false
    },
    "socialAction": {
      "completed": false
    }
  },
  "sleep": {
    "hours": 6.5,
    "feeling": "REGULAR",
    "nap": {
      "completed": false
    }
  }
}
```

### ENUM Values (mayúsculas)
- **day**: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
- **muscularGroup**: CHEST, BACK, LEGS, ARMS, ABDOMEN, CARDIO
- **energy**: 1-100 (int)
- **expectation**: POOR, REGULAR, GOOD, EXCELLENT
- **feeling**:
  - sleep: BAD, REGULAR, GOOD, EXCELLENT
  - mood: SAD, DOWN, NEUTRAL, HAPPY, EUPHORIC
- **completed**: true/false (boolean)

### Response Format
```json
{
  "success": true,
  "message": "Daily log registered successfully",
  "data": {
    "id": "uuid",
    "userId": "uuid",
    "day": "MONDAY",
    "date": "2026-04-21",
    "training": {...},
    "study": {...},
    "food": {...},
    "mood": {...},
    "sleep": {...},
    "createdAt": "timestamp"
  }
}
```

### Expected Status Codes
- 200 OK: Daily log registered/retrieved successfully
- 400 Bad Request: Datos inválidos en algún hábito
- 401 Unauthorized: JWT no válido o expirado

## 4 - Business Restrictions (Backend)
- No puede haber valores nulos ni vacíos en campos requeridos
- No se permiten caracteres fuera de ASCII
- Límites de horas: study <= 12 | training <= 4 | sleep <= 12 | nap <= 4
- El diariaLog no se considera completo si falta algún atributo requerido en cualquiera de los hábitos, retornando error 400

## 5 - Technical Guidelines
- Respeta los lineamientos del proyecto: Clean Architecture | SOLID
- No se debe exponer datos críticos del user en el endpoint
- La capa dominio|application está excluida de dependencias y frameworks externos
- Todos los endpoints requieren autenticación JWT
- Formato de respuesta: ApiResponse<T> según conventions.md

## 6 - Acceptance Criteria
- Escenario 1: Entrega Exitosa
  - Dado que el usuario completó todos los campos requeridos, el sistema devuelve estado 200 OK con el dailyLog registrado
- Escenario 2: Falta de datos
  - Dado que el usuario no completó algún campo requerido, el sistema devuelve error 400 BAD REQUEST
- Escenario 3: Caracteres inválidos
  - Dado que el usuario usó caracteres fuera de ASCII, el sistema devuelve error 400 BAD REQUEST