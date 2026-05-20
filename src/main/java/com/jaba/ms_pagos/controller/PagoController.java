package com.jaba.ms_pagos.controller;

import com.jaba.ms_pagos.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarPago(
            @RequestParam String pasarela,
            @RequestParam Double monto,
            @RequestParam String ordenCompra,
            @RequestParam Long usuarioId) { // <-- Captura del ID de usuario para auditoría

        Map<String, Object> resultado;

        switch (pasarela.toLowerCase()) {
            case "webpay":
                resultado = pagoService.procesarPagoWebpay(monto, ordenCompra, usuarioId);
                break;
            case "mercadopago":
                resultado = pagoService.procesarPagoMercadoPago(monto, ordenCompra, usuarioId);
                break;
            case "khipu":
                resultado = pagoService.procesarPagoKhipu(monto, ordenCompra, usuarioId);
                break;
            default:
                return ResponseEntity.badRequest().body(Map.of("error", "Pasarela no soportada."));
        }

        return ResponseEntity.ok(resultado);
    }

    // =================================================================
    // NUEVO ENDPOINT: Webhook para confirmar el pago y generar el PDF
    // =================================================================
    @PostMapping("/webhook/confirmar/{ordenCompra}")
    public ResponseEntity<Map<String, Object>> simularConfirmacionBanco(@PathVariable String ordenCompra) {
        
        // Llamamos al método mágico que aprueba, genera el PDF y avisa a RabbitMQ
        Map<String, Object> resultado = pagoService.confirmarPagoYEmitirDocumento(ordenCompra);
        
        return ResponseEntity.ok(resultado);
    }
}