# DOCUMENTACIÓN MÓDULO GESTIÓN DE PRODUCTOS - PARTE 2

## 4. CAPA DE TRANSFERENCIA DE DATOS (DTOs)

### 4.1 ¿Por qué usar DTOs?

Los **Data Transfer Objects (DTOs)** son objetos especializados que transportan datos entre las capas de la aplicación. NO son entidades JPA y NO se persisten directamente en la base de datos.

**Problemas que resuelven los DTOs**:

1. **Separación de Contrato API y Modelo de Dominio**:
   - La entidad `Producto` puede cambiar internamente sin afectar la API pública.
   - Ejemplo: Agregamos un campo `costoProveedor` a la entidad, pero NO queremos exponerlo en la API.

2. **Seguridad**:
   - Evita exponer información sensible (ej: costos, márgenes).
   - Control total sobre qué datos se envían al frontend.

3. **Validación Contextual**:
   - Diferentes validaciones para crear vs actualizar.
   - Ejemplo: Al crear, el `sku` se genera automáticamente. Al actualizar, puede modificarse.

4. **Performance**:
   - Evita cargar relaciones innecesarias (ej: no necesitamos las 50 reviews al listar productos).

5. **Desacoplamiento**:
   - El frontend no depende de la estructura interna de la BD.

### 4.2 ProductoDTO (DTO de Consulta)

**Ubicación**: `src/main/java/com/kiwisha/project/dto/ProductoDTO.java`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Integer productoId;
    private String titulo;
    private String slug;
    private String sku;
    private String resumen;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioAnterior;
    private Integer cantidad;
    private EstadoProducto estado;
    private Boolean publicado;
    private Boolean destacado;
    private Boolean nuevo;
    private Boolean enOferta;
    private String imagen;
    
    // Relaciones simplificadas
    private Integer categoriaId;
    private String categoriaTitulo;
    
    // Metadatos
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Campos calculados
    private Double promedioCalificacion;
    private Integer totalReviews;
}
```

**Justificación del Diseño**:

- **@Data (Lombok)**: Genera automáticamente getters, setters, `toString()`, `equals()`, `hashCode()`.
- **@Builder**: Facilita la creación de objetos con patrón builder: `ProductoDTO.builder().titulo("X").precio(10.0).build()`.
- **Campos calculados**: `promedioCalificacion` y `totalReviews` no existen en la entidad, se calculan en el servicio.
- **Relaciones aplanadas**: En vez de exponer todo el objeto `Categoria`, solo exponemos `categoriaId` y `categoriaTitulo`.
  - **Beneficio**: Reduce el tamaño del JSON y evita referencias circulares.

### 4.3 CrearProductoDTO (DTO de Entrada - Creación)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearProductoDTO {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    private String titulo;
    
    @Size(max = 500, message = "El resumen no puede exceder los 500 caracteres")
    private String resumen;
    
    @Size(max = 5000, message = "La descripción no puede exceder los 5000 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @DecimalMin(value = "0.0", message = "El precio anterior no puede ser negativo")
    private BigDecimal precioAnterior;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    
    @NotNull(message = "El estado es obligatorio")
    private EstadoProducto estado = EstadoProducto.BORRADOR;
    
    private Boolean publicado = false;
    private Boolean destacado = false;
    private Boolean nuevo = false;
    private Boolean enOferta = false;
    
    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    private String imagen;
    
    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;
}
```

**Validaciones con Jakarta Bean Validation**:

1. **@NotBlank vs @NotNull**:
   - `@NotBlank`: Para Strings, valida que no sea null, vacío ("") ni solo espacios ("   ").
   - `@NotNull`: Para cualquier objeto, solo valida que no sea null.

2. **@Size(max = X)**:
   - Limita la longitud de Strings.
   - **Justificación**: Previene ataques de DoS con campos excesivamente largos y mantiene consistencia con la BD.

3. **@DecimalMin y @Min**:
   - Validaciones numéricas para evitar valores negativos o cero donde no corresponde.
   - Ejemplo: Precio debe ser > 0, pero cantidad puede ser 0 (agotado).

