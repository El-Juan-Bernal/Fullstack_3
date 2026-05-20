package com.jaba.ms_pagos.service;

import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class SiiService {

    // Si el método principal falla, Spring llama automáticamente a 'fallbackSii'
    @CircuitBreaker(name = "siiCB", fallbackMethod = "fallbackSii")
    public String solicitarFolioSii(String tipoDocumento, Double montoTotal) {
        System.out.println("⏳ Conectando con los servidores del SII para emitir " + tipoDocumento + "...");

        // 👇 TRUCO PARA POSTMAN: Simulamos que el SII se cae el 50% de las veces al azar.
        // Así podrás ver en Postman cómo el sistema reacciona al éxito y al fracaso.
        if (Math.random() > 0.5) {
            throw new RuntimeException("Error 504: Gateway Timeout en portal del SII.");
        }

        // Si sobrevive, retorna un folio válido
        return "F-" + ((int)(Math.random() * 90000) + 10000);
    }

    // EL SALVAVIDAS: Se ejecuta solo si el método de arriba lanza una excepción
    public String fallbackSii(String tipoDocumento, Double montoTotal, Throwable t) {
        System.err.println("🚨 ¡Alerta SII Caído! Activando plan de contingencia. Motivo: " + t.getMessage());
        return "PENDIENTE_SII"; // Clave secreta para que el sistema sepa que falló
    }
}