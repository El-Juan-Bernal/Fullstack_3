package com.jaba.ms_pagos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // --- 1. EXCHANGES ---
    public static final String EXCHANGE_PRINCIPAL = "smartlogix.venta.exchange";
    public static final String DLX_FALLAS = "smartlogix.fallas.dlx";

    // --- 2. COLAS PRINCIPALES ---
    public static final String COLA_MENSAJERIA = "smartlogix.mensajeria.queue";
    public static final String COLA_BODEGA = "smartlogix.bodega.queue";

    // --- 3. COLAS DE MENSAJES MUERTOS (DLQ) ---
    public static final String DLQ_MENSAJERIA = "smartlogix.mensajeria.dlq";
    public static final String DLQ_BODEGA = "smartlogix.bodega.dlq";

    // --- 4. ROUTING KEYS ---
    public static final String KEY_NOTIFICAR = "venta.notificar";
    public static final String KEY_DESPACHAR = "venta.despachar";
    public static final String KEY_FAIL_NOTIFICAR = "fail.notificar";
    public static final String KEY_FAIL_DESPACHAR = "fail.despachar";

    // Declaración de los Exchanges
    @Bean
    public TopicExchange exchangePrincipal() {
        return new TopicExchange(EXCHANGE_PRINCIPAL);
    }

    @Bean
    public DirectExchange dlxFallas() {
        return new DirectExchange(DLX_FALLAS);
    }

    // --- CONFIGURACIÓN DE COLA MENSAJERIA + SU DLQ ---
    @Bean
    public Queue colaMensajeria() {
        Map<String, Object> args = new HashMap<>();
        // Si el procesamiento falla en ms_mensajeria, el mensaje es enviado al DLX
        args.put("x-dead-letter-exchange", DLX_FALLAS);
        args.put("x-dead-letter-routing-key", KEY_FAIL_NOTIFICAR);
        return new Queue(COLA_MENSAJERIA, true, false, false, args);
    }

    @Bean
    public Queue dlqMensajeria() {
        return new Queue(DLQ_MENSAJERIA, true);
    }

    @Bean
    public Binding bindingMensajeria() {
        return BindingBuilder.bind(colaMensajeria()).to(exchangePrincipal()).with(KEY_NOTIFICAR);
    }

    @Bean
    public Binding bindingDlqMensajeria() {
        return BindingBuilder.bind(dlqMensajeria()).to(dlxFallas()).with(KEY_FAIL_NOTIFICAR);
    }

    // --- CONFIGURACIÓN DE COLA BODEGA + SU DLQ ---
    @Bean
    public Queue colaBodega() {
        Map<String, Object> args = new HashMap<>();
        // Si el procesamiento falla al armar la orden de despacho, va al DLX
        args.put("x-dead-letter-exchange", DLX_FALLAS);
        args.put("x-dead-letter-routing-key", KEY_FAIL_DESPACHAR);
        return new Queue(COLA_BODEGA, true, false, false, args);
    }

    @Bean
    public Queue dlqBodega() {
        return new Queue(DLQ_BODEGA, true);
    }

    @Bean
    public Binding bindingBodega() {
        return BindingBuilder.bind(colaBodega()).to(exchangePrincipal()).with(KEY_DESPACHAR);
    }

    @Bean
    public Binding bindingDlqBodega() {
        return BindingBuilder.bind(dlqBodega()).to(dlxFallas()).with(KEY_FAIL_DESPACHAR);
    }

    // --- CONVERTER PARA MANDAR OBJETOS EN FORMATO JSON ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}