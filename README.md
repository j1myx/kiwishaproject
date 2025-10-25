# 🌾 Kiwisha E-Commerce Platform

Plataforma de comercio electrónico para productos andinos y derivados de kiwicha.

## 📋 Descripción del Proyecto

Kiwisha es una aplicación web full-stack que permite:
- **Usuario Final**: Navegar, buscar y comprar productos andinos
- **Administrador**: Gestionar productos, contenido, pedidos y configuración del sitio

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

## 🎯 Funcionalidades Implementadas (Fase 1)

### ✅ Completado

1. **Estructura MVC Refactorizada**
   - Separación clara de capas (Model, Repository, Service, Controller)
   - Patrones de diseño aplicados (Repository, Service Layer, DTO)

2. **Modelo de Datos Completo**
   - 15 entidades JPA con relaciones
   - Auditoría automática (creado_por, creado_en, etc.)
   - Validaciones con Bean Validation
   - 6 entidades nuevas identificadas y creadas

3. **Repositorios Spring Data JPA**
   - Métodos de consulta personalizados
   - Paginación y ordenamiento
   - Consultas JPQL optimizadas

4. **Configuración del Proyecto**
   - application.properties completo
   - pom.xml con todas las dependencias
   - Configuración JPA y ModelMapper
   - OpenAPI/Swagger configurado
   - Manejo global de excepciones

5. **Base de Datos**
   - Scripts SQL actualizados
   - Tablas nuevas creadas
   - Índices para optimización
   - Datos iniciales de configuración

## 📅 Fases del Proyecto

### Fase 1: Fundamentos ✅ (Completada)
- [x] Estructura MVC
- [x] Entidades JPA completas
- [x] Repositorios
- [x] Configuración base

### Fase 2: Lógica de Negocio (Próxima)
- [ ] Services con transacciones
- [ ] DTOs y Mappers
- [ ] Spring Security
- [ ] Validaciones avanzadas

### Fase 3: APIs y Controladores
- [ ] REST API Controllers
- [ ] Web Controllers
- [ ] Manejo de sesiones

### Fase 4: Integración Frontend
- [ ] Templates Thymeleaf
- [ ] Integración con APIs
- [ ] Formularios dinámicos

### Fase 5: Características Avanzadas
- [ ] Pasarela de pago
- [ ] Módulo de reportería
- [ ] Gestión de imágenes
- [ ] Notificaciones por email

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

**Última actualización**: Octubre 2025
**Versión**: 1.0.0 (Fase 1 Completada)
