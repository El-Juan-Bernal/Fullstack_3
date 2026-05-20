package com.jaba.ms_pagos.messaging;

import java.time.LocalDateTime;

// Usamos un Record de Java para crear un DTO inmutable y rápido
public record VentaAprobadaEvent(
        Long pagoId,
        String ordenCompra,
        Long usuarioId,
        Double montoTotal,
        String tipoDocumento, // "BOLETA" o "FACTURA"
        String folioSii,
        byte[] documentoPdf,  // Aquí viajará el archivo dibujado por el Factory
        LocalDateTime fechaHora
) {}