# Big refactor

## Que paso? 

#### El problema principal
Logica erronea, el primer planteamiento de la app, es que el usuario tenga que completar el registro de 5 habitos 
estaticos proporcionados por la web, los cuales son completados diariamente para que al final de la semana se 
proporcione un registro. Pero al principio hubo una confusión de relaciones en la base de datos, los habitos "estaticos"
no estaban relacionados al registro ni al usuario, entonces como podemos ver la cantidad de veces que un usuario registro el habito de ejercicio? como podemos ver que observación 
hizo el usuario en el habito Social el dia tal? No podíamos sin hacer unas querys complejas y molestas. 

#### Segundo problema
Para intentar resolver el problema principal, se hizo una normalización en la base de datos y el codigo en general,
en si la normalización estuvo bien, nos permitio relacionar el habito directamente con el habito, mejorando las querys,
el problema estuvo en la logica de implementación, haciendo que los habitos estaticos se relacionen a la clase Habit, 
pero la clase Habit se hizo de forma que genere los propios habitos, habitos estaticos relacionados a una clase que genera habitos?
y por supuesto el problema venía desde como se relacionaba esto desde Controller o el front. Sin mencionar que teníamos 
casos de uso sin ejecución, ya que se realizo casos de uso para los logs diarios, dejando la clase Habits y sus funciones
completamente inactiva y causando problemas en el codigo. La tabla habit_study apuntaba a habit, que dentro decía study,
totalmente redundande

## Que se hizo
Mas alla de un refactor que me consumio todos los tokens, lo principal fue el desacoplamiento de los habitos estaticos, 
es decir los "logs", de la clase Habit, tanto en codigo como en base de datos, un log de tal tipo ya no se relaciona con 
un habito del mismo tipo. Ahora los habitos de tipo fijo estan relacionados al daily entry, el cual esta relacionado al
usuario, permitiendo querys rapidas y precisas.
Pero que paso con todo el codigo de Habit y sus funciones (que se plantearon de mala manera), en lugar de borrar todo, 
aproveche y agrege una nueva funcion a la app. El usuario puede agregar un nuevo habito, el cual se maneja de tipo 
PERSONAL, el cual persiste en los siguientes daily_entrys, dicho de mejor forma por la IA:

- La Definición (Tabla habits)
Cuando el usuario entra y dice: "Quiero empezar el hábito de Meditar 10 minutos", eso pasa por el RegisterHabitUseCase.
Eso crea un único registro en la tabla habits.
Ese registro queda guardado de forma permanente (hasta que el usuario decida desactivarlo).
Es una "plantilla" que dice: "Este usuario tiene la meta de Meditar". No importa si es lunes, martes o el año que viene; ese hábito existe en su perfil.
- La Ejecución Diaria (Tabla habit_personal - El PersonalLog)
Acá es donde entra el día específico (DailyEntry).
Hoy es martes. El frontend le pregunta al backend: "Che, ¿qué hábitos personales tiene activos este usuario?". El backend le devuelve: "Tiene 'Meditar'" (leyendo de la tabla habits).
El usuario en la UI marca "¡Listo! Medité hoy".
Eso viaja al backend y se guarda en el PersonalLog. Ese log SI está atado a un solo día (el martes) y a la plantilla ("Meditar").
Mañana miércoles, el sistema le va a volver a mostrar "Meditar" (porque la plantilla sigue viva), pero el log del miércoles va a arrancar en blanco, esperando que el usuario vuelva a marcar que lo cumplió.

**De forma que la relacion del sistema quedo asi:**

    Eje central
    users
    └── Tiene muchos daily_entries (Un registro por cada día que entra).
    └── Tiene muchos habits (Sus plantillas personales, las que inventa él).
    
    Los Logs Estáticos 
    Apuntan directo y sin escalas al día correspondiente. Cero redundancia.
    habit_study ───── apunta a ─────> daily_entries
    habit_exercise ── apunta a ─────> daily_entries
    habit_sleep ───── apunta a ─────> daily_entries
    habit_mood ────── apunta a ─────> daily_entries
    habit_nutrition ─ apunta a ─────> daily_entries

    Log Personal
    habit_personal (El log diario de lo que inventó el usuario)
    ├── apunta a ─────> daily_entries (Para saber CUÁNDO lo hizo).
    └── apunta a ─────> habits (Para saber QUÉ hábito personalizado hizo).

**Como serían las querys?**
- Como veo cuantas horas estudio un usuario desde tal fecha hasta tal otra
  - daily_entries actua como puente y luego filtramos por los parametros correspondientes

```mermaid
  SELECT SUM(s.hours) AS total_study_hours
  FROM habit_study s
  JOIN daily_entries d ON s.entry_id = d.id
  WHERE d.user_id = 123                -- El ID del usuario
  AND d.date >= '2026-04-01'         -- Fecha de inicio
  AND d.date <= '2026-04-30';        -- Fecha de fin
```

- Como veo las horas de un Personal Habit? 
```mermaid
    SELECT SUM(p.hours) AS total_guitar_hours
    FROM habit_personal p
    JOIN daily_entries d ON p.entry_id = d.id
    WHERE d.user_id = 123
      AND p.habit_id = 45                -- Acá filtramos LA PLANTILLA específica
      AND d.date >= '2026-04-01'
      AND d.date <= '2026-04-30';
```

## Que saco de esto
- A armar bien la arquitectura de mi codigo antes de codear, no basta con un README.md con la idea y un poco de logica
- Si yo no se lo que estoy armando, la IA menos xd
