# Feature 4 - user-register-habit-daily-backend

## 2 - Description
Logica para que el usuario registre un DailyLog por dia, con habitos opcionales.

## 3 - EndPoint
- POST /api/habits/daily → registro de hábitos de un día específico
- GET  /api/habits/daily/{date} → hábitos cargados de un día
- GET  /api/habits/weekly → hábitos de la semana actual
- GET  /api/habits/summary → resumen semanal generado por IA

### Request Body
 - Para los casos COMPLETADOS
```json
    [{
      "Day": "MONDAY",
      "Training" : {
        "completed" : true,
        "hours" : 2,
        "muscularGroup" : "BACK",
        "energy" : 75
      }
    },
    { 
      "Day": "MONDAY",
      "Study": {
        "completed" : true,
        "hours" : 2,
        "theme": "Angular"
      }
    },
    {
      "Day" : "MONDAY",
      "Food" : {
        "expectation" : "GOOD",
        "observation" : {
          "boolean": true,
          "considerations": "Yes, very good"
        }
      }
    },
    {
      "Day" : "MONDAY",
      "Status/Sociality" : {
        "feeling" : "HAPPY",
        "observation" : {
          "boolean" : true,
          "dayActions" : "I meet a girl"
        },
        "socialAction" : {
          "boolean" : true,
          "whoPerson" : "with a girl of my university"
        }
      }
    },
    {
      "Day" : "MONDAY",
      "Sleepy" : {
        "time" : 6.5,
        "feeling" : "BAD",
        "nap" : {
          "boolean" : true,
          "hours" : 1
        }
      }
    }
]
```
- Casos de NO COMPLETADOS
```json
[
  {
    "Day": "MONDAY",
    "Training": {
      "completed": false,
      "reason": "I had no time today"
    }
  },
  {
    "Day": "MONDAY",
    "Study": {
      "completed": false,
      "reason": "I was too tired after work"
    }
  },
  {
    "Day": "MONDAY",
    "Food": {
      "expectation": "REGULAR",
      "observation": {
        "boolean": false
      }
    }
  },
  {
    "Day": "MONDAY",
    "Status/Sociality": {
      "feeling": "NEUTRAL",
      "observation": {
        "boolean": false
      },
      "socialAction": {
        "boolean": false
      }
    }
  },
  {
    "Day": "MONDAY",
    "Sleepy": {
      "time": 6.5,
      "feeling": "REGULAR",
      "nap": {
        "boolean": false
      }
    }
  }
]
```
### Expected Responses
- 201 Created: Return a dailyLog complete
- 400 Bad Request: Retorna datos invalidos en algun habito

## 4 - Bussines Restrictions (Backend)
- No puede haber valores nulos ni vacios en ningun atributo
- No se permiten caracteres fuera de ASCII
- Las horas en cada habito deben ser coherentes: estudio <=12 | entreno <=4 | sueño <= 12 | siesta <= 4
- El dailyLog no se considera completo si falta algun atributo en cualquiera de los habitos, retornando error 400 (Bad Request)

## 5 - Technical Guidelines
- Respeta los lineamientos del proyecto: Clean Architecture | SOLID
- No se debe exponer datos criticos del user en el endpoint
- La capa dominio|application esta excluida de dependencias y frameworks externos

## 6 - Acceptance Criteria
- Escenario 1 : Entrega Exitosa
    - Dado que el usuario cumplio con las exigencias pedidas en los use case, el sistema devuelve el estado 200 OK, armando el json con los datos correspondientes
- Escenario 2 : Falta de datos
    - Dado que el usuario no completo algun campo de cualquier habito registrable, el sistema no crea el json correspondiente y devulve un error 400 BAD REQUEST
- Escenario 3 : Caracteres invalidos
    - Dado que el usuario completo todos los campos de los atributos para los habitos registrables, pero utilizo caracteres fuera de ASCII, el sistema no genera el json correspondiente y devuelve error 400 BAD REQUEST