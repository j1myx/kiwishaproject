package com.kiwisha.project.service;

import com.kiwisha.project.dto.MercadoPagoPreferenceDTO;
import com.kiwisha.project.dto.PedidoDTO;

/**
 * Servicio de integración con Mercado Pago.
 */
public interface MercadoPagoService {

    /**
     * Crea una preferencia (Checkout Pro) para un pedido y devuelve su preferenceId.
     */
    MercadoPagoPreferenceDTO crearPreferenciaParaPedido(PedidoDTO pedido, String baseUrl);

    /**
     * Devuelve la public key configurada (para inicializar el SDK en el frontend).
     */
    String obtenerPublicKey();

    /**
     * Consulta el estado del pago en Mercado Pago usando external_reference.
     * Devuelve null si no hay pagos todavía.
     */
    String buscarEstadoPagoPorExternalReference(String externalReference);
}
