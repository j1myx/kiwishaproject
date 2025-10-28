# DOCUMENTACIÓN MÓDULO GESTIÓN DE PRODUCTOS - PARTE 3

## 6. CAPA DE PRESENTACIÓN (CONTROLADORES WEB)

### 6.1 Patrón MVC (Model-View-Controller)

El **Controlador** es el punto de entrada de las peticiones HTTP. Su responsabilidad es:

1. **Recibir la petición** del navegador.
2. **Validar los datos** de entrada.
3. **Llamar al servicio** correspondiente.
4. **Preparar el modelo** para la vista.
5. **Retornar la vista** (template Thymeleaf).

**Justificación del patrón MVC**:

- **Separación de Concerns**: El controlador NO contiene lógica de negocio, solo orquesta.
- **Testabilidad**: Podemos probar la lógica de negocio (servicio) sin levantar el servidor web.
- **Mantenibilidad**: Cambios en la UI no afectan la lógica de negocio y viceversa.

### 6.2 AdminWebController

**Ubicación**: `src/main/java/com/kiwisha/project/controller/web/AdminWebController.java`

```java
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminWebController {
    
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    
    // ============================================
    // LISTA DE PRODUCTOS
    // ============================================
    
    @GetMapping("/productos")
    public String listarProductos(
            @RequestParam(required = false) EstadoProducto estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        log.debug("Listando productos en admin - página: {}, estado: {}", page, estado);
        
        Page<ProductoDTO> productos;
        
        if (estado != null) {
            productos = productoService.filtrarPorEstado(estado, page, size);
        } else {
            productos = productoService.obtenerProductosConPaginacion(page, size, "creadoEn");
        }
        
        // Cargar categorías para el filtro
        List<CategoriaDTO> categorias = categoriaService.obtenerTodasLasCategorias();
        
        // Preparar modelo para la vista
        model.addAttribute("productos", productos.getContent());
        model.addAttribute("categorias", categorias);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", productos.getTotalPages());
        model.addAttribute("totalElementos", productos.getTotalElements());
        
        return "admin/productos/lista";
    }
    
    // ============================================
    // FORMULARIO CREAR PRODUCTO
    // ============================================
    
    @GetMapping("/productos/nuevo")
    public String mostrarFormularioCrear(Model model) {
        log.debug("Mostrando formulario de creación de producto");
        
        model.addAttribute("producto", new CrearProductoDTO());
        model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        model.addAttribute("estados", EstadoProducto.values());
        model.addAttribute("esNuevo", true);
        
        return "admin/productos/formulario";
    }
    
    // ============================================
    // GUARDAR PRODUCTO (CREAR)
    // ============================================
    
    @PostMapping("/productos")
    public String crearProducto(
            @Valid @ModelAttribute("producto") CrearProductoDTO crearDTO,
            BindingResult result,
            @RequestParam(value = "accion", required = false) String accion,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Intentando crear producto: {} con acción: {}", crearDTO.getTitulo(), accion);
        
        // Validaciones
        if (result.hasErrors()) {
            log.warn("Errores de validación al crear producto: {}", result.getAllErrors());
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            model.addAttribute("estados", EstadoProducto.values());
            model.addAttribute("esNuevo", true);
            return "admin/productos/formulario";
        }
        
        try {
            // Determinar el estado según el botón presionado
            if ("publicar".equals(accion)) {
                crearDTO.setEstado(EstadoProducto.PUBLICADO);
                crearDTO.setPublicado(true);
            } else {
                crearDTO.setEstado(EstadoProducto.BORRADOR);
                crearDTO.setPublicado(false);
            }
            
            // Crear el producto
            ProductoDTO productoCreado = productoService.crearProducto(crearDTO);
            
            log.info("Producto creado exitosamente: ID {} - {}", 
                     productoCreado.getProductoId(), productoCreado.getTitulo());
            
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Producto creado exitosamente: " + productoCreado.getTitulo());
            
            return "redirect:/admin/productos";
            
        } catch (ResourceNotFoundException e) {
            log.error("Error al crear producto: Categoría no encontrada", e);
            model.addAttribute("mensajeError", "La categoría seleccionada no existe");
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            model.addAttribute("estados", EstadoProducto.values());
            model.addAttribute("esNuevo", true);
            return "admin/productos/formulario";
        } catch (Exception e) {
            log.error("Error inesperado al crear producto", e);
            model.addAttribute("mensajeError", "Error al crear el producto: " + e.getMessage());
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            model.addAttribute("estados", EstadoProducto.values());
            model.addAttribute("esNuevo", true);
            return "admin/productos/formulario";
        }
    }
    
    // ============================================
    // FORMULARIO EDITAR PRODUCTO
    // ============================================
    
    @GetMapping("/productos/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        log.debug("Mostrando formulario de edición para producto ID: {}", id);
        
        try {
            ProductoDTO producto = productoService.obtenerProductoPorId(id);
            
            // Convertir ProductoDTO a ActualizarProductoDTO
            ActualizarProductoDTO actualizarDTO = ActualizarProductoDTO.builder()
                .titulo(producto.getTitulo())
                .sku(producto.getSku())
                .resumen(producto.getResumen())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .precioAnterior(producto.getPrecioAnterior())
                .cantidad(producto.getCantidad())
                .estado(producto.getEstado())
                .publicado(producto.getPublicado())
                .destacado(producto.getDestacado())
                .nuevo(producto.getNuevo())
                .enOferta(producto.getEnOferta())
                .imagen(producto.getImagen())
                .categoriaId(producto.getCategoriaId())
                .build();
            
            model.addAttribute("producto", actualizarDTO);
            model.addAttribute("productoId", id);
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            model.addAttribute("estados", EstadoProducto.values());
            model.addAttribute("esNuevo", false);
            
            return "admin/productos/formulario";
            
        } catch (ResourceNotFoundException e) {
            log.error("Producto no encontrado: ID {}", id);
            model.addAttribute("mensajeError", "Producto no encontrado");
            return "redirect:/admin/productos";
        }
    }
    
    // ============================================
    // ACTUALIZAR PRODUCTO
    // ============================================
    
    @PostMapping("/productos/{id}")
    public String actualizarProducto(
            @PathVariable Integer id,
            @Valid @ModelAttribute("producto") ActualizarProductoDTO actualizarDTO,
            BindingResult result,
            @RequestParam(value = "accion", required = false) String accion,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Intentando actualizar producto ID: {} con acción: {}", id, accion);
        
        if (result.hasErrors()) {
            log.warn("Errores de validación al actualizar producto ID: {}", id);
            model.addAttribute("productoId", id);
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            model.addAttribute("estados", EstadoProducto.values());
            model.addAttribute("esNuevo", false);
            return "admin/productos/formulario";
        }
        
        try {
            // Determinar el estado según el botón presionado
            if ("publicar".equals(accion)) {
                actualizarDTO.setEstado(EstadoProducto.PUBLICADO);
                actualizarDTO.setPublicado(true);
            } else if ("borrador".equals(accion)) {
                actualizarDTO.setEstado(EstadoProducto.BORRADOR);
                actualizarDTO.setPublicado(false);
            }
            
            ProductoDTO productoActualizado = productoService.actualizarProducto(id, actualizarDTO);
            
            log.info("Producto actualizado exitosamente: ID {} - {}", 
                     productoActualizado.getProductoId(), productoActualizado.getTitulo());
            
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Producto actualizado exitosamente: " + productoActualizado.getTitulo());
            
            return "redirect:/admin/productos";
            
        } catch (ResourceNotFoundException e) {
            log.error("Error al actualizar producto: No encontrado", e);
            model.addAttribute("mensajeError", "Producto no encontrado");
            return "redirect:/admin/productos";
        } catch (Exception e) {
            log.error("Error inesperado al actualizar producto ID: {}", id, e);
            model.addAttribute("mensajeError", "Error al actualizar el producto: " + e.getMessage());
            model.addAttribute("productoId", id);
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            model.addAttribute("estados", EstadoProducto.values());
            model.addAttribute("esNuevo", false);
            return "admin/productos/formulario";
        }
    }
    
    // ============================================
    // DUPLICAR PRODUCTO (AJAX)
    // ============================================
    
    @PostMapping("/productos/{id}/duplicar")
    @ResponseBody
    public ResponseEntity<?> duplicarProducto(@PathVariable Integer id) {
        log.debug("Duplicando producto ID: {}", id);
        
        try {
            ProductoDTO productoDuplicado = productoService.duplicarProducto(id);
            
            log.info("Producto duplicado exitosamente: Original ID {} -> Duplicado ID {}", 
                     id, productoDuplicado.getProductoId());
            
            // Retornar JSON con el ID del producto duplicado
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Producto duplicado exitosamente",
                "productoId", productoDuplicado.getProductoId()
            ));
            
        } catch (ResourceNotFoundException e) {
            log.error("Error al duplicar producto: No encontrado", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Producto no encontrado"));
        } catch (Exception e) {
            log.error("Error inesperado al duplicar producto ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error al duplicar el producto"));
        }
    }
    
    // ============================================
    // ELIMINAR PRODUCTO (AJAX)
    // ============================================
    
    @DeleteMapping("/productos/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        log.debug("Eliminando producto ID: {}", id);
        
        try {
            productoService.eliminarProducto(id);
            
            log.info("Producto eliminado exitosamente: ID {}", id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Producto eliminado exitosamente"
            ));
            
        } catch (ResourceNotFoundException e) {
            log.error("Error al eliminar producto: No encontrado", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "Producto no encontrado"));
        } catch (Exception e) {
            log.error("Error inesperado al eliminar producto ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error al eliminar el producto"));
        }
    }
}
```

