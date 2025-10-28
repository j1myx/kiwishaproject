# 📦 MÓDULO DE GESTIÓN DE PRODUCTOS - DOCUMENTACIÓN TÉCNICA

## Índice

1. [Arquitectura General](#1-arquitectura-general)
2. [Capa de Modelo (Entidades)](#2-capa-de-modelo-entidades)
3. [Capa de Persistencia (Repositorios)](#3-capa-de-persistencia-repositorios)
4. [Capa de Transferencia de Datos (DTOs)](#4-capa-de-transferencia-de-datos-dtos)
5. [Capa de Lógica de Negocio (Servicios)](#5-capa-de-lógica-de-negocio-servicios)
6. [Capa de Presentación (Controladores)](#6-capa-de-presentación-controladores)
7. [Vistas (Templates Thymeleaf)](#7-vistas-templates-thymeleaf)
8. [Configuración y Conexión a Base de Datos](#8-configuración-y-conexión-a-base-de-datos)
9. [Patrones de Diseño Aplicados](#9-patrones-de-diseño-aplicados)
10. [Tecnologías Utilizadas](#10-tecnologías-utilizadas)
11. [Flujo de Datos Completo](#11-flujo-de-datos-completo)

---

## 1. ARQUITECTURA GENERAL

### 1.1 Visión General del Módulo

El **Módulo de Gestión de Productos** es un sistema administrativo completo que permite a los administradores del e-commerce realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre el catálogo de productos. Este módulo sigue la arquitectura **MVC (Model-View-Controller)** extendida con **capas adicionales de servicio y persistencia** para lograr una separación clara de responsabilidades.

### 1.2 Arquitectura en Capas

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                      │
│  ┌──────────────────┐         ┌─────────────────────────┐  │
│  │  Templates HTML  │ ←────── │  AdminWebController     │  │
│  │  (Thymeleaf)     │         │  (Controlador Web)       │  │
│  └──────────────────┘         └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  CAPA DE LÓGICA DE NEGOCIO                   │
│  ┌──────────────────┐         ┌─────────────────────────┐  │
│  │  ProductoService │ ←────── │ ProductoServiceImpl     │  │
│  │  (Interfaz)      │         │ (Implementación)        │  │
│  └──────────────────┘         └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│              CAPA DE TRANSFERENCIA DE DATOS                  │
│  ┌──────────────────┐  ┌─────────────────────────────────┐ │
│  │  ProductoDTO     │  │  CrearProductoDTO               │ │
│  │  (Consulta)      │  │  ActualizarProductoDTO          │ │
│  └──────────────────┘  └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PERSISTENCIA                      │
│  ┌──────────────────┐         ┌─────────────────────────┐  │
│  │ ProductoRepository│ ←────→ │  Spring Data JPA        │  │
│  │  (Interface)      │         │  (Abstracción)          │  │
│  └──────────────────┘         └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      CAPA DE MODELO                          │
│  ┌──────────────────┐         ┌─────────────────────────┐  │
│  │  Producto        │ ←────→ │  JPA/Hibernate          │  │
│  │  (Entidad JPA)   │         │  (ORM)                  │  │
│  └──────────────────┘         └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    BASE DE DATOS MySQL                       │
│              (FreeSQLDatabase - Hosting Online)              │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 Justificación de la Arquitectura en Capas

**¿Por qué esta arquitectura?**

1. **Separación de Responsabilidades (SoC)**: Cada capa tiene una responsabilidad específica y bien definida. Esto facilita el mantenimiento, las pruebas y la escalabilidad del código.

2. **Bajo Acoplamiento**: Las capas se comunican a través de interfaces bien definidas (contratos), lo que permite cambiar la implementación de una capa sin afectar a las demás.

3. **Alta Cohesión**: Los componentes dentro de cada capa están estrechamente relacionados y trabajan juntos para cumplir una función específica.

4. **Testabilidad**: Al tener capas independientes, podemos escribir pruebas unitarias para cada capa sin necesidad de inicializar todo el contexto de la aplicación.

5. **Reusabilidad**: Los servicios y repositorios pueden ser reutilizados por diferentes controladores (API REST, Web Controllers, etc.).

---

## 2. CAPA DE MODELO (ENTIDADES)

### 2.1 Entidad Principal: Producto.java

**Ubicación**: `src/main/java/com/kiwisha/project/model/Producto.java`

La entidad `Producto` es el **núcleo del módulo**. Representa el modelo de dominio que mapea directamente a la tabla `productos` en la base de datos mediante **JPA (Java Persistence API)**.

#### 2.1.1 Código de la Entidad

```java
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Integer productoId;
    
    @NotBlank(message = "El título del producto es obligatorio")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String titulo;
    
    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 120, message = "El slug no puede exceder los 120 caracteres")
    @Column(nullable = false, unique = true, length = 120)
    private String slug;
    
    @Column(unique = true, length = 20)
    private String sku;
    
    @Column(columnDefinition = "TEXT")
    private String resumen;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @DecimalMin(value = "0.0", message = "El precio anterior debe ser mayor o igual a 0")
    @Column(name = "precio_anterior", precision = 10, scale = 2)
    private BigDecimal precioAnterior;
    
    @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
    @Column(nullable = false)
    private Integer cantidad = 0;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estado = EstadoProducto.BORRADOR;
    
    @Column(nullable = false)
    private Boolean publicado = false;
    
    @Column(nullable = false)
    private Boolean destacado = false;
    
    @Column(nullable = false)
    private Boolean nuevo = false;
    
    @Column(name = "en_oferta", nullable = false)
    private Boolean enOferta = false;
    
    @Column(length = 255)
    private String imagen;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoImagen> imagenes = new ArrayList<>();
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    
    // Métodos auxiliares para gestión bidireccional
    public void agregarImagen(ProductoImagen imagen) {
        imagenes.add(imagen);
        imagen.setProducto(this);
    }
    
    public void removerImagen(ProductoImagen imagen) {
        imagenes.remove(imagen);
        imagen.setProducto(null);
    }
}
```

#### 2.1.2 Justificación de Anotaciones JPA

**@Entity y @Table**
- **@Entity**: Marca la clase como una entidad JPA que será gestionada por el contexto de persistencia de Hibernate.
- **@Table(name = "productos")**: Define el nombre exacto de la tabla en la base de datos, siguiendo la convención snake_case.
- **Justificación**: Permite a Spring Data JPA y Hibernate gestionar automáticamente el mapeo objeto-relacional (ORM), eliminando la necesidad de escribir SQL manualmente para operaciones CRUD básicas.

**@Id y @GeneratedValue**
- **@Id**: Define la clave primaria de la entidad.
- **@GeneratedValue(strategy = GenerationType.IDENTITY)**: Delega la generación del ID a la base de datos mediante AUTO_INCREMENT.
- **Justificación**: MySQL gestiona eficientemente la generación de IDs únicos y secuenciales, evitando colisiones y mejorando el rendimiento.

**@Column con Validaciones**
```java
@NotBlank(message = "El título del producto es obligatorio")
@Size(max = 100, message = "El título no puede exceder los 100 caracteres")
@Column(nullable = false, length = 100)
private String titulo;
```
- **@NotBlank**: Validación a nivel de aplicación que asegura que el campo no sea nulo ni vacío.
- **@Size**: Limita la longitud del campo.
- **@Column**: Define restricciones a nivel de base de datos (NOT NULL, longitud).
- **Justificación**: Doble capa de validación (aplicación + BD) para garantizar integridad de datos. Si la validación en la aplicación falla, nunca llega a la BD.

**@Enumerated(EnumType.STRING)**
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private EstadoProducto estado = EstadoProducto.BORRADOR;
```
- Almacena el valor del enum como String en la BD (BORRADOR, PUBLICADO, ARCHIVADO).
- **Justificación**: Los valores String son más legibles en la BD y no se rompen si se agregan nuevos valores al enum. Usar `ORDINAL` sería peligroso porque el orden puede cambiar.

**@ManyToOne y @OneToMany**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;

@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ProductoImagen> imagenes = new ArrayList<>();
```
- **@ManyToOne**: Un producto pertenece a una categoría.
- **FetchType.LAZY**: Carga perezosa, solo trae la categoría cuando se accede explícitamente.
- **@OneToMany**: Un producto puede tener múltiples imágenes.
- **CascadeType.ALL**: Las operaciones (guardar, eliminar) se propagan a las imágenes.
- **orphanRemoval = true**: Si se elimina una imagen de la lista, se elimina también de la BD.
- **Justificación**: Gestiona automáticamente las relaciones bidireccionales y evita queries N+1 con LAZY loading.

#### 2.1.3 Enum EstadoProducto

```java
public enum EstadoProducto {
    BORRADOR,    // Producto en creación, no visible al público
    PUBLICADO,   // Producto activo y visible en el catálogo
    ARCHIVADO    // Producto descontinuado, no se elimina por histórico
}
```

**Justificación del Diseño**:
- **BORRADOR**: Permite a los administradores trabajar en un producto sin publicarlo inmediatamente. Útil para revisiones o productos en proceso.
- **PUBLICADO**: Estado activo que indica que el producto es visible para los clientes.
- **ARCHIVADO**: Alternativa a la eliminación física. Preserva el producto para auditorías, reportes históricos o referencias en pedidos antiguos.

**Ventajas sobre eliminación física**:
1. Mantiene integridad referencial con pedidos históricos.
2. Permite análisis de tendencias de productos descontinuados.
3. Facilita la reactivación de productos sin perder datos.

### 2.2 Herencia: AuditableEntity

```java
@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity {
    
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;
    
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
    
    @Column(name = "creado_por", length = 50, updatable = false)
    private String creadoPor;
    
    @Column(name = "actualizado_por", length = 50)
    private String actualizadoPor;
    
    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        creadoPor = "system"; // En producción sería el usuario autenticado
        actualizadoPor = "system";
    }
    
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
        actualizadoPor = "system"; // En producción sería el usuario autenticado
    }
}
```

**Justificación de Auditoría Automática**:
- **@MappedSuperclass**: Indica que esta clase no es una entidad, sino que sus atributos se heredan en las entidades hijas.
- **@PrePersist**: Callback que se ejecuta automáticamente antes de insertar un registro.
- **@PreUpdate**: Callback que se ejecuta antes de actualizar un registro.
- **Beneficios**:
  1. **Trazabilidad**: Siempre sabemos cuándo y quién creó/modificó un producto.
  2. **DRY (Don't Repeat Yourself)**: Evita repetir estos campos en cada entidad.
  3. **Consistencia**: Todos los productos tienen los mismos metadatos de auditoría.
  4. **Cumplimiento**: Facilita auditorías y cumplimiento de normativas (GDPR, SOX).

---

## 3. CAPA DE PERSISTENCIA (REPOSITORIOS)

### 3.1 ProductoRepository: Patrón DAO con Spring Data JPA

**Ubicación**: `src/main/java/com/kiwisha/project/repository/ProductoRepository.java`

```java
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Consultas por slug (SEO-friendly URLs)
    Optional<Producto> findBySlug(String slug);
    boolean existsBySlug(String slug);
    
    // Consultas por SKU
    Optional<Producto> findBySku(String sku);
    boolean existsBySku(String sku);
    
    // Consultas para generación de SKU único
    @Query("SELECT MAX(CAST(SUBSTRING(p.sku, LENGTH(:prefijo) + 2) AS INTEGER)) " +
           "FROM Producto p WHERE p.sku LIKE CONCAT(:prefijo, '-%')")
    Long findMaxSkuNumber(@Param("prefijo") String prefijo);
    
    // Consultas por estado
    List<Producto> findByEstado(EstadoProducto estado);
    Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);
    Long countByEstado(EstadoProducto estado);
    
    // Consultas por publicación
    List<Producto> findByPublicadoTrue();
    Page<Producto> findByPublicadoTrue(Pageable pageable);
    
    // Consultas combinadas (estado + categoría)
    Page<Producto> findByEstadoAndCategoria_CategoriaId(
        EstadoProducto estado, Integer categoriaId, Pageable pageable
    );
    
    // Búsqueda por título
    @Query("SELECT p FROM Producto p WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))")
    List<Producto> findByTituloContainingIgnoreCase(@Param("titulo") String titulo);
    
    // Filtros de precio
    List<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
    
    // Stock bajo
    @Query("SELECT p FROM Producto p WHERE p.cantidad < :stockMinimo AND p.publicado = true")
    List<Producto> findProductosConStockBajo(@Param("stockMinimo") Integer stockMinimo);
    
    // Productos destacados, nuevos, en oferta
    List<Producto> findByDestacadoTrueAndPublicadoTrue();
    List<Producto> findByNuevoTrueAndPublicadoTrue();
    List<Producto> findByEnOfertaTrueAndPublicadoTrue();
}
```

### 3.2 Patrón Repository (DAO)

**¿Qué es el Patrón Repository?**

El patrón Repository actúa como una **capa de abstracción** entre la lógica de negocio y el acceso a datos. En Spring Data JPA, este patrón se implementa mediante interfaces que extienden `JpaRepository`.

**Justificación del Uso de Spring Data JPA**:

1. **Eliminación de Código Boilerplate**:
   - Spring Data JPA genera automáticamente la implementación de métodos CRUD estándar: `save()`, `findById()`, `findAll()`, `delete()`, etc.
   - **Beneficio**: No necesitamos escribir DAOs manualmente con EntityManager.

2. **Query Methods (Métodos de Consulta Derivados)**:
   ```java
   Optional<Producto> findBySlug(String slug);
   ```
   - Spring interpreta el nombre del método y genera automáticamente la consulta SQL.
   - **Traducción**: `SELECT * FROM productos WHERE slug = ?`
   - **Beneficio**: Código más legible y menos propenso a errores de sintaxis SQL.

3. **@Query para Consultas Complejas**:
   ```java
   @Query("SELECT MAX(CAST(SUBSTRING(p.sku, LENGTH(:prefijo) + 2) AS INTEGER)) " +
          "FROM Producto p WHERE p.sku LIKE CONCAT(:prefijo, '-%')")
   Long findMaxSkuNumber(@Param("prefijo") String prefijo);
   ```
   - Permite escribir JPQL (Java Persistence Query Language) o SQL nativo para consultas complejas.
   - **Caso de uso**: Generación de SKU único (ej: KIW-0001, KIW-0002).
   - **Funcionamiento**:
     1. Busca todos los SKUs que empiecen con el prefijo (ej: "KIW-").
     2. Extrae el número (0001, 0002).
     3. Devuelve el máximo número encontrado.
     4. El servicio incrementa +1 para generar el siguiente SKU.

4. **Paginación y Ordenamiento Integrado**:
   ```java
   Page<Producto> findByEstado(EstadoProducto estado, Pageable pageable);
   ```
   - `Pageable`: Interfaz de Spring que encapsula paginación (`page`, `size`) y ordenamiento (`sort`).
   - **Beneficio**: Manejo eficiente de grandes volúmenes de datos sin cargar todo en memoria.

5. **Type-Safe Queries**:
   - Las consultas son verificadas en tiempo de compilación.
   - **Ventaja**: Errores de sintaxis se detectan antes de ejecutar la aplicación.

### 3.3 Consultas Específicas del Módulo

#### 3.3.1 Búsqueda por Slug (SEO-Friendly)
```java
Optional<Producto> findBySlug(String slug);
```
- **Propósito**: Los slugs son URLs amigables (ej: `/producto/kiwicha-organica-500g`).
- **Ventaja SEO**: Google indexa mejor URLs descriptivas que IDs numéricos.
- **Optional**: Evita NullPointerException, obliga a manejar el caso de "no encontrado".

#### 3.3.2 Stock Bajo (Alerta de Inventario)
```java
@Query("SELECT p FROM Producto p WHERE p.cantidad < :stockMinimo AND p.publicado = true")
List<Producto> findProductosConStockBajo(@Param("stockMinimo") Integer stockMinimo);
```
- **Propósito**: Dashboard del administrador muestra alertas de productos con stock crítico.
- **Lógica**: Solo alerta productos publicados (no tiene sentido alertar borradores).

#### 3.3.3 Filtros Combinados
```java
Page<Producto> findByEstadoAndCategoria_CategoriaId(
    EstadoProducto estado, Integer categoriaId, Pageable pageable
);
```
- **Sintaxis Spring Data JPA**: `_` navega relaciones (`categoria.categoriaId`).
- **Propósito**: Filtrar productos por estado y categoría simultáneamente.
- **Uso en UI**: Dropdown de categorías + filtro de estado en lista administrativa.

---

*Este es el final de la Parte 1. Continúo con la Parte 2 a continuación...*
