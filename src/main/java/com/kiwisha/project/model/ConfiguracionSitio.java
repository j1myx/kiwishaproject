package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidad que almacena configuraciones del sitio web.
 * Permite gestionar contenido dinámico como visión, misión, banners, etc.
 * Implementa el patrón Key-Value Store para configuraciones flexibles.
 */
@Entity
@Table(name = "configuracion_sitio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionSitio extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Integer configId;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    @Column(columnDefinition = "TEXT")
    private String valor;

    @Column(length = 200)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConfiguracion tipo = TipoConfiguracion.TEXTO;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Boolean editable = true;

    public enum TipoConfiguracion {
        TEXTO,
        TEXTAREA,
        HTML,
        URL,
        EMAIL,
        NUMERO,
        BOOLEAN,
        IMAGEN,
        JSON
    }

    // Constantes para configuraciones comunes
    public static final String SITIO_NOMBRE = "sitio.nombre";
    public static final String SITIO_DESCRIPCION = "sitio.descripcion";
    public static final String SITIO_LOGO = "sitio.logo";
    public static final String SITIO_FAVICON = "sitio.favicon";
    
    public static final String EMPRESA_VISION = "empresa.vision";
    public static final String EMPRESA_MISION = "empresa.mision";
    public static final String EMPRESA_VALORES = "empresa.valores";
    public static final String EMPRESA_HISTORIA = "empresa.historia";
    
    public static final String CONTACTO_EMAIL = "contacto.email";
    public static final String CONTACTO_TELEFONO = "contacto.telefono";
    public static final String CONTACTO_DIRECCION = "contacto.direccion";
    public static final String CONTACTO_WHATSAPP = "contacto.whatsapp";
    
    public static final String REDES_FACEBOOK = "redes.facebook";
    public static final String REDES_INSTAGRAM = "redes.instagram";
    public static final String REDES_TWITTER = "redes.twitter";
    public static final String REDES_LINKEDIN = "redes.linkedin";
    
    public static final String HOME_BANNER_TITULO = "home.banner.titulo";
    public static final String HOME_BANNER_SUBTITULO = "home.banner.subtitulo";
    public static final String HOME_BANNER_IMAGEN = "home.banner.imagen";
    public static final String HOME_BANNER_CTA = "home.banner.cta";
    
    public static final String TIENDA_ENVIO_GRATIS_MINIMO = "tienda.envio.gratis.minimo";
    public static final String TIENDA_MONEDA = "tienda.moneda";
    public static final String TIENDA_IVA = "tienda.iva";
}