### 6.3 Desglose de Anotaciones Spring MVC

#### 6.3.1 @Controller vs @RestController

```java
@Controller // Para vistas HTML (Thymeleaf)
@RestController // Para APIs REST (JSON)
```

- **@Controller**: Retorna nombres de vistas (Strings) que Thymeleaf procesa.
- **@RestController**: Combina `@Controller` + `@ResponseBody`, retorna JSON directamente.
- **Nuestro caso**: Usamos `@Controller` para la mayoría de endpoints, pero algunos métodos (duplicar, eliminar) tienen `@ResponseBody` para retornar JSON.

#### 6.3.2 @RequestMapping

```java
@RequestMapping("/admin")
```

- Define el prefijo de URL para todos los métodos del controlador.
- Todas las rutas empiezan con `/admin/productos`, `/admin/categorias`, etc.

#### 6.3.3 @GetMapping, @PostMapping, @DeleteMapping

```java
@GetMapping("/productos")          // GET /admin/productos
@PostMapping("/productos")          // POST /admin/productos
@DeleteMapping("/productos/{id}")   // DELETE /admin/productos/123
```

- **GET**: Para consultas (listar, ver formulario).
- **POST**: Para crear y actualizar (HTML forms no soportan PUT/PATCH).
- **DELETE**: Para eliminar (usado con AJAX).

