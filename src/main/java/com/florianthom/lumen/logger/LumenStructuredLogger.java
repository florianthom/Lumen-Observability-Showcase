package com.florianthom.lumen.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Structured Logging better uses MDC
 */
public final class LumenStructuredLogger {

    private final Logger logger;

    public LumenStructuredLogger(String className) {
        this.logger = LoggerFactory.getLogger(className);
    }

    public void validated(String invoiceId) {
        logger.atInfo()
                .addKeyValue("event.type", "invoice.validated")
                .addKeyValue("invoice.id", invoiceId)
                .log("Invoice validated for invoice {}", invoiceId);
    }

    public void validationFailed(String invoiceId, String reason) {
        logger.atError()
                .addKeyValue("event.type", "invoice.validation.failed")
                .addKeyValue("invoice.id", invoiceId)
                .addKeyValue("error.message", reason)
                .log("Invoice validation failed for invoice id " + invoiceId);
    }
}