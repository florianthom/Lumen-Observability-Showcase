package com.florianthom.lumen.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LumenUnstructuredLogger {

    private final Logger logger;

    public LumenUnstructuredLogger(String className) {
        this.logger = LoggerFactory.getLogger(className);
    }

    public void validated(String invoiceId) {
        System.out.println("invoice with id " + invoiceId + " validated successfully");
    }

    public void validationFailed(String invoiceId, String reason) {
        System.out.println("invoice with id " + invoiceId + " validated with failure: " + reason);
    }
}