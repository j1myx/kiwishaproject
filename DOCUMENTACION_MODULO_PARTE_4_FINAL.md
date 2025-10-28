# DOCUMENTACIÓN MÓDULO GESTIÓN DE PRODUCTOS - PARTE 4 (FINAL)

## 8. CONFIGURACIÓN Y CONEXIÓN A BASE DE DATOS

### 8.1 application.properties

**Ubicación**: `src/main/resources/application.properties`

```properties
# ============================================
# CONFIGURACIÓN DE BASE DE DATOS MySQL
# ============================================
# Base de datos en hosting online (FreeSQLDatabase)
spring.datasource.url=jdbc:mysql://sql10.freesqldatabase.com:3306/sql10804802?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=sql10804802
spring.datasource.password=SuDZQNHhFB
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Pool de conexiones HikariCP (optimizado para hosting)
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000

# ============================================
# CONFIGURACIÓN JPA/Hibernate
# ============================================
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.open-in-view=false
```

### 8.2 Justificación de Configuraciones

#### 8.2.1 URL de Conexión JDBC

```properties
jdbc:mysql://sql10.freesqldatabase.com:3306/sql10804802?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

**Componentes**:
- `jdbc:mysql://`: Protocolo JDBC para MySQL.
- `sql10.freesqldatabase.com:3306`: Host y puerto del servidor MySQL en hosting.
- `sql10804802`: Nombre de la base de datos.
- `useSSL=true`: Habilita conexión cifrada (obligatorio en hosting).
- `serverTimezone=UTC`: Evita warnings de zona horaria.
- `allowPublicKeyRetrieval=true`: Permite autenticación con clave pública (necesario para MySQL 8+).

#### 8.2.2 HikariCP Connection Pool

**¿Qué es un Connection Pool?**

Un **pool de conexiones** es un conjunto de conexiones a la base de datos que se reutilizan, en lugar de crear una nueva conexión por cada petición.

**Problema sin Connection Pool**:
```
Petición 1: Abrir conexión (300ms) + Query (50ms) + Cerrar conexión (100ms) = 450ms
Petición 2: Abrir conexión (300ms) + Query (50ms) + Cerrar conexión (100ms) = 450ms
Total: 900ms
```

**Con Connection Pool**:
```
Petición 1: Tomar conexión del pool (5ms) + Query (50ms) + Devolver al pool (5ms) = 60ms
Petición 2: Tomar conexión del pool (5ms) + Query (50ms) + Devolver al pool (5ms) = 60ms
Total: 120ms (7.5x más rápido!)
```

**Configuración para Hosting**:
```properties
maximum-pool-size=5       # Máximo 5 conexiones simultáneas (hosting gratuito tiene límites)
minimum-idle=2            # Mantener 2 conexiones abiertas siempre (reduce latencia)
connection-timeout=20000  # Esperar máximo 20 segundos para obtener conexión
idle-timeout=300000       # Cerrar conexiones inactivas después de 5 minutos
max-lifetime=600000       # Renovar conexiones cada 10 minutos (evita timeouts del servidor)
```

**Justificación**:
- **maximum-pool-size=5**: Los hostings gratuitos limitan conexiones concurrentes. 5 es un balance entre rendimiento y restricciones.
- **minimum-idle=2**: Siempre hay conexiones listas, reduciendo latencia de primera petición.
- **idle-timeout/max-lifetime**: Evita que el servidor cierre conexiones por inactividad.

#### 8.2.3 Hibernate DDL Auto

```properties
spring.jpa.hibernate.ddl-auto=update
```

**Opciones disponibles**:
- **none**: No hace nada (producción).
- **validate**: Solo valida que el esquema coincida.
- **update**: Crea/actualiza tablas automáticamente (desarrollo).
- **create**: Elimina y recrea todas las tablas (testing).
- **create-drop**: Como create, pero elimina al cerrar (testing).

**Nuestra elección: `update`**

**Justificación**:
- **Desarrollo ágil**: Cambios en entidades se reflejan automáticamente en la BD.
- **Sin pérdida de datos**: No elimina datos existentes.
- **Limitaciones**: No elimina columnas obsoletas (hay que hacerlo manualmente).

