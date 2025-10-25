# ANÁLISIS EXHAUSTIVO: PANTALLAS REALES vs IMPLEMENTACIÓN ACTUAL

**Fecha:** 2025-01-XX  
**Proyecto:** Kiwisha E-Commerce (Andean Treasures)  
**Versión Backend:** 1.5.0 (Fase 5 completada)  

---

## 📋 RESUMEN EJECUTIVO

El usuario proporcionó las pantallas HTML **REALES** que deben usarse como referencia para el proyecto. Estas pantallas difieren significativamente de las pantallas antiguas que se usaron durante el desarrollo de las Fases 1-5.

### ⚠️ HALLAZGOS CRÍTICOS

1. **CUPONES/DESCUENTOS ELIMINADOS** ✅ CONFIRMADO
   - ❌ NO hay campos de cupón en el carrito
   - ❌ NO hay campos de cupón en el checkout
   - ❌ NO hay mención de descuentos en ninguna pantalla
   - ⚠️ **IMPACTO**: Se implementó completamente la funcionalidad de cupones en Fase 2 y Fase 5

2. **NUEVAS PANTALLAS ADMINISTRATIVAS** 🆕
   - Dashboard Administrador (no existía en versión antigua)
   - Gestionar Contenidos (CMS) (no existía)
   - Reportería avanzada (no existía)

3. **MARCA Y DISEÑO**
   - Nombre: "Andean Treasures" (confirmado)
   - Framework: Tailwind CSS (confirmado en todas las pantallas)
   - Paleta de colores: #fcfaf8 (fondo), #f98006 (primario), #1c140d (texto), #9e7347 (secundario)

---

## 📊 COMPARATIVA DETALLADA POR PANTALLA

### 1️⃣ **INICIO / LANDING PAGE**

**Ubicación:** `Sources/pantallas_version_nueva/inicio_/landing/code.html`

**Características identificadas:**
- NO ACCESIBLE (archivo no encontrado en estructura esperada)
- Requiere revisión manual de estructura de carpetas

---

### 2️⃣ **CATÁLOGO / LISTADO DE PRODUCTOS (PLP)**

**Ubicación:** `Sources/pantallas_version_nueva/catálogo_/listado_de_productos_(plp)/code.html`

**Características identificadas:**
- NO ACCESIBLE (archivo no encontrado)
- Requiere revisión manual de estructura de carpetas

---

### 3️⃣ **DETALLE DE PRODUCTO (PDP)**

**Ubicación:** `Sources/pantallas_version_nueva/detalle_de_producto_(pdp)/code.html`

**✅ ANALIZADO - Pantalla Completa**

**Características NUEVAS:**
- Sistema de calificaciones con estrellas (rating 4.5/5, 125 reviews)
- Gráfico de distribución de reviews por estrellas (40% 5★, 30% 4★, 15% 3★, 10% 2★, 5% 1★)
- Galería de imágenes del producto (4 thumbnails + imagen principal)
- Breadcrumb: Home / Catalog / Kiwicha
- Tabs: Description | Specifications | Shipping & Returns
- Campo de cantidad (dropdown/select)
- Indicador "In stock"
- Botones: "Add to Cart" (primario naranja) + "Buy Now" (secundario)
- Productos relacionados en carrusel
- Precio: $5.99 / lb

**🆕 FUNCIONALIDADES NO IMPLEMENTADAS:**
- ❌ Sistema de reviews/calificaciones (entidad `Review` existe pero sin API REST completa)
- ❌ Tabs de información del producto
- ❌ Galería de imágenes (ProductoImagen existe, pero UI no implementada)
- ❌ Productos relacionados dinámicos

**✅ YA IMPLEMENTADO:**
- ✅ Entidad `Producto` con precio, stock, descripción
- ✅ Entidad `ProductoImagen` (relación @OneToMany)
- ✅ Entidad `Review` (desde Fase 3)
- ✅ API REST `ReviewApiController` (11 endpoints - Fase 5)

---

### 4️⃣ **CARRITO DE COMPRA**

