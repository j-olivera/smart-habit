import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth/auth.service';
import { BehaviorSubject, Observable, catchError, filter, switchMap, take, throwError } from 'rxjs';

// Variable de estado fuera de la función para mantener el estado entre peticiones
let isRefreshing = false;
let refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

/**
 * AUTH INTERCEPTOR (Funcional)
 * ----------------------------
 * Este interceptor tiene dos misiones clave:
 * 1. Adjuntar 'withCredentials: true' a TODAS las peticiones (salvo excepciones)
 *    para que las Cookies HttpOnly (JWT) viajen automáticamente.
 * 2. Atrapar errores 401 (Token Expirado). Cuando esto pasa, pausa las peticiones
 *    entrantes, llama al endpoint de refresh para renovar la cookie, y luego
 *    reintenta las peticiones pausadas.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // 1. Clonar la petición para agregar withCredentials y que viajen las cookies
  let authReq = req.clone({
    withCredentials: true
  });

  // 2. Pasar la petición al siguiente manejador y atrapar posibles errores
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si el error es 401 y NO es en la ruta de refresh o login
      if (error.status === 401 && !req.url.includes('/auth/refresh') && !req.url.includes('/auth/login')) {
        return handle401Error(authReq, next, authService);
      }

      // Si es otro error o falló el refresh mismo, lo dejamos pasar para que el servicio lo maneje
      return throwError(() => error);
    })
  );
};

function handle401Error(request: HttpRequest<unknown>, next: HttpHandlerFn, authService: AuthService): Observable<HttpEvent<unknown>> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null); // Bloqueamos la cola de peticiones

    return authService.refresh().pipe(
      switchMap((tokenResponse) => {
        isRefreshing = false;
        refreshTokenSubject.next(tokenResponse); // Liberamos la cola
        // Reintentamos la petición original. Las cookies nuevas ya se mandan solas por el navegador.
        return next(request);
      }),
      catchError((err) => {
        isRefreshing = false;
        // Si el refresh falló (token expirado o inválido), chau sesión.
        authService.logout();
        return throwError(() => err);
      })
    );
  } else {
    // Si ya hay un refresh en curso, las demás peticiones esperan acá hasta que refreshTokenSubject emita algo
    return refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(() => {
        return next(request);
      })
    );
  }
}