**En producción usaríamos**:
```properties
spring.jpa.hibernate.ddl-auto=validate
```
Y ejecutaríamos migrations con Flyway o Liquibase.

#### 8.2.4 Logging de SQL

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Salida en consola**:
```sql
Hibernate: 
    select
        p1_0.producto_id,
        p1_0.titulo,
        p1_0.precio,
        ...
    from
        productos p1_0
    where
        p1_0.producto_id=?
```

**Ventajas**:
- **Debugging**: Vemos exactamente qué queries ejecuta Hibernate.
- **Optimización**: Detectamos queries N+1 o consultas ineficientes.
- **Aprendizaje**: Entendemos cómo Hibernate traduce JPQL a SQL.

**En producción**: Deshabilitamos con `show-sql=false` (impacta rendimiento).

#### 8.2.5 Batch Processing

```properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

**¿Qué es Batch Processing?**

En lugar de ejecutar 20 INSERTs individuales:
```sql
INSERT INTO productos VALUES (...);
INSERT INTO productos VALUES (...);
... (20 veces)
```

Hibernate agrupa en un solo batch:
```sql
INSERT INTO productos VALUES (...), (...), (...), ... ; -- 20 registros en 1 query
```

**Beneficios**:
- **Reducción de latencia**: 20 round-trips → 1 round-trip.
- **Mejor rendimiento**: MySQL procesa batches más eficientemente.

**Cuándo se activa**: Al guardar múltiples productos (ej: importación masiva).

#### 8.2.6 Open Session In View (OSIV)

```properties
spring.jpa.open-in-view=false
```

**¿Qué es OSIV?**

Por defecto (true), Spring mantiene la sesión de Hibernate abierta hasta que la vista (Thymeleaf) termina de renderizarse.

**Problema**:
- Si la vista accede a relaciones LAZY, Hibernate hace queries adicionales en la capa de presentación.
- **Anti-patrón**: La vista NO debería ejecutar queries, solo mostrar datos.

**Nuestra solución (false)**:
- Obligamos a cargar todas las relaciones en el servicio.
- Si falta algo, obtenemos LazyInitializationException (error claro).
- **Ventaja**: Código más limpio y predecible.

**Ejemplo**:
```java
// ❌ Con OSIV=true (malo)
@GetMapping("/productos/{id}")
public String ver(@PathVariable Integer id, Model model) {
    Producto producto = productoRepository.findById(id).get();
    model.addAttribute("producto", producto);
    return "vista"; // La vista accede a producto.categoria y hace query aquí (malo)
}

// ✅ Con OSIV=false (bueno)
@Override
public ProductoDTO obtenerProductoPorId(Integer id) {
    Producto producto = productoRepository.findById(id).orElseThrow(...);
    
    // Forzar carga de categoría en el servicio
    if (producto.getCategoria() != null) {
        producto.getCategoria().getTitulo(); // Trigger LAZY load
    }
    
    return convertirADTO(producto); // DTO ya tiene todos los datos
}
```

### 8.3 JpaConfig.java (Configuración de Beans)

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Configuración estricta: Solo mapea campos que coinciden exactamente
        mapper.getConfiguration()
              .setMatchingStrategy(MatchingStrategies.STRICT)
              .setFieldMatchingEnabled(true)
              .setFieldAccessLevel(AccessLevel.PRIVATE)
              .setSkipNullEnabled(true); // No sobrescribir con nulls
        
        return mapper;
    }
}
```

**@EnableJpaAuditing**: Activa los callbacks `@PrePersist` y `@PreUpdate` de `AuditableEntity`.

**ModelMapper Configuration**:
- **STRICT**: Solo mapea si los nombres coinciden exactamente (evita mapeos erróneos).
- **fieldMatchingEnabled**: Permite mapear campos privados sin getters/setters.
- **skipNullEnabled**: Si el DTO tiene un campo null, no sobrescribe el valor existente en la entidad.

---

## 9. PATRONES DE DISEÑO APLICADOS

### 9.1 MVC (Model-View-Controller)

**Definición**: Separa la aplicación en tres componentes:
- **Model**: Lógica de negocio y datos (Entidades, Servicios).
- **View**: Presentación (Templates Thymeleaf).
- **Controller**: Maneja peticiones HTTP (AdminWebController).