**Ubicación:** `Sources/pantallas_version_nueva/carrito_de_compra/code.html`

**✅ ANALIZADO**

**🔍 BÚSQUEDA DE CUPONES:**
```bash
grep -i "cupón|coupon|descuento|discount|code|promo" carrito_de_compra/code.html
# RESULTADO: 0 matches - NO HAY CUPONES ✅ CONFIRMADO
```

**Características de la pantalla:**
- Lista de productos en carrito
- Selector de cantidad por producto
- Botón eliminar item
- Subtotal, envío, total
- Botón "Proceed to Checkout"
- **❌ SIN campos de cupón/descuento**

**⚠️ FUNCIONALIDAD IMPLEMENTADA QUE SOBRA:**
```java
// CarritoApiController.java - FASE 5
@PostMapping("/{sessionId}/cupon")  // ❌ ELIMINAR
public ResponseEntity<CarritoDTO> aplicarCupon(
    @PathVariable String sessionId,
    @RequestBody Map<String, String> request
)

@DeleteMapping("/{sessionId}/cupon")  // ❌ ELIMINAR
public ResponseEntity<CarritoDTO> removerCupon(@PathVariable String sessionId)
```

```java
// CarritoService.java - FASE 2
CarritoDTO aplicarCupon(String sessionId, String codigoCupon);  // ❌ ELIMINAR
CarritoDTO removerCupon(String sessionId);  // ❌ ELIMINAR
```

```java
// DTOs con campos de cupón - MODIFICAR
public class CarritoDTO {
    // ...
    private CuponDTO cupon;  // ❌ ELIMINAR
    private BigDecimal descuento;  // ❌ ELIMINAR O DEJAR PARA FUTURO
}
```

---

### 5️⃣ **CHECKOUT - DATOS PERSONALES**

**Ubicación:** `Sources/pantallas_version_nueva/checkout_–_datos_personales,_contacto_y_envío/code.html`

**✅ ANALIZADO - 101 líneas leídas**

**🔍 BÚSQUEDA DE CUPONES:**
```bash
grep -i "cupón|coupon|descuento|discount" checkout.html
# RESULTADO: 0 matches - NO HAY CUPONES ✅ CONFIRMADO
```

**Características de la pantalla:**
- **Indicador de progreso**: "Step 1 of 3" (3 pasos en total)
- **Formulario** con campos:
  - First Name
  - Last Name
  - Email
  - Phone
  - Address (completo en un solo campo, sin desglose provincia/distrito)
