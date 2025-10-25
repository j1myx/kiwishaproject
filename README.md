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
- **Usuario**: admin
- **Contraseña**: admin123

## 📚 Documentación API

Una vez iniciada la aplicación, la documentación Swagger estará disponible en:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 🎯 Funcionalidades Implementadas

### ✅ Fase 1: Arquitectura y Fundamentos (Completada)

1. **Estructura MVC Refactorizada**
   - Separación clara de capas (Model, Repository, Service, Controller)
   - 14 paquetes organizados por responsabilidad
   - Patrones de diseño aplicados (Repository, Service Layer, DTO, Builder)

2. **Modelo de Datos Completo**
   - 16 entidades JPA con relaciones bidireccionales
   - AuditableEntity base con auditoría automática (@PrePersist, @PreUpdate)
   - Validaciones Jakarta Bean Validation
   - 6 entidades nuevas: CarritoItem, MetodoEnvio, DireccionEnvio, Cupon, Review, ConfiguracionSitio

3. **Repositorios Spring Data JPA (10 repositorios)**
   - ProductoRepository (15 métodos personalizados)
   - PedidoRepository (9 métodos)
   - ReviewRepository (7 métodos con cálculo de promedios)
   - ClienteRepository, CategoriaRepository, CarritoItemRepository
   - CuponRepository, MetodoEnvioRepository, PaginaRepository, UsuarioRepository

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

3. **CarritoService (9 métodos)**
   - Gestión de carrito basado en sessionId
   - Agregar/actualizar/eliminar items
   - Aplicar y remover cupones de descuento
   - Validación de stock disponible en tiempo real
   - Cálculo automático de subtotales y totales

4. **PedidoService (10 métodos)**
   - Creación de pedidos desde el carrito
   - Validación de stock y aplicación de cupones
   - Generación de código único de pedido (PED-XXXX)
   - Reducción automática de stock
   - Consulta por ID, código, cliente, email y estado
   - Actualización de estado (PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO)
   - Cancelación con restauración de stock

5. **Utilidades**
   - SlugGenerator: Generación de URLs SEO-friendly
   - SessionIdGenerator: Generación de IDs únicos con UUID

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

- **Archivos Java**: 69 archivos
- **Líneas de código**: ~7,500 líneas
- **Entidades**: 16 entidades JPA
- **Repositorios**: 11 repositorios Spring Data JPA
- **Servicios**: 6 servicios completos (18 interfaces + implementaciones)
- **DTOs**: 16 DTOs con validaciones
- **Tiempo de compilación**: ~4.5 segundos
- **Errores**: 0 errores de compilación
- **Test coverage**: Pendiente

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

### Fase 4: Seguridad (En progreso)
- [ ] Spring Security configurado
- [ ] Autenticación y autorización
- [ ] BCryptPasswordEncoder
- [ ] Rutas públicas y protegidas
- [ ] Login form y remember-me

### Fase 5: APIs REST Controllers
- [ ] ProductoApiController
- [ ] CarritoApiController
- [ ] PedidoApiController
- [ ] CategoriaApiController
- [ ] ClienteApiController
- [ ] ReviewApiController
- [ ] Documentación OpenAPI completa

### Fase 6: Web Controllers y Frontend
- [ ] ProductoWebController
- [ ] CarritoWebController
- [ ] CheckoutWebController
- [ ] Templates Thymeleaf
- [ ] Conversión de HTMLs existentes
- [ ] Integración con TailwindCSS

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

**Última actualización**: 25 de Octubre 2025
**Versión**: 1.3.0 (Fases 1, 2 y 3 Completadas)
**Estado**: En desarrollo activo - Fase 4 en progreso