#### 6.3.4 @PathVariable y @RequestParam

```java
@GetMapping("/productos/{id}/editar")
public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
    // {id} en la URL se mapea a la variable 'id'
}

@GetMapping("/productos")
public String listarProductos(
    @RequestParam(required = false) EstadoProducto estado,
    @RequestParam(defaultValue = "0") int page) {
    // ?estado=PUBLICADO&page=2
}
```

- **@PathVariable**: Extrae valores de la URL (`/productos/123`).
- **@RequestParam**: Extrae parámetros de query string (`?estado=PUBLICADO`).

#### 6.3.5 @Valid y BindingResult

```java
@PostMapping("/productos")
public String crearProducto(
    @Valid @ModelAttribute("producto") CrearProductoDTO crearDTO,
    BindingResult result,
    Model model) {
    
    if (result.hasErrors()) {
        // Hay errores de validación
        return "admin/productos/formulario"; // Volver al formulario
    }
    
    // Continuar con la creación...
}
```

- **@Valid**: Activa las validaciones de Jakarta Bean Validation (`@NotBlank`, `@Min`, etc.).
- **BindingResult**: Contiene los errores de validación.
- **Flujo**:
  1. Spring valida el DTO automáticamente.
  2. Si hay errores, `result.hasErrors()` retorna `true`.
  3. Volvemos al formulario con los mensajes de error.
  4. Thymeleaf muestra los errores al usuario.

