package com.florianthom.lumen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LumenLogger {

    private final Logger log;

    public LumenLogger(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    public void validated(String invoiceId) {
        log.atInfo()
                .addKeyValue("event.type", "invoice.validated")
                .addKeyValue("invoice.id", invoiceId)
                .log("Invoice validated for invoice {}", invoiceId);
    }

    public void validationFailed(String invoiceId, String reason) {
        log.atError()
                .addKeyValue("event.type", "invoice.validation.failed")
                .addKeyValue("invoice.id", invoiceId)
                .addKeyValue("error.message", reason)
                .log("Invoice validation failed for invoice id " + invoiceId);
    }
}