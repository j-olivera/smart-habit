package com.smart.smart_backend.application.port.in.registers;

import com.smart.smart_backend.application.dto.habit.DailyEntryResponseDto;

import java.time.LocalDate;

public interface CreateDailyEntry {
    DailyEntryResponseDto execute(Long userId, LocalDate date);
}
/*
1. Seguridad (Evitás IDOR)
  Si ponés el ID en la URL tipo GET /api/users/123/habits, un usuario malintencionado
  podría cambiar el 123 por 124 y ver los hábitos de otra persona. Eso se llama IDOR
  (Insecure Direct Object Reference).
  Al sacarlo del JWT, el usuario solo puede ver lo que le pertenece según su token.
  El token es la "verdad" absoluta de quién es el que llama.

  2. Contratos Limpios
  Tu Use Case (en la capa de aplicación) necesita saber sobre qué usuario operar. Por
  eso, su métod SÍ debe recibir el userId.

  El flujo queda así:
   1. Frontend: Llama a POST /api/habits (sin ID en la URL, pero envía el Token).
   2. Controller: Ataja el pedido, le pide a Spring Security el ID del usuario
      autenticado.
   3. Use Case: Recibe el userId que le pasó el Controller y hace su magia.
 */