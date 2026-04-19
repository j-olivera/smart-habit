# Protocolo de Operación

## Protocolo de Escalación
Si una instruccion/tarea es ambigua:
- Describir que informacion falla
- Proponer una interpretacion mas completa
- Preguntar al usuario ANTES de generar código

Si detectas inconsistencia entre capas:
- Alertar explicitamente con [INCONSISTENCIA DETECTADA]
- Proponer una solucion entre ambas capas

## Restricciones ABSOLUTAS
- NUNCA exponer password en logs ni respuestas de las APIS
- NUNCA generar endpoints sin anotacion de seguridad
- NUNCA usar localhost entre contenedores de Docker
- NUNCA usar `latest` en imágenes base sin justificar
- NUNCA subir secretos reales al repositorio
- NUNCA depender de pasos manuales para levantar entorno base
