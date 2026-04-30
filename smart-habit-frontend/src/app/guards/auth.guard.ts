import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

/**
 * AUTH GUARD (Funcional)
 * -----------------------
 * Este Guardia se encarga de proteger las rutas privadas. 
 * En Angular moderno, los Guards son funciones, no clases.
 * 
 * ¿Cómo funciona?
 * 1. Intercepta el intento de navegación.
 * 2. Inyecta los servicios necesarios mediante 'inject()'.
 * 3. Evalúa una condición (en este caso, si hay un usuario logueado).
 * 4. Retorna true (pasa) o false/UrlTree (bloquea y redirecciona).
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // NOTA: Como usas HttpOnly Cookies, el front no puede "ver" el token.
  // Usamos el estado reactivo del 'currentUser' o un flag en localStorage
  // como indicador de que hay una sesión activa.
  const isLogged = !!authService.currentUser() || localStorage.getItem('isLoggedIn') === 'true';

  if (isLogged) {
    // Si está logueado, le permitimos el paso a la ruta solicitada.
    return true;
  }

  // Si no está logueado, lo mandamos al login.
  // 'parseUrl' crea un UrlTree que Angular usa para redireccionar inmediatamente.
  console.warn('--- AUTH GUARD: Acceso denegado. Redirigiendo al Login... ---');
  return router.parseUrl('/login');
};