**Beneficios en nuestro módulo**:
- Cambios en la UI (HTML/CSS) no afectan la lógica de negocio.
- Podemos agregar un REST API Controller sin tocar el servicio.
- Testing más fácil: Probamos el servicio sin levantar el servidor web.

### 9.2 Repository Pattern (DAO)

**Definición**: Abstrae el acceso a datos, encapsulando las queries.

**Implementación**: `ProductoRepository extends JpaRepository`.

**Beneficios**:
- Si cambiamos de MySQL a PostgreSQL, solo modificamos el `application.properties`.
- El servicio no sabe si los datos vienen de una BD, archivo, o API externa.
- Spring Data JPA genera automáticamente la implementación.

**Ejemplo sin Repository (anti-patrón)**:
```java
// ❌ Servicio con SQL directo (acoplado a la BD)
public class ProductoService {
    @Autowired
    private EntityManager em;
    
    public Producto buscar(Integer id) {
        return em.createQuery("SELECT p FROM Producto p WHERE p.id = :id", Producto.class)
                 .setParameter("id", id)
                 .getSingleResult();
    }
}
```

**Con Repository (correcto)**:
```java
// ✅ Servicio desacoplado
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository repository;
    
    public ProductoDTO obtenerProductoPorId(Integer id) {
        Producto producto = repository.findById(id).orElseThrow(...);
        return convertirADTO(producto);
    }
}
```

### 9.3 Service Layer Pattern

**Definición**: Capa intermedia entre controladores y repositorios que contiene la lógica de negocio.

**Justificación**:
- **Transaccionalidad**: Los servicios manejan transacciones (`@Transactional`).
- **Reutilización**: Un servicio puede ser llamado desde múltiples controladores (Web, REST API, Scheduled Tasks).
- **Validaciones complejas**: Lógica que no cabe en validaciones de bean (ej: SKU único).

**Ejemplo**:
```java
@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    
    @Override
    public ProductoDTO crearProducto(CrearProductoDTO dto) {
        // 1. Validaciones de negocio
        validarCategoria(dto.getCategoriaId());
        
        // 2. Generar valores automáticos
        String slug = generarSlugUnico(dto.getTitulo());
        String sku = generarSkuUnico();
        
        // 3. Crear entidad
        Producto producto = mapearAEntidad(dto);
        producto.setSlug(slug);
        producto.setSku(sku);
        
        // 4. Persistir
        Producto guardado = productoRepository.save(producto);
        
        // 5. Convertir y retornar
        return convertirADTO(guardado);
    }
}
```

### 9.4 DTO Pattern (Data Transfer Object)

**Definición**: Objetos simples que transportan datos entre procesos.

**Diferencias con Entidades**:
| Aspecto | Entidad | DTO |
|---------|---------|-----|
| **Propósito** | Representar BD | Transferir datos |
| **Anotaciones** | JPA (`@Entity`, `@Column`) | Validación (`@NotNull`, `@Size`) |
| **Relaciones** | Bidireccionales, LAZY | Aplanadas, solo IDs |
| **Modificabilidad** | Gestionada por Hibernate | Inmutable (idealmente) |

**Ventajas**:
1. **Seguridad**: No exponemos estructura interna de la BD.
2. **Flexibilidad**: Podemos cambiar la entidad sin romper la API.
3. **Performance**: DTOs más ligeros (sin relaciones cargadas).

**Ejemplo**:
```java
// Entidad (compleja, con relaciones)
@Entity
public class Producto {
    private Integer productoId;
    private String titulo;
    private BigDecimal precio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Categoria categoria; // Objeto completo
    
    @OneToMany(mappedBy = "producto")
    private List<Review> reviews; // Lista de objetos
}

// DTO (simple, aplanado)
public class ProductoDTO {
    private Integer productoId;
    private String titulo;
    private BigDecimal precio;
    
    private Integer categoriaId;       // Solo el ID
    private String categoriaTitulo;    // Solo el nombre
    
    private Double promedioCalificacion; // Calculado, no existe en BD
}
```

### 9.5 Builder Pattern

