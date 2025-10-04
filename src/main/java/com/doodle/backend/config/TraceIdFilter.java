package com.doodle.backend.config;

import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdFilter implements WebFilter {

    private static final String TRACE_ID = "traceId";

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String traceId = UUID.randomUUID().toString();

        MDC.put(TRACE_ID, traceId);
        exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(TRACE_ID, traceId))
                .doFinally(signal -> MDC.remove(TRACE_ID));
    }
}