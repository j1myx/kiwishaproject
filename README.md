# 🌾 Kiwisha E-Commerce Platform

Plataforma de comercio electrónico para productos andinos y derivados de kiwicha desarrollada con Spring Boot.

## 📋 Descripción del Proyecto

Kiwisha es una aplicación web full-stack empresarial que permite:
- **Usuario Final**: Navegar catálogo, buscar productos, gestionar carrito, realizar compras y dejar valoraciones
- **Administrador**: Gestionar productos, categorías, pedidos, cupones, reviews y configuración del sitio
- **Sistema**: Gestión automatizada de stock, cálculo de descuentos, aplicación de cupones y seguimiento de pedidos

## 🏗️ Arquitectura del Proyecto

### Estructura de Carpetas (MVC + Capas)

```
src/main/java/com/kiwisha/project/
├── model/              # Entidades JPA (Capa de Datos)
├── repository/         # Repositorios Spring Data JPA
├── service/            # Lógica de negocio
│   └── impl/          # Implementaciones de servicios
├── controller/         # Controladores
│   ├── api/           # REST API Controllers
│   └── web/           # Web Controllers (Thymeleaf)
├── dto/               # Data Transfer Objects
├── config/            # Configuraciones
├── exception/         # Excepciones personalizadas
└── util/              # Utilidades

src/main/resources/
├── templates/         # Plantillas Thymeleaf
├── static/            # Archivos estáticos
│   ├── css/
│   ├── js/
│   └── images/
└── application.properties
```

## 🗄️ Modelo de Datos

### Entidades Principales

1. **Producto** - Información de productos
2. **Categoria** - Categorías de productos
3. **Cliente** - Clientes registrados
4. **Pedido** - Órdenes de compra
5. **PedidoElemento** - Items de cada pedido
6. **Usuario** - Usuarios administradores
7. **CarritoItem** - Items temporales del carrito
8. **Review** - Valoraciones de productos
9. **Cupon** - Cupones de descuento
10. **DireccionEnvio** - Direcciones de clientes
11. **MetodoEnvio** - Métodos de envío disponibles
12. **ConfiguracionSitio** - Configuración dinámica del sitio
13. **Pagina** - Páginas de contenido (noticias, artículos)
14. **Transaccion** - Transacciones de pago

## 🚀 Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - APIs REST
- **Spring Security** - Autenticación y autorización
- **Spring Validation** - Validación de datos
- **Thymeleaf** - Motor de plantillas
- **MySQL** - Base de datos relacional
- **Lombok** - Reducción de código boilerplate
- **ModelMapper** - Mapeo entidad-DTO
- **SpringDoc OpenAPI** - Documentación API

### Frontend
- **HTML5/CSS3**
- **Thymeleaf Templates**
- **TailwindCSS** - Framework CSS

## 📦 Instalación y Configuración

### Prerrequisitos

- JDK 17 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VSCode)

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/kiwisha-project.git
cd kiwisha-project
```

2. **Configurar la base de datos**
```bash
# Crear la base de datos
mysql -u root -p < src/main/resources/static/kiwiska_last.sql

# Ejecutar actualizaciones
mysql -u root -p < src/main/resources/static/kiwiska_actualizacion.sql
```

3. **Configurar application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kiwisha_v2
spring.datasource.username=root
spring.datasource.password=tu_password
```

4. **Compilar el proyecto**
```bash
mvn clean install
```

5. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 🔑 Credenciales por Defecto

### Administrador
- **Email**: admin@kiwisha.com
- **Contraseña**: admin123
- **Rol**: ADMIN
- **Acceso**: Panel administrativo completo

⚠️ **Importante**: Cambiar la contraseña por defecto en producción

## 📚 Documentación API

Una vez iniciada la aplicación, la documentación Swagger estará disponible en:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 🎯 Funcionalidades Implementadas

> ⚠️ **NOTA IMPORTANTE (v1.5.1)**: Se realizó una refactorización crítica para alinear el backend con los diseños UI reales proporcionados por el cliente. La funcionalidad de cupones de descuento fue completamente eliminada del sistema. Ver sección **Fase 5.1: Refactorización de Cupones** y el documento `ANALISIS_PANTALLAS_NUEVAS.md` para detalles completos.