**Definición**: Facilita la creación de objetos complejos con múltiples campos.

**Implementación**: `@Builder` de Lombok.

**Comparación**:

```java
// ❌ Sin Builder (verboso, propenso a errores)
CrearProductoDTO dto = new CrearProductoDTO();
dto.setTitulo("Kiwicha");
dto.setPrecio(new BigDecimal("25.00"));
dto.setCantidad(100);
dto.setEstado(EstadoProducto.BORRADOR);
dto.setCategoriaId(1);
// ... 10 líneas más

// ✅ Con Builder (fluido, legible)
CrearProductoDTO dto = CrearProductoDTO.builder()
    .titulo("Kiwicha")
    .precio(new BigDecimal("25.00"))
    .cantidad(100)
    .estado(EstadoProducto.BORRADOR)
    .categoriaId(1)
    .build();
```

**Ventajas**:
- **Inmutabilidad**: Podemos hacer el objeto final (campos `final`).
- **Validación en build()**: Verificamos campos obligatorios al construir.
- **Legibilidad**: Código más expresivo.

### 9.6 Dependency Injection (DI)

**Definición**: Spring inyecta las dependencias automáticamente.

**Implementación**: `@RequiredArgsConstructor` (Lombok) + campos `final`.

```java
@Service
@RequiredArgsConstructor // Genera constructor con todas las dependencias final
public class ProductoServiceImpl implements ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ModelMapper modelMapper;
    
    // Spring inyecta automáticamente estos 3 beans
}
```

**Ventajas sobre @Autowired**:
```java
// ❌ Field Injection (difícil de testear)
@Service
public class ProductoServiceImpl {
    @Autowired
    private ProductoRepository repository;
}

// ✅ Constructor Injection (fácil de testear)
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl {
    private final ProductoRepository repository;
    
    // En tests, podemos pasar un mock:
    // new ProductoServiceImpl(mockRepository)
}
```

### 9.7 Strategy Pattern (Implícito en Estados)

**Definición**: Diferentes comportamientos según el estado del objeto.

**Implementación**: Enum `EstadoProducto` + lógica condicional.

```java
public enum EstadoProducto {
    BORRADOR,    // No visible, editable
    PUBLICADO,   // Visible, editable con restricciones
    ARCHIVADO    // No visible, solo lectura
}

// En el servicio:
if (producto.getEstado() == EstadoProducto.ARCHIVADO) {
    throw new IllegalStateException("No se puede editar un producto archivado");
}
```

**Alternativa avanzada (no implementada)**:
```java
public interface EstadoProducto {
    boolean puedeEditarse();
    boolean esVisible();
}

public class Borrador implements EstadoProducto {
    public boolean puedeEditarse() { return true; }
    public boolean esVisible() { return false; }
}

public class Publicado implements EstadoProducto {
    public boolean puedeEditarse() { return true; }
    public boolean esVisible() { return true; }
}
```

### 9.8 Template Method (En AuditableEntity)

**Definición**: Define el esqueleto de un algoritmo, permitiendo a subclases sobrescribir pasos específicos.

```java
@MappedSuperclass
public abstract class AuditableEntity {
    
    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        // Las subclases heredan este comportamiento
    }
    
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
        // Las subclases heredan este comportamiento
    }
}

@Entity
public class Producto extends AuditableEntity {
    // Hereda onCreate() y onUpdate()
}
```

---

## 10. TECNOLOGÍAS UTILIZADAS Y JUSTIFICACIÓN

### 10.1 Spring Boot

**¿Qué es?**: Framework que simplifica el desarrollo de aplicaciones Java empresariales.

**Justificación**:
- **Convention over Configuration**: Configuración mínima, máxima productividad.
- **Starter Dependencies**: `spring-boot-starter-web` incluye todo lo necesario (Tomcat, Jackson, Validation).
- **Auto-Configuration**: Spring detecta bibliotecas en el classpath y se auto-configura.
- **Actuator**: Endpoints de monitoreo (`/health`, `/metrics`) para producción.

**Alternativas**:
- **Jakarta EE**: Más complejo, requiere servidor de aplicaciones (WildFly, Payara).
- **Micronaut**: Más ligero, pero ecosistema menos maduro.
- **Quarkus**: Optimizado para GraalVM, pero curva de aprendizaje más alta.

