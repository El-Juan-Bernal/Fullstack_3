package com.jaba.ms_pagos.service.pdf;

import com.jaba.ms_pagos.model.Pago;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component("COMPROBANTE") // Se activa cuando el SII falla
public class GeneradorComprobante implements GeneradorPdf {

    @Override
    public byte[] generarDocumento(Pago pago) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fontAlerta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph titulo = new Paragraph("Smart Logix\nCOMPROBANTE DE PAGO TEMPORAL\n\n", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph alerta = new Paragraph("ATENCIÓN: Estimado cliente, su pago ha sido procesado con éxito. Sin embargo, los servicios del SII se encuentran temporalmente fuera de línea. Su documento tributario definitivo será enviado a su correo a la brevedad.\n\n", fontAlerta);
            document.add(alerta);

            document.add(new Paragraph("Orden de Compra: " + pago.getOrdenCompra(), fontNormal));
            document.add(new Paragraph("Monto Pagado: $" + pago.getMontoTotal(), fontNormal));
            document.add(new Paragraph("Fecha: " + pago.getFechaHora().toLocalDate().toString(), fontNormal));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}