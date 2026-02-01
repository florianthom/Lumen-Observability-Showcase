package com.florianthom.lumen.logger;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LumenLoggerFactory {
    private final ConcurrentMap<String, LumenUnstructuredLogger> cacheUnstructedLogger = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, LumenStructuredLogger> cacheStructuredLogger = new ConcurrentHashMap<>();

    public LumenStructuredLogger getStructuredLogger(Class<?> clazz) {
        return cacheStructuredLogger.computeIfAbsent(clazz.getName(), this::createStructuredLogger);
    }

    public LumenUnstructuredLogger getUnstructuredLogger(Class<?> clazz) {
        return cacheUnstructedLogger.computeIfAbsent(clazz.getName(), this::createUnstructuredLogger);
    }

    private LumenStructuredLogger createStructuredLogger(String name) {
        return new LumenStructuredLogger(name);
    }

    private LumenUnstructuredLogger createUnstructuredLogger(String name) {
        return new LumenUnstructuredLogger(name);
    }
}