### 10.2 Spring Data JPA

**¿Qué es?**: Abstracción sobre JPA/Hibernate para simplificar acceso a datos.

**Justificación**:
- **Query Methods**: Generación automática de consultas desde nombres de métodos.
- **@Query Annotation**: Consultas JPQL personalizadas cuando es necesario.
- **Paginación Integrada**: `Pageable` y `Page` para listas grandes.
- **Auditoría**: `@EnableJpaAuditing` para campos de auditoría automáticos.

**Alternativas**:
- **JDBC Template**: Más control, pero más código boilerplate.
- **MyBatis**: XML para mapear SQL, más verboso.
- **jOOQ**: Type-safe SQL, pero requiere generación de código.

### 10.3 Hibernate (JPA Provider)

**¿Qué es?**: ORM (Object-Relational Mapping) que mapea objetos Java a tablas SQL.

**Justificación**:
- **Productividad**: No escribimos SQL para operaciones CRUD.
- **Database Agnostic**: Cambiar de MySQL a PostgreSQL es trivial.
- **Lazy Loading**: Optimización automática de queries.
- **Caching**: Cache de primer y segundo nivel para reducir queries.

**Consideraciones**:
- **Query N+1**: Cuidado con relaciones LAZY no optimizadas.
- **Curva de aprendizaje**: Hibernate tiene comportamientos "mágicos" que requieren entendimiento.

### 10.4 MySQL

**¿Qué es?**: Sistema de gestión de bases de datos relacional.

**Justificación**:
- **Popularidad**: Amplia comunidad, documentación abundante.
- **Rendimiento**: Optimizado para lectura (OLTP).
- **Hosting**: Disponible en la mayoría de proveedores (FreeSQLDatabase en nuestro caso).
- **Transacciones ACID**: Garantiza consistencia de datos.

**Alternativas**:
- **PostgreSQL**: Más features avanzados (JSON, full-text search), pero hosting más caro.
- **MariaDB**: Fork de MySQL, compatible pero con mejoras.
- **H2**: Base de datos en memoria, ideal para testing.

### 10.5 Thymeleaf

**¿Qué es?**: Motor de plantillas para generar HTML dinámico.

**Justificación**:
- **Natural Templating**: Los templates son HTML válido.
- **Integración con Spring**: Acceso a beans, seguridad, validaciones.
- **Server-Side Rendering**: SEO-friendly (Google indexa el HTML generado).
- **Expresiones Potentes**: `${...}`, `*{...}`, `@{...}`, `#temporals`, `#numbers`.

**Alternativas**:
- **JSP**: Obsoleto, requiere compilación.
- **Freemarker**: Sintaxis menos intuitiva.
- **React/Vue**: SPA (Single Page Application), requiere API REST.

**¿Por qué no React en este módulo?**:
- SSR (Server-Side Rendering) es más simple para dashboards administrativos.
- No necesitamos reactividad compleja (no es tiempo real).
- Thymeleaf reduce complejidad (sin build tools como Webpack).

### 10.6 Tailwind CSS

**¿Qué es?**: Framework CSS utility-first.

**Justificación**:
- **Productividad**: Clases utilitarias (`bg-blue-500`, `text-white`) en lugar de CSS custom.
- **Responsivo**: Breakpoints integrados (`md:`, `lg:`).
- **Consistencia**: Sistema de diseño predefinido (colores, espaciados).
- **CDN**: Incluimos via `<script>`, sin build process.

**Ejemplo**:
```html
<!-- ❌ Sin Tailwind (CSS custom) -->
<style>
  .boton-primario {
    background-color: #f97316;
    color: white;
    padding: 0.5rem 1.5rem;
    border-radius: 0.5rem;
  }
  .boton-primario:hover {
    background-color: #ea580c;
  }
</style>
<button class="boton-primario">Guardar</button>

<!-- ✅ Con Tailwind (utility classes) -->
<button class="bg-orange-500 hover:bg-orange-600 text-white px-6 py-2 rounded-lg">
  Guardar
</button>
```

### 10.7 Lombok

**¿Qué es?**: Biblioteca que genera código boilerplate automáticamente.