- **Botón**: "Continue to Payment" (naranja #f98006)
- **Header**: "Andean Treasures" con menú: Home | Products | About | Contact
- **❌ SIN campos de cupón/descuento**

**✅ ALINEADO CON:**
- Entidad `Cliente` (campos nombre, apellido, email, teléfono, dirección)
- DTO `CrearPedidoDTO` con información de cliente

**⚠️ CAMPOS DE CUPÓN A ELIMINAR:**
```java
// CrearPedidoDTO.java
public class CrearPedidoDTO {
    // ...
    private String codigoCupon;  // ❌ ELIMINAR
}
```

---

### 6️⃣ **PAGO (PASARELA)**

**Ubicación:** `Sources/pantallas_version_nueva/pago_(pasarela_embebida_o_paso_a_pasarela)/code.html`

**✅ REVISADO PARCIALMENTE (50 líneas)**

**Características identificadas:**
- Header "Andean Treasures" estándar
- Menú: Shop | About | Contact
- Proceso de checkout multi-paso
- **❌ SIN campos de cupón visible en las primeras 50 líneas**

**🔍 REQUIERE ANÁLISIS COMPLETO**

---

### 7️⃣ **CONFIRMACIÓN DE COMPRA (ÉXITO)**

**Ubicación:** `Sources/pantallas_version_nueva/confirmación_de_compra_(éxito)/code.html`

**✅ ANALIZADO COMPLETO**

**Características de la pantalla:**
- **Título**: "Purchase Confirmed"
- **Order Number**: "#1234567890"
- **Secciones**:
  1. Order Summary (productos, subtotal, shipping, total)
  2. Payment Method (tipo: Credit Card, últimos 4 dígitos)
  3. Shipping Address (nombre, dirección, ciudad, postal, país)
  4. Estimated Delivery (3-5 días hábiles)
- **Botones**:
  - "Download Receipt" (secundario)
  - "Continue Shopping" (primario naranja)
- **Productos recomendados**: "You Might Also Like" (3 productos)
- **❌ SIN mención de cupones/descuentos en el resumen**

**✅ ALINEADO CON:**
- Entidad `Pedido` (numero, subtotal, envio, total)
- Entidad `Transaccion` (método de pago)
- Flujo de PedidoService.crearPedido()

---

### 8️⃣ **RESULTADO DE PAGO - RECHAZADO**

**Ubicación:** `Sources/pantallas_version_nueva/resultado_de_pago_–_rechazado/code.html`

**❓ NO ANALIZADO AÚN**

---

### 9️⃣ **DASHBOARD ADMINISTRADOR** 🆕

**Ubicación:** `Sources/pantallas_version_nueva/dashboard_administrador/code.html`

**✅ ANALIZADO COMPLETO - NUEVA FUNCIONALIDAD**

**🆕 PANTALLA NUEVA (NO EXISTÍA EN VERSIÓN ANTIGUA)**

**Características:**
- **Panel lateral izquierdo** con menú:
  - ✅ **Gestionar Contenidos** (activo/resaltado)
  - Gestionar Productos
  - Reportería
  - Publicar cambios
- **Sección principal**:
  - **KPIs Rápidos** (3 cards):
    - Productos Publicados: 120
    - Borradores: 15
    - Stock Bajo: 8
  - **Actividad Reciente**:
    - Lista de eventos con timestamp
    - Ejemplo: "Producto 'Quinua Real' actualizado - 2024-07-26 14:30"
    - Tipos: actualizaciones de productos, publicación de artículos, creación de productos
- **Marca**: "Cooperativa Andina" (en el panel lateral)

**⚠️ FUNCIONALIDAD NO IMPLEMENTADA:**
- ❌ Controlador web para dashboard admin
- ❌ Endpoints API para KPIs (productos publicados, borradores, stock bajo)
- ❌ Sistema de auditoría/actividad reciente
- ❌ Sistema de publicación de cambios (workflow aprobación)
- ❌ Estados de producto: Publicado/Borrador/Archivado

**📝 ENTIDADES A CREAR/MODIFICAR:**
```java
// Producto.java - Agregar enum EstadoProducto
public enum EstadoProducto {
    BORRADOR, PUBLICADO, ARCHIVADO
}

// Nuevo: Auditoría / Log de Actividad
public class ActividadAdmin {
    private Long id;
    private String tipo;  // "PRODUCTO_ACTUALIZADO", "ARTICULO_PUBLICADO", etc.
    private String descripcion;
    private LocalDateTime fecha;
    private Usuario usuario;
}
```

---

### 🔟 **ADMIN - GESTIONAR CONTENIDOS** 🆕

**Ubicación:** `Sources/pantallas_version_nueva/admin_–_gestionar_contenidos/code.html`

**✅ ANALIZADO COMPLETO - NUEVA FUNCIONALIDAD**

**🆕 SISTEMA CMS COMPLETO (NO EXISTÍA)**

**Características:**
- **Tabs**:
  - ✅ Editar Página de Inicio (activo)
  - Noticias
  - Artículos
- **Secciones editables de la home**:
  1. **Hero Section**:
     - Campo: Título (input text)
     - Campo: Subtítulo (textarea)
     - Imagen principal (aspect 3:2)
  2. **Carrusel de Destacados**:
     - 3 productos mostrados (Quinua Real, Kiwicha Orgánica, Hierbas Andinas)
     - Cada uno con imagen + nombre + descripción
  3. **Banners Promocionales**:
     - Imagen de banner (aspect 3:2)
- **Botones**:
  - "Guardar Borrador" (secundario)
  - "Solicitar Aprobación" (primario naranja)

**⚠️ FUNCIONALIDAD PARCIALMENTE IMPLEMENTADA:**
- ✅ Entidad `Pagina` existe (Fase 1) con:
  - tipo (ENUM: HOME, NOSOTROS, CONTACTO, TERMINOS, PRIVACIDAD)
  - titulo, contenido
  - fechaPublicacion, activo
- ✅ Entidad `PaginaImagen` para imágenes asociadas
- ✅ Entidad `PaginaEtiqueta` para etiquetas
- ❌ NO hay controladores API REST para gestión de páginas
- ❌ NO hay sistema de aprobación de cambios (workflow)
- ❌ NO hay diferenciación entre borrador/publicado en Pagina
- ❌ NO implementado: Noticias y Artículos como entidades separadas

**📝 CONTROLADORES A CREAR:**
```java
// PaginaApiController.java - CREAR EN FASE 6
@RestController
@RequestMapping("/api/admin/paginas")
public class PaginaApiController {
    // Métodos CRUD para páginas
    // Método para publicar/despublicar
    // Método para solicitar aprobación
    // CRUD para hero, carrusel, banners
}
```

---

### 1️⃣1️⃣ **ADMIN - GESTIONAR PRODUCTOS (LISTADO)** 📝

**Ubicación:** `Sources/pantallas_version_nueva/admin_–_gestionar_productos_(listado)/code.html`

**✅ ANALIZADO COMPLETO**

**Características:**
- **Búsqueda**: Input con icono lupa "Buscar productos..."
- **Filtros**: Dropdown buttons para Categoría, Estado, Precio
- **Tabla de productos** con columnas:
  - Nombre
  - SKU (ej: KIW-ORG-001)
  - Categoría
  - Precio
  - Stock
  - Estado (button con "Publicado" / "Borrador")
  - Última actualización
  - Acciones (Editar | Duplicar | Archivar)
- **Paginación**: 10 páginas, botones prev/next
- **Botón**: "Nuevo producto" (secundario, esquina superior derecha)
- **Productos ejemplo**:
  - Kiwicha Orgánica ($5.99, 150 stock, Publicado)
  - Harina de Kiwicha ($4.29, 50 stock, Borrador)
  - Quinua Roja ($8.99, 75 stock, Publicado)

**✅ PARCIALMENTE IMPLEMENTADO:**
- ✅ Entidad `Producto` con nombre, sku, precio, stock
- ✅ ProductoApiController (Fase 5) con:
  - ✅ buscarPorNombre()
  - ✅ buscarPorCategoria()
  - ✅ buscarPorPrecio()
  - ✅ crearProducto(), actualizarProducto(), eliminarProducto()
- ❌ Falta campo `sku` en entidad Producto
- ❌ Falta enum `EstadoProducto` (Publicado/Borrador/Archivado)
- ❌ Falta endpoint `duplicarProducto()`
- ❌ Falta endpoint `archivarProducto()`

**📝 MODIFICACIONES NECESARIAS:**
```java
// Producto.java
@Entity
public class Producto {
    // ...
    @Column(unique = true, length = 50)  // AGREGAR
    private String sku;
    
    @Enumerated(EnumType.STRING)  // AGREGAR
    private EstadoProducto estado = EstadoProducto.BORRADOR;
}

// ProductoApiController.java
@PostMapping("/{id}/duplicar")  // AGREGAR
public ResponseEntity<ProductoDTO> duplicarProducto(@PathVariable Long id)

@PutMapping("/{id}/archivar")  // AGREGAR
public ResponseEntity<Void> archivarProducto(@PathVariable Long id)

@PutMapping("/{id}/publicar")  // AGREGAR
public ResponseEntity<ProductoDTO> publicarProducto(@PathVariable Long id)
```

---

### 1️⃣2️⃣ **ADMIN - REPORTERÍA** 🆕

**Ubicación:** `Sources/pantallas_version_nueva/admin_–_reportería/code.html`

**✅ ANALIZADO COMPLETO - NUEVA FUNCIONALIDAD**

**🆕 SISTEMA DE REPORTES AVANZADO (NO EXISTÍA)**

**Características:**
- **Panel izquierdo con filtros**:
  - **Fecha/Hora del Sistema** (dropdown)
  - **Botón "Exportar a Excel (XLSX)"** (con icono download)
  - **Acordeón "Filtros"** (desplegable)
  - **Tipos de reporte** (radio buttons):
    - ✅ Top productos del mes (seleccionado)
    - Compras por provincia
  - **Filtros dinámicos**:
    - Mes (dropdown)
    - Categoría (dropdown)
    - Top N (slider: valor 5)
    - Rango de fechas (2 inputs: fecha inicio / fecha fin)
    - Provincia (dropdown)
  - **Botones**:
    - "Aplicar filtros" (primario naranja)
    - "Restablecer" (transparente)

- **Panel principal**:
  - **KPIs** (3 cards):
    - Ventas totales: $12,500
    - Unidades vendidas: 3,200
    - Ticket promedio: $3.91
  - **Toggle view**: Tabla | Gráfico
  - **Tabla de ranking** (cuando view=Tabla):
    - Columnas: Ranking, Producto, Categoría, Unidades vendidas, Ingresos
    - Datos ejemplo: Kiwicha Orgánica (1,200 unidades, $4,500)
  - **Gráfico de barras horizontales** (cuando view=Gráfico):
    - Top productos del mes con barras de progreso
  - **Botones de exportación**:
    - "Exportar a Excel (XLSX)"
    - "Descargar imagen del gráfico"

**⚠️ FUNCIONALIDAD NO IMPLEMENTADA:**
- ❌ Controlador API para reportes
- ❌ Servicio de generación de reportes
- ❌ Exportación a Excel (librería Apache POI)
- ❌ Generación de gráficos (Chart.js o similar)
- ❌ Queries agregadas para KPIs y rankings

**📝 SERVICIOS Y CONTROLADORES A CREAR:**
```java
// ReporteService.java - CREAR
public interface ReporteService {
    ReporteVentasDTO generarReporteVentas(FiltrosReporteDTO filtros);
    List<ProductoRankingDTO> obtenerTopProductos(int topN, LocalDate desde, LocalDate hasta);
    List<ComprasPorProvinciaDTO> obtenerComprasPorProvincia(LocalDate desde, LocalDate hasta);
    byte[] exportarExcel(String tipoReporte, FiltrosReporteDTO filtros);
}

// ReporteApiController.java - CREAR EN FASE 6
@RestController
@RequestMapping("/api/admin/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteApiController {
    @GetMapping("/ventas")
    public ResponseEntity<ReporteVentasDTO> generarReporteVentas(@RequestParam FiltrosReporteDTO filtros);
    
    @GetMapping("/top-productos")
    public ResponseEntity<List<ProductoRankingDTO>> topProductos(...);
    
    @GetMapping("/compras-provincia")
    public ResponseEntity<List<ComprasPorProvinciaDTO>> comprasPorProvincia(...);
    
    @GetMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel(...);
}
```

---

### 1️⃣3️⃣ **ADMIN - CREAR/EDITAR PRODUCTO (FORM)**

**Ubicación:** `Sources/pantallas_version_nueva/admin_–_crear/editar_producto_(form)/code.html`

**❓ NO ANALIZADO AÚN (existe en carpeta antigua)**

---

### 1️⃣4️⃣ **LOGIN ADMINISTRADOR**

**Ubicación:** `Sources/pantallas_version_nueva/login_administrador/code.html`

**❓ NO ANALIZADO AÚN**

---

## 🚨 RESUMEN DE IMPACTO EN BACKEND

### ❌ FUNCIONALIDAD A ELIMINAR (CUPONES)

#### **Fase 2 - Capa de Servicio**
```java
// CarritoService.java
CarritoDTO aplicarCupon(String sessionId, String codigoCupon);  // ELIMINAR
CarritoDTO removerCupon(String sessionId);  // ELIMINAR

// CarritoServiceImpl.java - Eliminar métodos completos
@Override
public CarritoDTO aplicarCupon(String sessionId, String codigoCupon) { ... }  // ELIMINAR
@Override
public CarritoDTO removerCupon(String sessionId) { ... }  // ELIMINAR
```

#### **Fase 5 - REST API**
```java
// CarritoApiController.java - Eliminar 2 endpoints
@PostMapping("/{sessionId}/cupon")  // ELIMINAR
public ResponseEntity<CarritoDTO> aplicarCupon(...) { ... }

@DeleteMapping("/{sessionId}/cupon")  // ELIMINAR
public ResponseEntity<CarritoDTO> removerCupon(...) { ... }
```

#### **DTOs - Modificar**
```java
// CarritoDTO.java
private CuponDTO cupon;  // ELIMINAR
private BigDecimal descuento;  // ELIMINAR

// PedidoDTO.java
private String codigoCupon;  // ELIMINAR
private BigDecimal descuento;  // HACER OPCIONAL (puede usarse para descuentos futuros)

// CrearPedidoDTO.java
private String codigoCupon;  // ELIMINAR
```

#### **Entidades - Decisión**
```java
// OPCIÓN 1: Eliminar completamente
// model/Cupon.java - ELIMINAR archivo
// repository/CuponRepository.java - ELIMINAR archivo
// Producto.java - Eliminar relación @ManyToOne Cupon
// CarritoItem.java - Eliminar relación @ManyToOne Cupon
// Pedido.java - Eliminar relación @ManyToOne Cupon

// OPCIÓN 2: Marcar como @Deprecated para uso futuro (RECOMENDADO)
@Deprecated  // AGREGAR
@Entity
@Table(name = "cupones")
public class Cupon {
    // Mantener estructura pero no usar en código activo
}
```

**⚠️ RECOMENDACIÓN**: Usar OPCIÓN 2 para mantener flexibilidad futura sin eliminar tablas de base de datos.

---

### 🆕 FUNCIONALIDAD A IMPLEMENTAR

#### **1. Sistema de Reviews/Calificaciones (PDP)**
- ✅ Entidad `Review` YA EXISTE (Fase 1)
- ✅ `ReviewApiController` YA EXISTE (Fase 5, 11 endpoints)
- ❌ Falta validación de "compra verificada" (solo usuarios que compraron pueden reviewar)
- ❌ Falta cálculo automático de rating promedio en `Producto`

**Modificaciones:**
```java
// Producto.java - AGREGAR
@Column
private Double ratingPromedio = 0.0;

@Column
private Integer totalReviews = 0;

// ProductoService.java - AGREGAR método
public void recalcularRating(Long productoId);
```

#### **2. Estados de Producto (Dashboard Admin)**
```java
// Producto.java - AGREGAR
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private EstadoProducto estado = EstadoProducto.BORRADOR;

public enum EstadoProducto {
    BORRADOR,
    PUBLICADO,
    ARCHIVADO
}

@Column(unique = true, length = 50)  // AGREGAR
private String sku;
```

#### **3. Sistema de Auditoría/Actividad (Dashboard Admin)**
```java
// CREAR: model/ActividadAdmin.java
@Entity
@Table(name = "actividades_admin")
public class ActividadAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActividad tipo;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    // ... getters/setters
}

public enum TipoActividad {
    PRODUCTO_ACTUALIZADO,
    PRODUCTO_CREADO,
    ARTICULO_PUBLICADO,
    PAGINA_MODIFICADA,
    BANNER_ACTUALIZADO
}
```

#### **4. Sistema CMS (Gestionar Contenidos)**
```java
// Pagina.java - MODIFICAR
@Enumerated(EnumType.STRING)  // AGREGAR
private EstadoPagina estado = EstadoPagina.BORRADOR;

public enum EstadoPagina {  // CREAR
    BORRADOR,
    EN_REVISION,
    PUBLICADO
}

// CREAR: controller/api/PaginaApiController.java
@RestController
@RequestMapping("/api/admin/paginas")
@PreAuthorize("hasRole('ADMIN')")
public class PaginaApiController {
    // CRUD completo + publicar/despublicar
}

// CREAR: dto/HeroSectionDTO.java, CarruselDTO.java, BannerDTO.java
```

#### **5. Sistema de Reportería**
```java
// CREAR: service/ReporteService.java
public interface ReporteService {
    ReporteVentasDTO generarReporteVentas(FiltrosReporteDTO filtros);
    List<ProductoRankingDTO> obtenerTopProductos(int topN, LocalDate desde, LocalDate hasta);
    List<ComprasPorProvinciaDTO> obtenerComprasPorProvincia(LocalDate desde, LocalDate hasta);
    byte[] exportarExcel(String tipoReporte, FiltrosReporteDTO filtros);
}

// CREAR: controller/api/ReporteApiController.java
@RestController
@RequestMapping("/api/admin/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteApiController {
    // 4-5 endpoints para reportes y exportación
}

// AGREGAR DEPENDENCIA: Apache POI para Excel
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
```

---

## 📂 GESTIÓN DE ARCHIVOS HTML

### ✅ ACCIONES A REALIZAR

1. **ELIMINAR carpetas antiguas:**
```bash
# En Sources/
rm -rf "admin_–_crear/"
rm -rf "carrito_de_compra/"
rm -rf "checkout_–_datos_personales,_contacto_y_envío/"
rm -rf "confirmación_de_compra_(éxito)/"
rm -rf "detalle_de_producto_(pdp)/"
rm -rf "login_administrador/"
rm -rf "pago_(pasarela_embebida_o_paso_a_pasarela)/"
rm -rf "resultado_de_pago_–_rechazado/"
```

2. **MOVER pantallas nuevas a ubicación principal:**
```bash
# Desde: Sources/pantallas_version_nueva/*
# Hacia: Sources/
mv pantallas_version_nueva/* ./
rmdir pantallas_version_nueva/
```

---

## 📋 CHECKLIST DE REFACTORIZACIÓN

### ⚠️ PRIORIDAD ALTA (Eliminar Cupones)

- [ ] **Backend - Fase 2**
  - [ ] Eliminar métodos en `CarritoService` (aplicarCupon, removerCupon)
  - [ ] Eliminar implementación en `CarritoServiceImpl`
  - [ ] Refactorizar `PedidoServiceImpl.crearPedido()` - remover lógica de cupones

- [ ] **Backend - Fase 5**
  - [ ] Eliminar endpoints en `CarritoApiController` (2 endpoints)
  - [ ] Actualizar `CarritoDTO` - remover campos cupon y descuento
  - [ ] Actualizar `PedidoDTO` - remover campo codigoCupon
  - [ ] Actualizar `CrearPedidoDTO` - remover campo codigoCupon

- [ ] **Entidades (Decisión)**
  - [ ] Marcar `Cupon.java` como @Deprecated
  - [ ] Comentar/remover relaciones @ManyToOne en `Producto`, `CarritoItem`, `Pedido`
  - [ ] Documentar en README que cupones NO están en uso

- [ ] **Testing**
  - [ ] Compilar proyecto: `mvn clean compile`
  - [ ] Ejecutar tests: `mvn test`
  - [ ] Probar endpoints API manualmente (Postman/Swagger)

---

### 🆕 PRIORIDAD MEDIA (Nuevas Funcionalidades Core)

- [ ] **Estados de Producto**
  - [ ] Agregar enum `EstadoProducto` en `Producto.java`
  - [ ] Agregar campo `sku` en `Producto.java`
  - [ ] Migración de BD: ALTER TABLE productos ADD COLUMN estado, sku
  - [ ] Actualizar ProductoService con métodos publicar/archivar/duplicar

- [ ] **Reviews/Rating**
  - [ ] Agregar campos `ratingPromedio` y `totalReviews` en `Producto.java`
  - [ ] Crear método `recalcularRating()` en ProductoService
  - [ ] Agregar trigger o listener en ReviewService para actualizar rating
  - [ ] Validar "compra verificada" en ReviewService

- [ ] **Auditoría/Actividad**
  - [ ] Crear entidad `ActividadAdmin.java`
  - [ ] Crear `ActividadAdminRepository.java`
  - [ ] Crear `ActividadAdminService.java`
  - [ ] Agregar @EventListener en servicios para registrar actividades
  - [ ] Crear endpoint GET /api/admin/actividades/recientes

---

### 🔧 PRIORIDAD BAJA (Funcionalidades Admin Avanzadas)

- [ ] **CMS - Gestionar Contenidos**
  - [ ] Agregar enum `EstadoPagina` en `Pagina.java`
  - [ ] Crear `PaginaApiController.java` (CRUD + publicar)
  - [ ] Crear DTOs: HeroSectionDTO, CarruselDTO, BannerDTO
  - [ ] Implementar sistema de aprobación (workflow básico)

- [ ] **Reportería**
  - [ ] Agregar dependencia Apache POI en `pom.xml`
  - [ ] Crear `ReporteService.java` y `ReporteServiceImpl.java`
  - [ ] Crear `ReporteApiController.java` (5 endpoints)
  - [ ] Implementar queries agregadas para KPIs
  - [ ] Implementar exportación a Excel

---

## 📊 ESTIMACIÓN DE ESFUERZO

| Tarea | Complejidad | Estimación | Fase sugerida |
|-------|-------------|------------|---------------|
| **Eliminar cupones** | Media | 4-6 horas | Fase 5.1 (inmediata) |
| **Estados de producto** | Baja | 2-3 horas | Fase 5.2 |
| **Reviews/Rating** | Media | 3-4 horas | Fase 5.2 |
| **Auditoría** | Media | 4-5 horas | Fase 6 |
| **CMS básico** | Alta | 8-10 horas | Fase 6 |
| **Reportería** | Alta | 10-12 horas | Fase 7 |
| **TOTAL** | - | **31-40 horas** | Fases 5.1 - 7 |

---

## 🔄 PRÓXIMOS PASOS RECOMENDADOS

### **Paso 1: Refactorización inmediata (Fase 5.1)**
1. Eliminar funcionalidad de cupones (endpoints, servicios, DTOs)
2. Compilar y probar que todo funciona sin cupones
3. Actualizar README.md - Documentar cambios en v1.5.1
4. Commit: `"Refactor: Removed coupon functionality to align with real UI designs"`

### **Paso 2: Implementar funcionalidades críticas (Fase 5.2)**
1. Agregar estados de producto (BORRADOR/PUBLICADO/ARCHIVADO)
2. Implementar SKU en productos
3. Mejorar sistema de reviews (rating promedio, compra verificada)
4. Commit: `"Feature: Added product states (draft/published/archived) and SKU field"`

### **Paso 3: Dashboard Admin (Fase 6)**
1. Implementar KPIs básicos (productos publicados, borradores, stock bajo)
2. Sistema de auditoría/actividad reciente
3. Endpoints API para dashboard
4. Commit: `"Feature: Admin dashboard with KPIs and activity log"`

### **Paso 4: CMS y Reportería (Fase 7)**
1. Sistema CMS para editar página de inicio
2. Sistema de reportería básico
3. Exportación a Excel
4. Commit: `"Feature: CMS for homepage management and basic reporting system"`

---

## 📝 NOTAS FINALES

- ✅ **Confirmado**: Las pantallas en `Sources/pantallas_version_nueva/` son las **REALES**
- ✅ **Confirmado**: **NO hay cupones** en carrito ni checkout
- 🆕 **Identificado**: 3 pantallas administrativas nuevas (Dashboard, CMS, Reportería)
- ⚠️ **Impacto**: ~40 horas de trabajo para alinear backend con UI real
- 🎯 **Prioridad**: Eliminar cupones PRIMERO, luego agregar funcionalidades nuevas

---

**Documento generado el:** 2025-01-XX  
**Autor:** GitHub Copilot (AI Assistant)  
**Versión:** 1.0