#### 6.3.6 RedirectAttributes (Flash Messages)

```java
redirectAttributes.addFlashAttribute("mensajeExito", 
    "Producto creado exitosamente: " + productoCreado.getTitulo());

return "redirect:/admin/productos";
```

- **Flash Attributes**: Se almacenan en sesión por una sola petición (el redirect).
- **Patrón POST-Redirect-GET (PRG)**:
  1. Usuario envía formulario (POST).
  2. Servidor procesa y hace redirect (GET).
  3. Si el usuario recarga la página, no reenvía el formulario.
  - **Beneficio**: Evita duplicar productos al refrescar la página.

### 6.4 Seguridad con @PreAuthorize

```java
@PreAuthorize("hasRole('ADMIN')")
public class AdminWebController {
    // ...
}
```

- **Spring Security**: Verifica que el usuario autenticado tenga el rol `ADMIN`.
- Si NO tiene el rol, Spring Security redirige a la página de login o muestra error 403 (Forbidden).
- **Justificación**: Los endpoints administrativos solo deben ser accesibles para administradores.

### 6.5 Manejo de Errores

Implementamos try-catch en cada endpoint para manejar excepciones específicas:

```java
try {
    ProductoDTO producto = productoService.crearProducto(crearDTO);
    redirectAttributes.addFlashAttribute("mensajeExito", "Producto creado");
    return "redirect:/admin/productos";
    
} catch (ResourceNotFoundException e) {
    // Categoría no encontrada
    model.addAttribute("mensajeError", "La categoría seleccionada no existe");
    return "admin/productos/formulario";
    
} catch (DuplicateResourceException e) {
    // SKU duplicado
    model.addAttribute("mensajeError", "El SKU ya existe");
    return "admin/productos/formulario";
    
} catch (Exception e) {
    // Cualquier otro error
    log.error("Error inesperado", e);
    model.addAttribute("mensajeError", "Error al crear el producto");
    return "admin/productos/formulario";
}
```

**Ventajas**:
1. El usuario ve mensajes de error específicos.
2. Los errores se registran en logs para debugging.
3. La aplicación no crashea, muestra el formulario con el error.

---

## 7. VISTAS (TEMPLATES THYMELEAF)

### 7.1 ¿Por qué Thymeleaf?

**Thymeleaf** es un motor de plantillas moderno para Java que se integra perfectamente con Spring Boot.

**Ventajas sobre JSP**:
1. **HTML5 Válido**: Los templates son HTML puro que puedes abrir en el navegador sin servidor.
2. **Natural Templating**: Los designers pueden trabajar en los templates sin conocer Java.
3. **Expresiones Potentes**: `${...}`, `*{...}`, `@{...}`, `#{...}`.
4. **Integración con Spring**: Acceso directo a beans, validaciones, seguridad.

### 7.2 Lista de Productos (lista.html)