**Justificación**:
- **@Data**: Genera getters, setters, `toString()`, `equals()`, `hashCode()`.
- **@Builder**: Patrón Builder sin escribir código.
- **@RequiredArgsConstructor**: Constructor con dependencias finales (DI).
- **@Slf4j**: Logger sin declarar `private static final Logger log = ...`.

**Impacto en productividad**:
Sin Lombok, `Producto.java` tendría ~500 líneas. Con Lombok: ~150 líneas.

### 10.8 Bean Validation (Jakarta)

**¿Qué es?**: Framework de validación declarativa con anotaciones.

**Justificación**:
- **Declarativo**: Validaciones en el DTO, no en el controlador.
- **Reutilizable**: Mismas validaciones en Web y REST API.
- **Mensajes Personalizados**: `@NotBlank(message = "El título es obligatorio")`.

**Alternativa manual**:
```java
// ❌ Sin Bean Validation (verboso, repetitivo)
if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
    throw new ValidationException("El título es obligatorio");
}
if (dto.getTitulo().length() > 100) {
    throw new ValidationException("El título no puede exceder 100 caracteres");
}
// ... 20 validaciones más

// ✅ Con Bean Validation (declarativo)
@NotBlank(message = "El título es obligatorio")
@Size(max = 100, message = "El título no puede exceder 100 caracteres")
private String titulo;
```

---

## 11. FLUJO DE DATOS COMPLETO (EJEMPLO REAL)

### 11.1 Crear un Producto (Flujo Paso a Paso)

**Escenario**: Administrador crea un producto "Kiwicha Orgánica 500g" y presiona "Publicar".

#### Paso 1: Usuario completa el formulario
- **Template**: `admin/productos/formulario.html`
- **Datos**:
  - Título: "Kiwicha Orgánica 500g"
  - Precio: 25.00
  - Stock: 100
  - Categoría: "Granos Andinos" (ID: 1)
  - Botón: "Publicar"

#### Paso 2: Formulario se envía al controlador
- **HTTP**: `POST /admin/productos`
- **Parámetros**:
  ```
  titulo=Kiwicha Orgánica 500g
  precio=25.00
  cantidad=100
  categoriaId=1
  accion=publicar
  ```

#### Paso 3: Controlador recibe y valida
```java
@PostMapping("/productos")
public String crearProducto(
    @Valid @ModelAttribute("producto") CrearProductoDTO crearDTO,
    BindingResult result,
    @RequestParam(value = "accion") String accion) {
    
    // Spring ejecuta validaciones automáticamente
    if (result.hasErrors()) {
        // Hay errores: @NotBlank, @Min, etc.
        return "admin/productos/formulario";
    }
    
    // Determinar estado según botón
    if ("publicar".equals(accion)) {
        crearDTO.setEstado(EstadoProducto.PUBLICADO);
    }
    
    // Llamar al servicio
    ProductoDTO creado = productoService.crearProducto(crearDTO);
    
    return "redirect:/admin/productos";
}
```

#### Paso 4: Servicio ejecuta lógica de negocio
```java
@Transactional
public ProductoDTO crearProducto(CrearProductoDTO dto) {
    // 4.1 Validar que la categoría existe
    Categoria categoria = categoriaRepository.findById(1)
        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    
    // 4.2 Mapear DTO → Entidad
    Producto producto = modelMapper.map(dto, Producto.class);
    
    // 4.3 Generar slug único
    // "Kiwicha Orgánica 500g" → "kiwicha-organica-500g"
    String slug = generarSlugUnico("Kiwicha Orgánica 500g");
    producto.setSlug(slug);
    
    // 4.4 Generar SKU único
    // Resultado: "KIW-0001"
    String sku = generarSkuUnico();
    producto.setSku(sku);
    
    // 4.5 Asignar relaciones
    producto.setCategoria(categoria);
    producto.setEstado(EstadoProducto.PUBLICADO);
    producto.setPublicado(true);
    
    // 4.6 Guardar en BD
    Producto guardado = productoRepository.save(producto);
    // Hibernate ejecuta:
    // INSERT INTO productos (titulo, slug, sku, precio, cantidad, estado, publicado, categoria_id, creado_en, ...)
    // VALUES ('Kiwicha Orgánica 500g', 'kiwicha-organica-500g', 'KIW-0001', 25.00, 100, 'PUBLICADO', true, 1, NOW(), ...)
    
    // 4.7 Convertir a DTO y retornar
    return convertirADTO(guardado);
}
```