4. **Valores por Defecto**:
   ```java
   private EstadoProducto estado = EstadoProducto.BORRADOR;
   private Boolean publicado = false;
   ```
   - Si el frontend no envía estos campos, se asignan valores seguros por defecto.
   - **Justificación**: Un producto nuevo debe estar en borrador hasta que se revise.

### 4.4 ActualizarProductoDTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarProductoDTO {
    
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    private String titulo;
    
    @Size(max = 20, message = "El SKU no puede exceder los 20 caracteres")
    private String sku;
    
    @Size(max = 500, message = "El resumen no puede exceder los 500 caracteres")
    private String resumen;
    
    @Size(max = 5000, message = "La descripción no puede exceder los 5000 caracteres")
    private String descripcion;
    
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @DecimalMin(value = "0.0", message = "El precio anterior no puede ser negativo")
    private BigDecimal precioAnterior;
    
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    
    private EstadoProducto estado;
    
    private Boolean publicado;
    private Boolean destacado;
    private Boolean nuevo;
    private Boolean enOferta;
    
    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    private String imagen;
    
    private Integer categoriaId;
}
```

**Diferencias clave con CrearProductoDTO**:

1. **Campos Opcionales (sin @NotNull)**:
   - Al actualizar, solo se envían los campos que cambian (PATCH semántico).
   - El servicio valida que al menos un campo esté presente.

2. **SKU Editable**:
   - En creación, el SKU se genera automáticamente.
   - En actualización, puede modificarse manualmente si es necesario.

3. **Flexibilidad**:
   - Permite actualizaciones parciales sin sobrescribir campos no enviados.

### 4.5 Mapeo DTO ↔ Entidad con ModelMapper

En el servicio, usamos **ModelMapper** para convertir entre entidades y DTOs:

```java
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;
    
    @Override
    public ProductoDTO crearProducto(CrearProductoDTO crearDTO) {
        // DTO → Entidad
        Producto producto = modelMapper.map(crearDTO, Producto.class);
        
        // Lógica de negocio...
        producto.setSlug(generarSlugUnico(crearDTO.getTitulo()));
        producto.setSku(generarSkuUnico());
        
        // Guardar
        Producto guardado = productoRepository.save(producto);
        
        // Entidad → DTO
        return convertirADTO(guardado);
    }
}
```

**¿Por qué ModelMapper?**

- **Automatización**: Mapea automáticamente campos con el mismo nombre.
- **Menos Código**: Sin ModelMapper, tendríamos que escribir:
  ```java
  producto.setTitulo(crearDTO.getTitulo());
  producto.setPrecio(crearDTO.getPrecio());
  // ... 15 líneas más
  ```
- **Configuración**:
  ```java
  @Configuration
  public class JpaConfig {
      @Bean
      public ModelMapper modelMapper() {
          ModelMapper mapper = new ModelMapper();
          mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
          return mapper;
      }
  }
  ```

---

## 5. CAPA DE LÓGICA DE NEGOCIO (SERVICIOS)

### 5.1 Patrón Service Layer

El **Service Layer** es donde reside la **lógica de negocio** de la aplicación. Los servicios orquestan operaciones complejas que involucran múltiples repositorios, validaciones, cálculos, etc.

**Principios aplicados**:

1. **Single Responsibility Principle (SRP)**:
   - Cada servicio tiene una responsabilidad específica.
   - `ProductoService` solo maneja operaciones de productos.

2. **Interface Segregation**:
   - Definimos una interfaz (`ProductoService`) y una implementación (`ProductoServiceImpl`).
   - **Beneficio**: Facilita testing con mocks y permite múltiples implementaciones.

3. **Dependency Injection**:
   - Spring inyecta las dependencias automáticamente.
   - **Anotación**: `@RequiredArgsConstructor` (Lombok) genera un constructor con todas las dependencias `final`.

### 5.2 ProductoService (Interfaz)

```java
public interface ProductoService {
    
    // CRUD Básico
    ProductoDTO crearProducto(CrearProductoDTO crearProductoDTO);
    ProductoDTO actualizarProducto(Integer id, ActualizarProductoDTO actualizarProductoDTO);
    void eliminarProducto(Integer id);
    ProductoDTO obtenerProductoPorId(Integer id);
    