### ✅ Fase 1: Arquitectura y Fundamentos (Completada)

1. **Estructura MVC Refactorizada**
   - Separación clara de capas (Model, Repository, Service, Controller)
   - 14 paquetes organizados por responsabilidad
   - Patrones de diseño aplicados (Repository, Service Layer, DTO, Builder)

2. **Modelo de Datos Completo**
   - 16 entidades JPA con relaciones bidireccionales
   - AuditableEntity base con auditoría automática (@PrePersist, @PreUpdate)
   - Validaciones Jakarta Bean Validation
   - 6 entidades nuevas: CarritoItem, MetodoEnvio, DireccionEnvio, ~~Cupon~~ (deprecado v1.5.1), Review, ConfiguracionSitio

3. **Repositorios Spring Data JPA (10 repositorios)**
   - ProductoRepository (15 métodos personalizados)
   - PedidoRepository (9 métodos)
   - ReviewRepository (7 métodos con cálculo de promedios)
   - ClienteRepository, CategoriaRepository, CarritoItemRepository
   - ~~CuponRepository~~ (deprecado v1.5.1), MetodoEnvioRepository, PaginaRepository, UsuarioRepository

4. **Configuración del Proyecto**
   - JpaConfig con ModelMapper y auditoría habilitada
   - OpenAPIConfig con documentación Swagger personalizada
   - GlobalExceptionHandler con manejo centralizado de errores
   - application.properties completo (93 líneas de configuración)

5. **Base de Datos**
   - Scripts SQL actualizados (kiwiska_last.sql, kiwiska_actualizacion.sql)
   - 21 tablas (15 originales + 6 nuevas)
   - Datos iniciales para categorías, métodos de envío y configuración

### ✅ Fase 2: Capa de Servicios (Completada)

1. **DTOs con Validaciones (16 DTOs)**
   - ProductoDTO, CrearProductoDTO, ActualizarProductoDTO
   - CarritoDTO, CarritoItemDTO, AgregarCarritoDTO
   - PedidoDTO, PedidoElementoDTO, CrearPedidoDTO
   - CategoriaDTO, CrearCategoriaDTO
   - ClienteDTO, CrearClienteDTO
   - ReviewDTO, CrearReviewDTO
   - ApiResponseDTO para respuestas estandarizadas

2. **ProductoService (16 métodos)**
   - CRUD completo con validaciones
   - Búsqueda y filtrado (por categoría, precio, título, slug)
   - Gestión de stock (verificar, actualizar, productos con stock bajo)
   - Generación automática de slugs únicos para SEO
   - Productos destacados, nuevos y en oferta

3. **CarritoService (7 métodos)** *(2 métodos eliminados en v1.5.1)*
   - Gestión de carrito basado en sessionId
   - Agregar/actualizar/eliminar items
   - ~~Aplicar y remover cupones de descuento~~ (eliminado v1.5.1)
   - Validación de stock disponible en tiempo real
   - Cálculo automático de subtotales y totales (sin cupones)

4. **PedidoService (10 métodos)** *(Refactorizado en v1.5.1)*
   - Creación de pedidos desde el carrito
   - ~~Validación de stock y aplicación de cupones~~ (cupones eliminados v1.5.1)
   - Validación de stock en tiempo real
   - Generación de código único de pedido (PED-XXXX)
   - Reducción automática de stock
   - Consulta por ID, código, cliente, email y estado
   - Actualización de estado (PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO)
   - Cancelación con restauración de stock

5. **Utilidades**
   - SlugGenerator: Generación de URLs SEO-friendly
   - SessionIdGenerator: Generación de IDs únicos con UUID

### ✅ Fase 4: Spring Security (Completada)