#### Paso 5: Repositorio persiste en BD
```java
// Spring Data JPA genera automáticamente:
productoRepository.save(producto);

// Hibernate traduce a:
INSERT INTO productos (
    producto_id, titulo, slug, sku, precio, cantidad,
    estado, publicado, categoria_id, creado_en, actualizado_en
) VALUES (
    NULL, 'Kiwicha Orgánica 500g', 'kiwicha-organica-500g', 'KIW-0001',
    25.00, 100, 'PUBLICADO', true, 1, '2025-10-28 10:30:00', '2025-10-28 10:30:00'
);
```

#### Paso 6: MySQL ejecuta la query
```sql
-- MySQL en FreeSQLDatabase ejecuta el INSERT
-- Auto-genera producto_id = 123
-- Retorna el ID a Hibernate
```

#### Paso 7: Controlador hace redirect
```java
return "redirect:/admin/productos";
// HTTP 302 Redirect → GET /admin/productos
```

#### Paso 8: Navegador recarga la lista
- **GET** `/admin/productos`
- Controlador llama a `productoService.obtenerProductosConPaginacion(0, 20, "creadoEn")`
- Servicio llama a `productoRepository.findAll(PageRequest.of(0, 20, Sort.by("creadoEn").descending()))`
- Hibernate ejecuta:
  ```sql
  SELECT * FROM productos ORDER BY creado_en DESC LIMIT 20 OFFSET 0;
  ```
- Template `lista.html` renderiza la tabla con el nuevo producto en primera posición.

### 11.2 Diagrama de Secuencia Completo

```
Usuario          Navegador         Controlador         Servicio         Repositorio         MySQL
  |                 |                   |                  |                  |                |
  |--- Llenar form --|                  |                  |                  |                |
  |                 |--- POST /admin/productos ----------->|                  |                |
  |                 |                   |                  |                  |                |
  |                 |                   |--- crearProducto(dto) ------------>|                |
  |                 |                   |                  |                  |                |
  |                 |                   |                  |--- validar categoría ----------->|
  |                 |                   |                  |<--- categoría encontrada --------|
  |                 |                   |                  |                  |                |
  |                 |                   |                  |--- generar slug ---|                |
  |                 |                   |                  |--- generar SKU ----|                |
  |                 |                   |                  |                  |                |
  |                 |                   |                  |--- save(producto) ------------->|
  |                 |                   |                  |                  |--- INSERT ----->|
  |                 |                   |                  |                  |<--- ID:123 -----|
  |                 |                   |                  |<--- producto guardado ------------|
  |                 |                   |                  |                  |                |
  |                 |                   |<--- ProductoDTO -|                  |                |
  |                 |<--- redirect:/admin/productos -------|                  |                |
  |                 |                   |                  |                  |                |
  |                 |--- GET /admin/productos ------------>|                  |                |
  |                 |                   |--- listar() -----|--- findAll() ----|--- SELECT ----->|
  |                 |                   |                  |                  |<--- resultados -|
  |                 |                   |<--- Page<DTO> ---|                  |                |
  |                 |<--- HTML renderizado (lista.html) ---|                  |                |
  |<--- Ver lista --|                   |                  |                  |                |
```

---

## 12. CONCLUSIONES Y BUENAS PRÁCTICAS

### 12.1 Principios SOLID Aplicados

1. **Single Responsibility Principle (SRP)**:
   - Cada clase tiene una única responsabilidad.
   - `ProductoService`: Solo lógica de negocio de productos.
   - `ProductoRepository`: Solo acceso a datos.

2. **Open/Closed Principle (OCP)**:
   - Código abierto para extensión, cerrado para modificación.
   - Ejemplo: Podemos agregar nuevos estados sin modificar código existente (enum).

3. **Liskov Substitution Principle (LSP)**:
   - `ProductoServiceImpl implements ProductoService`.
   - Podemos reemplazar la implementación sin romper el código.