**Ubicación**: `src/main/resources/templates/admin/productos/lista.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Productos - Admin</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50">

<!-- Mensajes Flash -->
<div th:if="${mensajeExito}" 
     class="fixed top-4 right-4 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg">
    <span th:text="${mensajeExito}"></span>
</div>

<div th:if="${mensajeError}" 
     class="fixed top-4 right-4 bg-red-500 text-white px-6 py-3 rounded-lg shadow-lg">
    <span th:text="${mensajeError}"></span>
</div>

<!-- Header -->
<div class="bg-white shadow">
    <div class="max-w-7xl mx-auto px-4 py-6">
        <div class="flex justify-between items-center">
            <h1 class="text-3xl font-bold text-gray-900">Gestión de Productos</h1>
            <a th:href="@{/admin/productos/nuevo}" 
               class="bg-orange-500 hover:bg-orange-600 text-white px-6 py-2 rounded-lg">
                + Nuevo Producto
            </a>
        </div>
    </div>
</div>

<!-- Filtros -->
<div class="max-w-7xl mx-auto px-4 py-6">
    <div class="bg-white rounded-lg shadow p-6 mb-6">
        <form method="get" th:action="@{/admin/productos}" class="flex gap-4">
            
            <!-- Filtro por Categoría -->
            <select name="categoriaId" 
                    class="min-w-[200px] border border-gray-300 rounded-lg px-4 py-2">
                <option value="">Todas las categorías</option>
                <option th:each="cat : ${categorias}" 
                        th:value="${cat.categoriaId}"
                        th:text="${cat.titulo}"
                        th:selected="${categoriaId == cat.categoriaId}">
                </option>
            </select>
            
            <!-- Filtro por Estado -->
            <select name="estado" 
                    class="min-w-[180px] border border-gray-300 rounded-lg px-4 py-2">
                <option value="">Todos los estados</option>
                <option th:each="est : ${T(com.kiwisha.project.model.EstadoProducto).values()}"
                        th:value="${est}"
                        th:text="${est}"
                        th:selected="${estadoSeleccionado == est}">
                </option>
            </select>
            
            <!-- Filtro por Precio -->
            <select name="precio" 
                    class="min-w-[180px] border border-gray-300 rounded-lg px-4 py-2">
                <option value="">Todos los precios</option>
                <option value="0-50">S/ 0 - S/ 50</option>
                <option value="50-100">S/ 50 - S/ 100</option>
                <option value="100-200">S/ 100 - S/ 200</option>
                <option value="200+">Más de S/ 200</option>
            </select>
            
            <!-- Campo de búsqueda -->
            <input type="text" 
                   name="busqueda" 
                   placeholder="Buscar por título..."
                   class="flex-1 border border-gray-300 rounded-lg px-4 py-2">
            
            <!-- Botones -->
            <button type="submit" 
                    class="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded-lg">
                Filtrar
            </button>
            
            <a th:href="@{/admin/productos}" 
               class="bg-gray-300 hover:bg-gray-400 text-gray-700 px-6 py-2 rounded-lg">
                Limpiar
            </a>
        </form>
    </div>
    
    <!-- Tabla de Productos -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
                <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Imagen
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Título
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        SKU
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Precio
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Stock
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Estado
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                        Acciones
                    </th>
                </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
                <tr th:each="producto : ${productos}" 
                    th:classappend="${productoStat.odd} ? 'bg-white' : 'bg-gray-50'">
                    
                    <!-- Imagen -->
                    <td class="px-6 py-4">
                        <img th:src="${producto.imagen != null ? producto.imagen : '/images/placeholder.png'}" 
                             alt="Producto"
                             class="w-16 h-16 object-cover rounded">
                    </td>
                    
                    <!-- Título -->
                    <td class="px-6 py-4">
                        <div class="text-sm font-medium text-gray-900" 
                             th:text="${producto.titulo}">
                        </div>
                        <div class="text-sm text-gray-500" 
                             th:text="${producto.categoriaTitulo}">
                        </div>
                    </td>
                    
                    <!-- SKU -->
                    <td class="px-6 py-4 text-sm text-gray-900" 
                        th:text="${producto.sku}">
                    </td>
                    
                    <!-- Precio -->
                    <td class="px-6 py-4 text-sm text-gray-900">
                        <span th:text="'S/ ' + ${#numbers.formatDecimal(producto.precio, 1, 2)}"></span>
                    </td>
                    
                    <!-- Stock -->
                    <td class="px-6 py-4">
                        <span th:classappend="${producto.cantidad < 10} ? 'text-red-600 font-bold' : 'text-gray-900'"
                              th:text="${producto.cantidad}">
                        </span>
                    </td>
                    
                    <!-- Estado (Badge) -->
                    <td class="px-6 py-4">
                        <span th:class="${producto.estado == T(com.kiwisha.project.model.EstadoProducto).PUBLICADO} ? 
                                        'bg-green-100 text-green-800' : 
                                        ${producto.estado == T(com.kiwisha.project.model.EstadoProducto).BORRADOR} ?
                                        'bg-yellow-100 text-yellow-800' : 
                                        'bg-gray-100 text-gray-800'"
                              class="px-2 py-1 text-xs font-semibold rounded-full"
                              th:text="${producto.estado}">
                        </span>
                    </td>
                    
                    <!-- Acciones -->
                    <td class="px-6 py-4 text-sm font-medium space-x-2">
                        <a th:href="@{/admin/productos/{id}/editar(id=${producto.productoId})}" 
                           class="text-blue-600 hover:text-blue-900">
                            Editar
                        </a>
                        
                        <button th:onclick="'duplicarProducto(' + ${producto.productoId} + ')'"
                                class="text-green-600 hover:text-green-900">
                            Duplicar
                        </button>
                        
                        <button th:onclick="'confirmarEliminar(' + ${producto.productoId} + ', \'' + ${producto.titulo} + '\')'"
                                class="text-red-600 hover:text-red-900">
                            Eliminar
                        </button>
                    </td>
                </tr>
                
                <!-- Fila vacía si no hay productos -->
                <tr th:if="${#lists.isEmpty(productos)}">
                    <td colspan="7" class="px-6 py-8 text-center text-gray-500">
                        No se encontraron productos
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    
    <!-- Paginación -->
    <div class="mt-6 flex justify-center" th:if="${totalPaginas > 1}">
        <nav class="flex space-x-2">
            <a th:each="i : ${#numbers.sequence(0, totalPaginas - 1)}"
               th:href="@{/admin/productos(page=${i}, estado=${estadoSeleccionado})}"
               th:class="${i == paginaActual} ? 
                         'bg-orange-500 text-white' : 
                         'bg-white text-gray-700 hover:bg-gray-100'"
               class="px-4 py-2 border rounded-lg"
               th:text="${i + 1}">
            </a>
        </nav>
    </div>
</div>

<!-- JavaScript para Duplicar/Eliminar -->
<script th:inline="javascript">
    function duplicarProducto(id) {
        if (!confirm('¿Deseas duplicar este producto?')) return;
        
        fetch(`/admin/productos/${id}/duplicar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Redirigir al formulario de edición del producto duplicado
                window.location.href = `/admin/productos/${data.productoId}/editar`;
            } else {
                alert('Error al duplicar: ' + data.message);
            }
        })
        .catch(error => {
            alert('Error de red: ' + error);
        });
    }
    
    function confirmarEliminar(id, titulo) {
        if (!confirm(`¿Estás seguro de eliminar "${titulo}"?`)) return;
        
        fetch(`/admin/productos/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                alert('Error al eliminar: ' + data.message);
            }
        })
        .catch(error => {
            alert('Error de red: ' + error);
        });
    }
</script>

</body>
</html>
```

### 7.3 Expresiones Thymeleaf

#### 7.3.1 Variable Expressions (`${...}`)

```html
<span th:text="${producto.titulo}">Título del producto</span>
```

- Evalúa una expresión contra el modelo (`Model`).
- `producto` viene de `model.addAttribute("producto", dto)`.

#### 7.3.2 Selection Expressions (`*{...}`)

```html
<form th:object="${producto}">
    <input type="text" th:field="*{titulo}">
</form>
```

- Trabaja con el objeto seleccionado (`th:object`).
- `*{titulo}` es equivalente a `${producto.titulo}`.

#### 7.3.3 URL Expressions (`@{...}`)

```html
<a th:href="@{/admin/productos/{id}/editar(id=${producto.productoId})}">Editar</a>
```

- Genera URLs con context path automático.
- **Resultado**: `/contexto/admin/productos/123/editar`.
- Soporta parámetros de query string: `@{/productos(page=0,estado='PUBLICADO')}`.

#### 7.3.4 Utility Objects

```html
<!-- Formatear números -->
<span th:text="'S/ ' + ${#numbers.formatDecimal(producto.precio, 1, 2)}"></span>

<!-- Formatear fechas -->
<span th:text="${#temporals.format(producto.creadoEn, 'dd/MM/yyyy')}"></span>

<!-- Verificar listas vacías -->
<tr th:if="${#lists.isEmpty(productos)}">...</tr>
```

### 7.4 Integración con Spring Security

```html
<html xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

<!-- Mostrar solo si el usuario tiene rol ADMIN -->
<div sec:authorize="hasRole('ADMIN')">
    <button>Eliminar producto</button>
</div>

<!-- Mostrar el nombre del usuario autenticado -->
<span sec:authentication="name">Usuario</span>
```

---

*Fin de la Parte 3. Continúo con la Parte 4 (final)...*
