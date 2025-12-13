package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.MercadoPagoPreferenceDTO;
import com.kiwisha.project.dto.PedidoDTO;
import com.kiwisha.project.dto.PedidoElementoDTO;
import com.kiwisha.project.service.MercadoPagoService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoServiceImpl implements MercadoPagoService {

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @Value("${mercadopago.public-key:}")
    private String publicKey;

    @Value("${mercadopago.currency:PEN}")
    private String currency;

    /**
     * AUTO (default): usa sandbox si el token es TEST-.
     * TEST/SANDBOX: fuerza sandbox_init_point.
     * PROD/PRODUCTION: fuerza init_point.
     */
    @Value("${mercadopago.environment:AUTO}")
    private String environment;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @PostConstruct
    void logConfigStatus() {
        boolean hasAccessToken = accessToken != null && !accessToken.isBlank();
        boolean hasPublicKey = publicKey != null && !publicKey.isBlank();
        log.info(
                "MercadoPago config: accessTokenPresent={}, accessTokenMode={}, publicKeyPresent={}, publicKeyMode={}, currency={}",
                hasAccessToken,
                inferCredentialMode(accessToken),
                hasPublicKey,
                inferCredentialMode(publicKey),
                currency
        );
    }

    @Override
    public MercadoPagoPreferenceDTO crearPreferenciaParaPedido(PedidoDTO pedido, String baseUrl) {
        if (pedido == null || pedido.getPedidoId() == null) {
            throw new IllegalArgumentException("Pedido inválido para crear preferencia");
        }
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Mercado Pago no está configurado: falta mercadopago.access-token");
        }

        MercadoPagoConfig.setAccessToken(accessToken);

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("BaseUrl inválida para Mercado Pago");
        }

        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String successUrl = baseUrl + "/checkout/mercadopago/success?pedidoId=" + pedido.getPedidoId();
        String failureUrl = baseUrl + "/checkout/mercadopago/failure?pedidoId=" + pedido.getPedidoId();
        String pendingUrl = baseUrl + "/checkout/mercadopago/pending?pedidoId=" + pedido.getPedidoId();

        log.info("MercadoPago back_urls: success={}, failure={}, pending={}", successUrl, failureUrl, pendingUrl);

        List<PreferenceItemRequest> items = new ArrayList<>();

        if (pedido.getElementos() != null && !pedido.getElementos().isEmpty()) {
            for (PedidoElementoDTO elemento : pedido.getElementos()) {
                if (elemento == null || elemento.getCantidad() == null || elemento.getCantidad() <= 0) {
                    continue;
                }
                BigDecimal unitPrice = elemento.getPrecio() != null ? elemento.getPrecio() : BigDecimal.ZERO;
                if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    log.warn("Saltando item con precio inválido para MercadoPago. pedidoId={}, producto={}, cantidad={}, unitPrice={}",
                            pedido.getPedidoId(), elemento.getProductoTitulo(), elemento.getCantidad(), unitPrice);
                    continue;
                }
                items.add(
                        PreferenceItemRequest.builder()
                                .title(elemento.getProductoTitulo() != null ? elemento.getProductoTitulo() : "Producto")
                                .quantity(elemento.getCantidad())
                                .currencyId(currency)
                                .unitPrice(unitPrice)
                                .build()
                );
            }
        }

        if (items.isEmpty()) {
            // Fallback: usar el total del pedido como un único ítem
            BigDecimal total = pedido.getTotal() != null ? pedido.getTotal() : BigDecimal.ZERO;
            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Total del pedido inválido para Mercado Pago");
            }
            items = List.of(
                    PreferenceItemRequest.builder()
                            .title("Pedido " + (pedido.getCodigo() != null ? pedido.getCodigo() : pedido.getPedidoId()))
                            .quantity(1)
                            .currencyId(currency)
                            .unitPrice(total)
                            .build()
            );
        }

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .failure(failureUrl)
                .pending(pendingUrl)
                .build();

        boolean enableAutoReturn = baseUrl.toLowerCase().startsWith("https://");

        // Algunas combinaciones de SDK/API pueden terminar persistiendo arrays con IDs vacíos
        // en payment_methods. Para evitarlo, seteamos explícitamente listas vacías.
        PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                .excludedPaymentMethods(Collections.emptyList())
                .excludedPaymentTypes(Collections.emptyList())
                .build();

        PreferenceRequest.PreferenceRequestBuilder requestBuilder = PreferenceRequest.builder()
            .items(items)
            .backUrls(backUrls)
            .paymentMethods(paymentMethods)
            .externalReference(pedido.getCodigo() != null ? pedido.getCodigo() : String.valueOf(pedido.getPedidoId()));

        // Nota: Mercado Pago puede ser estricto con auto_return y URLs no públicas/https.
        if (enableAutoReturn) {
            requestBuilder.autoReturn("approved");
        } else {
            log.info("MercadoPago auto_return deshabilitado (baseUrl no es https): {}", baseUrl);
        }

        PreferenceRequest request = requestBuilder.build();

        try {
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(request);

            log.info(
                    "Preferencia MercadoPago creada. pedidoId={}, preferenceId={}, initPoint={}, sandboxInitPoint={}",
                    pedido.getPedidoId(),
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint()
            );

            return new MercadoPagoPreferenceDTO(
                preference.getId(),
                preference.getInitPoint(),
                preference.getSandboxInitPoint()
            );
        } catch (MPApiException e) {
            if (e.getApiResponse() != null) {
                log.error(
                        "Error MercadoPago API creando preferencia. pedidoId={}, statusCode={}, response={}",
                        pedido.getPedidoId(),
                        e.getApiResponse().getStatusCode(),
                        e.getApiResponse().getContent(),
                        e
                );
            } else {
                log.error("Error MercadoPago API creando preferencia (sin apiResponse). pedidoId={}", pedido.getPedidoId(), e);
            }
            throw new RuntimeException("No se pudo crear la preferencia de Mercado Pago", e);
        } catch (MPException e) {
            log.error("Error MercadoPago SDK creando preferencia. pedidoId={}", pedido.getPedidoId(), e);
            throw new RuntimeException("No se pudo crear la preferencia de Mercado Pago", e);
        }
    }

    @Override
    public String obtenerPublicKey() {
        return publicKey;
    }

    @Override
    public String buscarEstadoPagoPorExternalReference(String externalReference) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Mercado Pago no está configurado: falta mercadopago.access-token");
        }
        if (externalReference == null || externalReference.isBlank()) {
            return null;
        }

        try {
            String encoded = URLEncoder.encode(externalReference, StandardCharsets.UTF_8);
            URI uri = URI.create(
                    "https://api.mercadopago.com/v1/payments/search?external_reference=" + encoded
                            + "&sort=date_created&criteria=desc&limit=1"
            );

            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .header("Authorization", "Bearer " + accessToken.trim())
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("MercadoPago payments/search falló. statusCode={}, externalReference={}", response.statusCode(), externalReference);
                return null;
            }

            // Parseo liviano sin acoplar DTOs: buscamos results[0].status
            @SuppressWarnings("unchecked")
            Map<String, Object> body = new com.fasterxml.jackson.databind.ObjectMapper().readValue(response.body(), Map.class);
            Object resultsObj = body.get("results");
            if (!(resultsObj instanceof List<?> results) || results.isEmpty()) {
                return null;
            }
            Object firstObj = results.get(0);
            if (!(firstObj instanceof Map<?, ?> first)) {
                return null;
            }
            Object statusObj = first.get("status");
            return statusObj != null ? String.valueOf(statusObj) : null;
        } catch (Exception e) {
            log.warn("Error consultando estado de pago en MercadoPago. externalReference={}", externalReference, e);
            return null;
        }
    }

    public boolean usarSandboxInitPoint() {
        String env = environment != null ? environment.trim().toUpperCase() : "AUTO";
        if (env.equals("TEST") || env.equals("SANDBOX")) {
            return true;
        }
        if (env.equals("PROD") || env.equals("PRODUCTION")) {
            return false;
        }
        return "TEST".equals(inferCredentialMode(accessToken));
    }

    private String inferCredentialMode(String credential) {
        if (credential == null || credential.isBlank()) {
            return "MISSING";
        }
        String trimmed = credential.trim();
        if (trimmed.startsWith("TEST-")) {
            return "TEST";
        }
        if (trimmed.startsWith("APP_USR-")) {
            return "PROD";
        }
        return "UNKNOWN";
    }
}
