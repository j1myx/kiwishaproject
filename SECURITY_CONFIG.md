# Configuración de Spring Security - Fase 4

## Resumen de Implementación

### Archivos Creados

1. **SecurityConfig.java**
   - Configuración principal de Spring Security
   - Definición de rutas públicas y protegidas
   - Configuración de autenticación por formulario
   - Remember-me token (7 días de validez)
   - CSRF habilitado para formularios, deshabilitado para APIs REST
   - Página de acceso denegado personalizada

2. **CustomUserDetailsService.java**
   - Implementación de UserDetailsService
   - Carga usuarios desde la base de datos usando UsuarioRepository
   - Autenticación basada en email (usado como username)
   - Validación de usuarios activos
   - Mapeo de roles con prefijo ROLE_

3. **DataInitializer.java**
   - Inicializador de datos al arrancar la aplicación
   - Crea roles ADMIN y CLIENTE si no existen
   - Crea usuario administrador por defecto:
     - Email: admin@kiwisha.com
     - Password: admin123
     - Rol: ADMIN

4. **RolRepository.java**
   - Repositorio para la entidad Rol
   - Método findByNombre() para buscar roles

5. **RolUsuarioRepository.java**
   - Repositorio para la relación Usuario-Rol
   - Método findByUsuarioUsuarioId() para obtener roles de un usuario

## Configuración de Rutas

### Rutas Públicas (sin autenticación)
- `/api/public/**` - APIs públicas
- `/productos/**` - Catálogo de productos
- `/categorias/**` - Categorías
- `/carrito/**` - Carrito de compras
- `/checkout/**` - Proceso de compra
- `/css/**`, `/js/**`, `/images/**` - Recursos estáticos
- `/login`, `/registro` - Páginas de autenticación

### Rutas de Administrador (requieren rol ADMIN)
- `/admin/**` - Panel administrativo
- `/api/admin/**` - APIs administrativas

### Rutas de Cliente (requieren autenticación)
- `/mi-cuenta/**` - Cuenta del cliente
- `/mis-pedidos/**` - Historial de pedidos
- `/api/cliente/**` - APIs del cliente

### Otras rutas
- Cualquier ruta no especificada requiere autenticación

## Configuración de Login

- **Página de login**: `/login`
- **Proceso de login**: `POST /login`
- **Parámetros**:
  - Username: `email`
  - Password: `password`
  - Remember-me: `remember-me` (opcional)
- **Redirección exitosa**: `/admin/dashboard`
- **Redirección en error**: `/login?error=true`

## Configuración de Logout

- **URL**: `/logout`
- **Redirección**: `/login?logout=true`
- **Acciones**:
  - Invalida la sesión HTTP
  - Elimina cookie JSESSIONID
  - Limpia el contexto de seguridad

## Usuario Administrador por Defecto

Para acceder al sistema inicialmente:
- **Email**: admin@kiwisha.com
- **Contraseña**: admin123
- **Rol**: ADMIN

⚠️ **IMPORTANTE**: Cambiar la contraseña después del primer login en producción

## Codificación de Contraseñas

- Algoritmo: BCrypt
- Bean: `PasswordEncoder` configurado en SecurityConfig
- Usado automáticamente por Spring Security para verificar contraseñas

## Próximos Pasos

- Crear controladores REST API (Fase 5)
- Crear controladores web con Thymeleaf (Fase 6)
- Implementar página de login personalizada
- Agregar validación de sesiones y manejo de errores