4. **Interface Segregation Principle (ISP)**:
   - Interfaces específicas (`ProductoService`) en lugar de una interfaz gigante.

5. **Dependency Inversion Principle (DIP)**:
   - Dependemos de abstracciones (`ProductoRepository`, `ProductoService`), no de implementaciones concretas.

### 12.2 Ventajas del Enfoque Implementado

1. **Mantenibilidad**: Código organizado en capas facilita cambios.
2. **Escalabilidad**: Podemos agregar más servicios sin afectar existentes.
3. **Testabilidad**: Cada capa puede testearse independientemente.
4. **Seguridad**: Spring Security protege endpoints administrativos.
5. **Performance**: Connection pooling, batch processing, paginación.
6. **Trazabilidad**: Logging exhaustivo, auditoría automática.

### 12.3 Posibles Mejoras Futuras

1. **Testing**:
   - Unit tests para servicios con Mockito.
   - Integration tests con `@SpringBootTest`.
   - E2E tests con Selenium.

2. **Caching**:
   - `@Cacheable` para productos más consultados.
   - Redis para cache distribuido.

3. **Búsqueda Avanzada**:
   - Elasticsearch para búsqueda full-text.
   - Filtros facetados (por rango de precio, calificación).

4. **Optimización de Queries**:
   - `@EntityGraph` para evitar N+1.
   - Proyecciones para queries específicas.

5. **Gestión de Imágenes**:
   - Upload a AWS S3 o Cloudinary.
   - Redimensionamiento automático.

6. **API REST Completa**:
   - Exponer todos los endpoints como REST para apps móviles.
   - Documentación con Swagger/OpenAPI.

7. **Event-Driven Architecture**:
   - Publicar eventos al crear/actualizar productos.
   - Microservicios escuchan eventos (ej: actualizar cache, enviar notificaciones).

### 12.4 Lecciones Aprendidas

1. **Validación en múltiples capas**: Frontend (HTML5), DTO (Bean Validation), Servicio (lógica de negocio).
2. **DTOs son esenciales**: Nunca exponer entidades directamente.
3. **Transacciones**: Siempre usar `@Transactional` en servicios.
4. **Logging**: Fundamental para debugging en producción.
5. **Patrones de diseño**: No reinventar la rueda, usar soluciones probadas.

---

## 📚 RESUMEN EJECUTIVO

### Arquitectura
- **Patrón**: MVC extendido con capas de servicio y persistencia.
- **Tecnologías**: Spring Boot, Spring Data JPA, Hibernate, MySQL, Thymeleaf, Tailwind CSS.

### Componentes Clave
1. **Modelo**: Entidad `Producto` con auditoría automática.
2. **Repositorio**: `ProductoRepository` con 16 métodos de consulta.
3. **DTOs**: 3 DTOs (`ProductoDTO`, `CrearProductoDTO`, `ActualizarProductoDTO`) con validaciones.
4. **Servicio**: `ProductoServiceImpl` con 16 métodos de lógica de negocio.
5. **Controlador**: `AdminWebController` con 8 endpoints (CRUD + duplicar).
6. **Vistas**: 2 templates Thymeleaf (lista y formulario).

### Patrones Aplicados
- Repository, Service Layer, DTO, Builder, Dependency Injection, Template Method, Strategy.

### Funcionalidades
- ✅ CRUD completo con validaciones.
- ✅ Estados: BORRADOR, PUBLICADO, ARCHIVADO.
- ✅ Generación automática de slug y SKU.
- ✅ Duplicación de productos.
- ✅ Filtros dinámicos (categoría, estado, precio).
- ✅ Paginación y ordenamiento.
- ✅ Mensajes flash (éxito/error).
- ✅ Seguridad con Spring Security.

### Métricas
- **Líneas de código**: ~2,500 líneas (Java) + 800 líneas (HTML).
- **Tiempo de desarrollo**: 3 semanas (con documentación).
- **Cobertura de tests**: Pendiente (objetivo: 80%).

---

**Autor**: Equipo Kiwisha  
**Fecha**: 28 de Octubre 2025  
**Versión Módulo**: 1.7.1  
**Estado**: ✅ Producción
