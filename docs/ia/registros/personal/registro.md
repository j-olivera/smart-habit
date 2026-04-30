### Cambios

- Se hicieron cambios en la clase PromptBuilder para que pueda manejar el tipo de hábito "personal" y mostrar un mensaje diferente en el reporte semanal
- Se corrigio el problema de que el prompt no se formateara correctamente
- Se corrigio el problema de que el reporte semanal no se generara correctamente
- Ahora los logs personales son manejados por el nombre del hábito en el prompt

### UX/UI

- Se hicieron los cambios correspondientes para que los atributos coincidan con el backend
- Ahora los opcionables son manejables

### AuthGuard

- Se implementó un AuthGuard para proteger las rutas de la API que requieren autenticación