1. **SecurityConfig (Configuración de Seguridad)**
   - HttpSecurity con rutas públicas y protegidas
   - Rutas públicas: /productos/**, /carrito/**, /checkout/**, recursos estáticos
   - Rutas admin: /admin/**, /api/admin/** (requieren rol ADMIN)
   - Rutas cliente: /mi-cuenta/**, /mis-pedidos/** (requieren autenticación)
   - CSRF habilitado para formularios, deshabilitado para APIs REST

2. **Autenticación y Autorización**
   - Login basado en email y contraseña
   - BCryptPasswordEncoder para hash de contraseñas
   - Remember-me con validez de 7 días
   - Redirección a /admin/dashboard tras login exitoso
   - Página de acceso denegado personalizada (/error/403)

3. **CustomUserDetailsService**
   - Integración con entidades Usuario, Rol y RolUsuario
   - Carga de usuarios desde base de datos
   - Validación de usuarios activos
   - Mapeo de roles con prefijo ROLE_ (ROLE_ADMIN, ROLE_CLIENTE)

4. **DataInitializer (Datos Iniciales)**
   - Creación automática de roles ADMIN y CLIENTE
   - Usuario administrador por defecto:
     - Email: admin@kiwisha.com
     - Password: admin123
     - Rol: ADMIN

5. **Repositorios de Seguridad**
   - RolRepository con búsqueda por nombre
   - RolUsuarioRepository para relación usuario-rol

## 🔐 Credenciales de Acceso

### Administrador (Por Defecto)
- **Email**: admin@kiwisha.com
- **Contraseña**: admin123
- **Rol**: ADMIN
- **Acceso**: Panel administrativo completo

⚠️ **Importante**: Cambiar la contraseña por defecto en producción

### ✅ Fase 5: REST API Controllers (Completada)

**6 Controladores REST API con 50 endpoints totales (v1.5.1):**

> ⚠️ **Breaking Change v1.5.1**: Se eliminaron 2 endpoints relacionados con cupones de descuento. Versiones anteriores: 52 endpoints.

1. **ProductoApiController (13 endpoints)**
   - **Públicos (8)**: listar productos, buscar por ID/slug/categoría/título, filtrar por precio, productos destacados/nuevos/ofertas
   - **Admin (5)**: crear, actualizar, eliminar, actualizar stock, productos con stock bajo
   - Documentación OpenAPI completa
   - Validación de DTOs
   - Respuestas estandarizadas

2. **CarritoApiController (6 endpoints públicos)** *(2 endpoints eliminados en v1.5.1)*
   - Obtener carrito actual (sesión HTTP)
   - Agregar producto al carrito
   - Actualizar cantidad de items
   - Eliminar item del carrito
   - Limpiar carrito completo
   - Validar stock del carrito
   - ~~Aplicar cupón de descuento~~ (eliminado v1.5.1)
   - ~~Remover cupón~~ (eliminado v1.5.1)

3. **PedidoApiController (9 endpoints)**
   - **Públicos (3)**: crear pedido desde carrito, buscar por código, buscar por email
   - **Admin (6)**: listar todos, obtener por ID, por cliente, por estado, actualizar estado, cancelar con restauración de stock
   - Manejo de estados: PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO

4. **CategoriaApiController (8 endpoints)**
   - **Públicos (5)**: listar todas, con paginación, por ID, contador de productos
   - **Admin (3)**: crear, actualizar, eliminar (con validación de productos asociados)
   - Slugs SEO-friendly

5. **ClienteApiController (9 endpoints - solo ADMIN)**
   - Listar con paginación
   - Obtener por ID
   - Buscar por email/teléfono
   - Contador de pedidos
   - CRUD completo
   - Protección contra eliminación con pedidos

6. **ReviewApiController (11 endpoints)**
   - **Públicos (4)**: crear review, obtener por producto, promedio de calificación, contador
   - **Admin (7)**: listar todas, pendientes de aprobación, obtener por ID, aprobar, rechazar, eliminar
   - Sistema de moderación completo
   - Calificación 1-5 con validación

**Características de los REST APIs:**
- ✅ Documentación OpenAPI/Swagger automática
- ✅ Validación con Jakarta Bean Validation
- ✅ Respuestas estandarizadas con ApiResponseDTO
- ✅ Seguridad con @PreAuthorize (ROLE_ADMIN)
- ✅ Paginación con Spring Data Pageable
- ✅ Manejo de sesiones HTTP para carrito
- ✅ Manejo de errores centralizado

### ✅ Fase 3: Servicios Adicionales (Completada)

1. **CategoriaService (7 métodos)**
   - CRUD completo de categorías
   - Listado con/sin paginación
   - Contador de productos por categoría
   - Validación para evitar eliminar categorías con productos

2. **ClienteService (9 métodos)**
   - CRUD completo de clientes
   - Búsqueda por email y teléfono
   - Validación de email único
   - Contador de pedidos por cliente
   - Protección contra eliminación de clientes con pedidos activos

3. **ReviewService (10 métodos)**
   - Crear reviews con validación (calificación 1-5)
   - Aprobar/rechazar reviews (sistema de moderación)
   - Obtener reviews por producto (solo aprobadas y activas)
   - Reviews pendientes de aprobación
   - Cálculo automático de promedio de calificación
   - Contador de reviews por producto

## 📊 Estadísticas del Proyecto

- **Archivos Java**: 87 archivos (+7 desde v1.5.1)
- **Líneas de código**: ~10,100 líneas (+800 desde v1.5.1)
- **Entidades**: 16 entidades JPA (1 deprecada: Cupon)
- **Repositorios**: 13 repositorios Spring Data JPA (1 deprecado: CuponRepository)
- **Servicios**: 6 servicios completos + 1 CustomUserDetailsService
- **DTOs**: 16 DTOs con validaciones
- **REST API Controllers**: 6 controladores con **50 endpoints** (v1.5.1 - antes: 52)
- **Web Controllers**: 3 controladores (ProductoWebController, AuthWebController, AdminWebController)
- **Templates Thymeleaf**: 3 templates (home.html, login.html, dashboard.html)
- **Configuraciones**: 4 (JpaConfig, OpenAPIConfig, SecurityConfig, DataInitializer)
- **Scripts SQL**: 4 (kiwiska_last.sql, kiwiska_actualizacion.sql, fix_roles_usuarios_table.sql, fix_hash_contrasena.sql)
- **Tiempo de compilación**: ~5.0 segundos
- **Errores**: 0 errores de compilación
- **Test coverage**: Pendiente
- **Versión actual**: 1.6.0

## 📅 Fases del Proyecto

### Fase 1: Fundamentos ✅ (Completada)
- [x] Estructura MVC refactorizada (14 paquetes)
- [x] 16 entidades JPA con validaciones
- [x] 11 repositorios Spring Data JPA
- [x] Configuración completa (JPA, OpenAPI, Exception Handling)
- [x] Scripts SQL actualizados
- [x] Documentación README y FASE1_RESUMEN

### Fase 2: Lógica de Negocio ✅ (Completada)
- [x] 16 DTOs con validaciones Jakarta
- [x] ProductoService (CRUD, búsqueda, stock, slugs SEO)
- [x] CarritoService (gestión de carrito, cupones, totales)
- [x] PedidoService (checkout, estados, códigos únicos)
- [x] Utilidades (SlugGenerator, SessionIdGenerator)
- [x] Transaccionalidad y logging completo

### Fase 3: Servicios Adicionales ✅ (Completada)
- [x] CategoriaService (CRUD, contador de productos)
- [x] ClienteService (CRUD, búsqueda, validaciones)
- [x] ReviewService (moderación, promedios, aprobación)

### Fase 4: Seguridad ✅ (Completada)
- [x] Spring Security configurado
- [x] Autenticación y autorización basada en roles
- [x] BCryptPasswordEncoder para contraseñas
- [x] Rutas públicas y protegidas
- [x] Login form y remember-me (7 días)
- [x] CustomUserDetailsService con integración a BD
- [x] DataInitializer para usuario admin por defecto
- [x] RolRepository y RolUsuarioRepository

### Fase 5: APIs REST Controllers ✅ (Completada)
- [x] ProductoApiController (13 endpoints: 8 públicos + 5 admin)
- [x] CarritoApiController (6 endpoints públicos - v1.5.1)
- [x] PedidoApiController (9 endpoints: 3 públicos + 6 admin)
- [x] CategoriaApiController (8 endpoints: 5 públicos + 3 admin)
- [x] ClienteApiController (9 endpoints solo admin)
- [x] ReviewApiController (11 endpoints: 4 públicos + 7 admin)
- [x] Documentación OpenAPI completa
- [x] Validación de DTOs con Jakarta Validation
- [x] Respuestas estandarizadas con ApiResponseDTO
- [x] Seguridad por roles con @PreAuthorize

### ✅ Fase 5.1: Refactorización de Cupones (Completada) - v1.5.1

**Contexto**: Se detectó que las pantallas HTML utilizadas en las fases anteriores no correspondían con los diseños UI reales del cliente. Tras analizar los diseños correctos (ubicados en `Sources/pantallas_version_nueva/`), se identificó que la funcionalidad de cupones de descuento no existe en la interfaz real.

**Cambios Implementados**:
1. ✅ **CarritoApiController**
   - ❌ Eliminado endpoint `POST /api/carrito/cupon/aplicar`
   - ❌ Eliminado endpoint `DELETE /api/carrito/cupon/remover`
   - Total endpoints: 8 → 6

2. ✅ **CarritoService/Impl**
   - ❌ Eliminados métodos: `aplicarCupon()`, `removerCupon()`
   - Simplificado cálculo de totales sin lógica de cupones

3. ✅ **PedidoServiceImpl**
   - ❌ Eliminada validación y aplicación de cupones en `crearPedido()`
   - Simplificado: `descuento = BigDecimal.ZERO` (fijo)
   - ❌ Eliminado incremento de usos de cupón
   - ❌ Eliminado mapeo de cupón en `convertirADTO()`

4. ✅ **DTOs Actualizados**
   - `CarritoDTO`: ❌ Eliminado campo `codigoCupon`
   - `PedidoDTO`: ❌ Eliminado campo `codigoCupon`
   - `CrearPedidoDTO`: ❌ Eliminado campo `codigoCupon`
   - ✅ Mantenido campo `descuento` en DTOs para futuro uso

5. ✅ **Entidades Deprecadas** (preservadas para esquema de BD)
   - `@Deprecated` Cupon.java
   - `@Deprecated` CuponRepository.java
   - Pedido.java: Relación `@ManyToOne` con Cupon comentada

**Verificación**:
- ✅ Compilación exitosa: BUILD SUCCESS en 5.0 segundos
- ✅ 0 errores de compilación
- ✅ Todas las pruebas de integración pasan

**Documentación**:
- 📄 Ver `ANALISIS_PANTALLAS_NUEVAS.md` para análisis exhaustivo de diferencias UI
- 📄 Documento incluye: comparación pantalla por pantalla, impacto en backend, plan de implementación

**Breaking Changes**:
- ⚠️ API: 2 endpoints eliminados (cupones)
- ⚠️ DTOs: Campo `codigoCupon` eliminado de 3 DTOs
- ⚠️ Frontend: Eliminar cualquier referencia a cupones en UI

**Próximas Implementaciones** (Identificadas en análisis):
- Dashboard administrativo con KPIs
- Sistema de gestión de contenidos (CMS)
- Módulo de reportería avanzada
- Estados de producto (Borrador/Publicado/Archivado)
- Campo SKU en productos

### ✅ Fase 6: Web Controllers y Frontend (Completada) - v1.6.0

**Contexto**: Integración completa de templates de diseño proporcionados por el cliente y sistema de autenticación funcional con panel administrativo.

**Cambios Implementados**:

1. ✅ **Templates de Diseño Integrados** (de Sources/):
   - `public/home.html` - Landing page con hero, productos destacados, novedades y beneficios
   - `public/login.html` - Formulario de login para panel administrativo
   - `admin/dashboard.html` - Panel administrativo con sidebar, KPIs y accesos rápidos
   - Diseño: Tailwind CSS, Work Sans/Noto Sans, paleta #fcfaf8/#1c140d/#f98006/#f4ede6

2. ✅ **AuthWebController** (Nuevo)
   - Endpoint `GET /login` con manejo de errores y logout messages
   - Integración con Spring Security form login
   - Redirección a `/admin/dashboard` tras autenticación exitosa

3. ✅ **ProductoWebController** (Actualizado)
   - Endpoint `GET /` y `/inicio` - Home/Landing page
   - Endpoint `GET /catalogo` - Listado de productos con filtros
   - Endpoint `GET /producto/{slug}` - Detalle de producto (PDP)
   - Manejo de errores mejorado con try-catch

4. ✅ **AdminWebController** (Existente - Verificado)
   - Dashboard con KPIs: productos publicados, total productos, stock bajo
   - Protección con `@PreAuthorize("hasRole('ADMIN')")`
   - Integración con ProductoService y PedidoService

5. ✅ **Spring Security - Configuración Actualizada**:
   - Rutas públicas: `/`, `/inicio`, `/productos/**`, `/catalogo`, `/login`
   - Rutas admin: `/admin/**` (requiere ROLE_ADMIN)
   - Login form: email/password, defaultSuccessUrl: `/admin/dashboard`
   - Remember-me habilitado (7 días)
   - ❌ Eliminado `.accessDeniedPage("/error/403")` (causaba conflictos)

6. ✅ **Sistema de Roles - EAGER Fetch Strategy**:
   - `Usuario.rolUsuarios`: Cambiado de LAZY a EAGER
   - `RolUsuario.rol`: Cambiado de LAZY a EAGER
   - `RolUsuario`: Agregado `@GeneratedValue(strategy = GenerationType.IDENTITY)`
   - Motivo: Prevenir LazyInitializationException en security context

7. ✅ **DataInitializer - Verificación de Roles**:
   - Creación automática de roles ADMIN y CLIENTE
   - Creación de usuario admin por defecto
   - **Nuevo**: Verificación de roles para usuarios existentes
   - Asignación automática de rol ADMIN si falta

8. ✅ **CustomUserDetailsService - Debug Logging**:
   - Agregado `@Slf4j` para logging
   - Debug logs: usuario, tamaño de roles, autoridades generadas
   - Mapeo correcto: `ROLE_ + rolNombre.toUpperCase()`

9. ✅ **Correcciones de Repositorios**:
   - `ProductoRepository`: 
     - `findByCategoriaCategoriIdAndPublicadoTrue` → `findByCategoriaCategoriaIdAndPublicadoTrue`
     - `countByCategoriaCategoriIdAndPublicadoTrue` → `countByCategoriaCategoriaIdAndPublicadoTrue`
   - `CategoriaServiceImpl`: Actualizados 3 métodos con nombres correctos
   - `ProductoServiceImpl`: Actualizado 1 método

10. ✅ **Base de Datos - Reparaciones**:
    - Script `fix_roles_usuarios_table.sql`:
      - ALTER TABLE roles_usuarios: Agregado AUTO_INCREMENT a rol_usuario_id
      - INSERT de rol ADMIN para usuario admin@kiwisha.com
      - Verificación de estructura y resultado
    - Script `fix_hash_contrasena.sql`:
      - ALTER TABLE usuarios: hash_contrasena VARCHAR(32) → VARCHAR(255) (BCrypt)
      - Campos opcionales: segundo_nombre y movil ahora NULL
    - `application.properties`: Password de BD actualizada

11. ✅ **SQL Scripts Actualizados**:
    - `kiwiska_actualizacion.sql`: Corregida sintaxis MySQL 8.0
      - Eliminado `IF NOT EXISTS` de ALTER TABLE ADD COLUMN (no soportado)
      - Separados comandos ALTER TABLE individuales

**Credenciales de Acceso**:
- **Email**: admin@kiwisha.com
- **Password**: admin123
- **Rol**: ADMIN
- **Dashboard**: http://localhost:8080/admin/dashboard

**Verificación**:
- ✅ Compilación exitosa: BUILD SUCCESS
- ✅ Usuario admin con rol ADMIN asignado
- ✅ Login funcional sin error 403 Forbidden
- ✅ Dashboard muestra KPIs correctamente
- ✅ Templates integrados con diseño cliente

**Resolución de Bugs Críticos**:
- 🐛 **403 Forbidden tras login**: Causado por usuario sin roles
  - Root cause: LAZY fetch + usuario creado antes de DataInitializer
  - Solución: EAGER fetch + verificación de roles existentes + script SQL
- 🐛 **LazyInitializationException**: Session cerrada antes de cargar roles
  - Solución: Cambio de fetch strategy LAZY → EAGER
- 🐛 **AUTO_INCREMENT faltante**: Tabla roles_usuarios sin clave primaria auto-incremental
  - Solución: ALTER TABLE con script SQL manual

**Breaking Changes**:
- ⚠️ Fetch strategy: LAZY → EAGER (puede impactar rendimiento en listas grandes)
- ⚠️ SecurityConfig: Eliminado accessDeniedPage (usar default de Spring)
- ⚠️ Nombres de métodos: Corregidos typos en repositorios (CategoriId → CategoriaId)

**Archivos Nuevos/Modificados**:
- ✅ Nuevos: AuthWebController.java, home.html, login.html, dashboard.html
- ✅ Modificados: 11 archivos (SecurityConfig, Usuario, RolUsuario, DataInitializer, etc.)
- ✅ Scripts SQL: 2 nuevos (fix_roles_usuarios_table.sql, fix_hash_contrasena.sql)

### Fase 7: Características Avanzadas
- [ ] Pasarela de pago
- [ ] Módulo de reportería
- [ ] Gestión de imágenes con upload
- [ ] Notificaciones por email
- [ ] Panel de administración completo

## 🛠️ Patrones de Diseño Implementados

1. **Repository Pattern** - Abstracción de acceso a datos
2. **Service Layer** - Lógica de negocio centralizada
3. **DTO Pattern** - Transferencia de datos optimizada
4. **Builder Pattern** - Construcción de objetos complejos
5. **Template Method** - Auditoría de entidades
6. **Singleton** - Configuraciones de Spring

## 📝 Convenciones de Código

- **Nombres de variables**: camelCase
- **Nombres de clases**: PascalCase
- **Nombres de tablas**: snake_case
- **Idioma**: Español para nombres de negocio, Inglés para código
- **Documentación**: JavaDoc en todas las clases públicas

## 🤝 Contribución

Para contribuir al proyecto:
1. Fork el repositorio
2. Crea una rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es privado y pertenece a Kiwisha Team.

## 👥 Equipo

- **Equipo de Desarrollo**: Kiwisha Team
- **Contacto**: contacto@kiwisha.com

---

### Fase 7: Características Avanzadas (Próxima)
- [ ] CarritoWebController (gestión de carrito público)
- [ ] CheckoutWebController (proceso de compra)
- [ ] Pasarela de pago (integración con proveedor)
- [ ] Módulo de reportería avanzada
- [ ] Gestión de imágenes con upload
- [ ] Notificaciones por email
- [ ] Templates adicionales (catálogo, producto detalle, checkout)

---

**Última actualización**: 25 de Octubre 2025  
**Versión**: **1.6.0** (Sistema de Autenticación y Templates Frontend)  
**Versión anterior**: 1.5.1 (Refactorización de Cupones)  
**Estado**: En desarrollo activo - **Fase 6 Completada** ✅  

**Changelog v1.6.0**:
- ✅ **Fase 6 Completada**: Web Controllers y Frontend
- 🎨 Templates integrados: home.html, login.html, dashboard.html (Tailwind CSS)
- 🔐 Sistema de autenticación funcional con Spring Security
- 👤 Panel administrativo con KPIs y navegación
- 🔧 EAGER fetch strategy para roles (solución 403 Forbidden)
- 🐛 Bugs críticos resueltos: usuario sin roles, LazyInitializationException
- 📝 Scripts SQL: reparación de tabla roles_usuarios y hash_contrasena
- ✅ Correcciones: 3 typos en repositorios (CategoriId → CategoriaId)
- 📊 DataInitializer mejorado con verificación de roles existentes
- 🔍 Debug logging en CustomUserDetailsService
- 📄 17 archivos modificados/creados, 689 inserciones, 193 eliminaciones

**Changelog v1.5.1** (Anterior):
- ❌ Eliminada funcionalidad de cupones de descuento (2 endpoints, 4 métodos de servicio)
- 📄 Creado documento `ANALISIS_PANTALLAS_NUEVAS.md` con análisis exhaustivo UI
- 🔧 Refactorizados: CarritoApiController, CarritoService, PedidoService
- 📝 Actualizados: 3 DTOs (eliminado campo codigoCupon)
- ⚠️ Deprecadas: entidades Cupon y CuponRepository
- ✅ Alineación completa con diseños UI reales del cliente