    // Consultas
    List<ProductoDTO> obtenerTodosLosProductos();
    Page<ProductoDTO> obtenerProductosConPaginacion(int page, int size, String sortBy);
    
    // Búsqueda y filtros
    ProductoDTO obtenerProductoPorSlug(String slug);
    List<ProductoDTO> buscarProductosPorTitulo(String titulo);
    List<ProductoDTO> filtrarPorPrecio(BigDecimal min, BigDecimal max);
    Page<ProductoDTO> filtrarPorEstadoYCategoria(EstadoProducto estado, Integer categoriaId, 
                                                   int page, int size);
    
    // Operaciones especiales
    ProductoDTO duplicarProducto(Integer id);
    ProductoDTO actualizarStock(Integer id, Integer cantidad);
    List<ProductoDTO> obtenerProductosConStockBajo(Integer stockMinimo);
    
    // Generadores
    String generarSlugUnico(String titulo);
    String generarSkuUnico();
}
```

**Justificación de Métodos**:

- **CRUD**: Operaciones estándar que todo módulo debe tener.
- **Paginación**: Evita cargar miles de productos en memoria.
- **Búsqueda por Slug**: URLs SEO-friendly.
- **Duplicar**: Acelera la creación de productos similares (ej: variantes de tamaño/color).
- **Stock bajo**: Dashboard muestra alertas de inventario crítico.

### 5.3 ProductoServiceImpl (Implementación Detallada)

#### 5.3.1 Crear Producto

```java
@Override
@Transactional
public ProductoDTO crearProducto(CrearProductoDTO crearProductoDTO) {
    log.debug("Creando nuevo producto: {}", crearProductoDTO.getTitulo());
    
    // 1. Validar categoría
    Categoria categoria = categoriaRepository.findById(crearProductoDTO.getCategoriaId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Categoría no encontrada con ID: " + crearProductoDTO.getCategoriaId()));
    
    // 2. Mapear DTO → Entidad
    Producto producto = modelMapper.map(crearProductoDTO, Producto.class);
    
    // 3. Generar slug único (SEO)
    producto.setSlug(generarSlugUnico(crearProductoDTO.getTitulo()));
    
    // 4. Generar SKU único
    if (producto.getSku() == null || producto.getSku().isEmpty()) {
        producto.setSku(generarSkuUnico());
    }
    
    // 5. Asignar categoría
    producto.setCategoria(categoria);
    
    // 6. Aplicar estado y publicación según el botón presionado
    if (crearProductoDTO.getEstado() == EstadoProducto.PUBLICADO) {
        producto.setPublicado(true);
    } else {
        producto.setPublicado(false);
    }
    
    // 7. Guardar en BD
    Producto guardado = productoRepository.save(producto);
    log.info("Producto creado exitosamente con ID: {} y SKU: {}", 
             guardado.getProductoId(), guardado.getSku());
    
    // 8. Convertir a DTO y retornar
    return convertirADTO(guardado);
}
```

**Desglose paso a paso**:

1. **Logging**: `@Slf4j` (Lombok) proporciona un logger para debugging.
   - **Nivel DEBUG**: Solo se ve en desarrollo.
   - **Nivel INFO**: Se guarda en producción para auditoría.

2. **@Transactional**: Toda la operación es atómica.
   - Si algo falla (ej: slug duplicado), se hace **rollback** automáticamente.
   - **Beneficio**: Consistencia de datos garantizada.

3. **Validación de Categoría**:
   - Antes de guardar, verificamos que la categoría existe.
   - `orElseThrow()`: Lanza excepción personalizada si no se encuentra.

4. **Generación de Slug** (explicado en detalle más adelante):
   - Transforma "Kiwicha Orgánica 500g" → "kiwicha-organica-500g".
   - Verifica unicidad: si existe, agrega número: "kiwicha-organica-500g-2".

5. **Generación de SKU**:
   - Formato: `XXX-NNNN` (ej: KIW-0001).
   - Prefijo de 3 letras + guión + número secuencial de 4 dígitos.

6. **Lógica de Publicación**:
   - Si el usuario presiona "Publicar": `estado = PUBLICADO` y `publicado = true`.
   - Si presiona "Guardar Borrador": `estado = BORRADOR` y `publicado = false`.

#### 5.3.2 Generación de Slug Único (SEO)

```java
@Override
public String generarSlugUnico(String titulo) {
    // 1. Convertir a minúsculas
    String slug = titulo.toLowerCase();
    
    // 2. Reemplazar espacios por guiones
    slug = slug.replaceAll("\\s+", "-");
    
    // 3. Eliminar caracteres especiales (mantener solo letras, números, guiones)
    slug = slug.replaceAll("[^a-z0-9-]", "");
    
    // 4. Eliminar guiones duplicados
    slug = slug.replaceAll("-+", "-");
    
    // 5. Eliminar guiones al inicio y final
    slug = slug.replaceAll("^-|-$", "");
    
    // 6. Verificar unicidad
    String slugFinal = slug;
    int contador = 1;
    while (productoRepository.existsBySlug(slugFinal)) {
        slugFinal = slug + "-" + contador;
        contador++;
    }
    
    log.debug("Slug generado: {} para título: {}", slugFinal, titulo);
    return slugFinal;
}
```

**¿Por qué es importante?**:

- **SEO**: Los slugs descriptivos mejoran el ranking en Google.
  - ❌ Mal: `/producto/12345`
  - ✅ Bien: `/producto/kiwicha-organica-500g`

- **Unicidad**: Dos productos pueden tener títulos similares:
  - "Kiwicha Orgánica 500g" → `kiwicha-organica-500g`
  - "Kiwicha Orgánica 500g" (duplicado) → `kiwicha-organica-500g-2`

- **Normalización**: Elimina caracteres problemáticos para URLs.

#### 5.3.3 Generación de SKU Único

```java
@Override
public String generarSkuUnico() {
    // 1. Generar prefijo de 3 letras aleatorias
    String prefijo = generarPrefijoAleatorio(3);
    
    // 2. Buscar el último número usado con este prefijo
    Long ultimoNumero = productoRepository.findMaxSkuNumber(prefijo);
    
    // 3. Incrementar el número
    long nuevoNumero = (ultimoNumero != null) ? ultimoNumero + 1 : 1;
    
    // 4. Formatear con ceros a la izquierda (0001, 0002, ...)
    String sku = String.format("%s-%04d", prefijo, nuevoNumero);
    
    // 5. Verificar que no exista (aunque es muy improbable)
    while (productoRepository.existsBySku(sku)) {
        nuevoNumero++;
        sku = String.format("%s-%04d", prefijo, nuevoNumero);
    }
    
    log.debug("SKU generado: {}", sku);
    return sku;
}

private String generarPrefijoAleatorio(int longitud) {
    return new Random().ints(97, 123) // 'a' a 'z'
        .limit(longitud)
        .mapToObj(c -> String.valueOf((char) c))
        .collect(Collectors.joining())
        .toUpperCase();
}
```

**Justificación del Formato SKU**:

- **Prefijo alfabético**: Identifica visualmente la categoría o tipo.
  - Ej: KIW-xxxx para kiwicha, QUI-xxxx para quinua.
- **Número secuencial**: Garantiza unicidad y facilita inventario.
- **Formato fijo**: `%04d` siempre genera 4 dígitos (0001, 0099, 1000).
  - **Beneficio**: Ordenamiento consistente en reportes.

**Consulta asociada en el repositorio**:
```java
@Query("SELECT MAX(CAST(SUBSTRING(p.sku, LENGTH(:prefijo) + 2) AS INTEGER)) " +
       "FROM Producto p WHERE p.sku LIKE CONCAT(:prefijo, '-%')")
Long findMaxSkuNumber(@Param("prefijo") String prefijo);
```
- Busca todos los SKUs con el prefijo (ej: `KIW-%`).
- Extrae la parte numérica (0001 → 1).
- Retorna el máximo número.

#### 5.3.4 Actualizar Producto

```java
@Override
@Transactional
public ProductoDTO actualizarProducto(Integer id, ActualizarProductoDTO actualizarDTO) {
    log.debug("Actualizando producto ID: {}", id);
    
    // 1. Verificar que el producto existe
    Producto producto = productoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Producto no encontrado con ID: " + id));
    
    // 2. Actualizar solo los campos enviados (PATCH semántico)
    if (actualizarDTO.getTitulo() != null) {
        producto.setTitulo(actualizarDTO.getTitulo());
        // Regenerar slug si cambió el título
        producto.setSlug(generarSlugUnico(actualizarDTO.getTitulo()));
    }
    
    if (actualizarDTO.getSku() != null) {
        // Validar que el nuevo SKU no exista
        if (productoRepository.existsBySku(actualizarDTO.getSku()) && 
            !producto.getSku().equals(actualizarDTO.getSku())) {
            throw new DuplicateResourceException("El SKU ya existe: " + actualizarDTO.getSku());
        }
        producto.setSku(actualizarDTO.getSku());
    }
    
    if (actualizarDTO.getPrecio() != null) {
        producto.setPrecio(actualizarDTO.getPrecio());
    }
    
    if (actualizarDTO.getCantidad() != null) {
        producto.setCantidad(actualizarDTO.getCantidad());
    }
    
    if (actualizarDTO.getEstado() != null) {
        producto.setEstado(actualizarDTO.getEstado());
        // Sincronizar publicación con estado
        producto.setPublicado(actualizarDTO.getEstado() == EstadoProducto.PUBLICADO);
    }
    
    if (actualizarDTO.getCategoriaId() != null) {
        Categoria categoria = categoriaRepository.findById(actualizarDTO.getCategoriaId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Categoría no encontrada con ID: " + actualizarDTO.getCategoriaId()));
        producto.setCategoria(categoria);
    }
    
    // 3. Guardar cambios
    Producto actualizado = productoRepository.save(producto);
    log.info("Producto actualizado: ID {} - {}", actualizado.getProductoId(), actualizado.getTitulo());
    
    // 4. Retornar DTO
    return convertirADTO(actualizado);
}
```

**Características clave**:

1. **Actualización Parcial (PATCH)**:
   - Solo actualizamos los campos que vienen en el DTO.
   - **Ventaja**: El frontend puede enviar solo lo que cambió.

2. **Validaciones Condicionales**:
   - Si cambió el SKU, verificamos que no esté duplicado.
   - Si cambió el título, regeneramos el slug.

3. **Sincronización Estado-Publicación**:
   - Si `estado = PUBLICADO`, automáticamente `publicado = true`.
   - Esto evita inconsistencias.

#### 5.3.5 Duplicar Producto

```java
@Override
@Transactional
public ProductoDTO duplicarProducto(Integer id) {
    log.debug("Duplicando producto ID: {}", id);
    
    // 1. Obtener el producto original
    Producto original = productoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Producto no encontrado con ID: " + id));
    
    // 2. Crear una copia
    Producto copia = Producto.builder()
        .titulo(original.getTitulo() + " (Copia)")
        .resumen(original.getResumen())
        .descripcion(original.getDescripcion())
        .precio(original.getPrecio())
        .precioAnterior(original.getPrecioAnterior())
        .cantidad(0) // Stock inicial en 0 para la copia
        .estado(EstadoProducto.BORRADOR) // Siempre crear como borrador
        .publicado(false)
        .destacado(false)
        .nuevo(false)
        .enOferta(false)
        .imagen(original.getImagen())
        .categoria(original.getCategoria())
        .build();
    
    // 3. Generar nuevos identificadores únicos
    copia.setSlug(generarSlugUnico(copia.getTitulo()));
    copia.setSku(generarSkuUnico());
    
    // 4. Guardar la copia
    Producto guardado = productoRepository.save(copia);
    log.info("Producto duplicado: Original ID {} -> Copia ID {}", id, guardado.getProductoId());
    
    return convertirADTO(guardado);
}
```

**Decisiones de diseño**:

1. **Título con sufijo "(Copia)"**: Indica claramente que es una duplicación.
2. **Stock en 0**: Evita errores de inventario.
3. **Estado BORRADOR**: Obliga al administrador a revisar antes de publicar.
4. **Nuevos slug y SKU**: Garantiza unicidad.

---

*Fin de la Parte 2. Continúo con la Parte 3...*
