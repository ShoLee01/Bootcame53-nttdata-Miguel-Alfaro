package com.nttdata.bank.loans.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

public class GlobalErrorHandler {
    public static Function<Throwable, Mono<? extends ResponseEntity<Map<String, Object>>>> getThrowableFunction(Map<String, Object> response) {
        return t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "field " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(l -> {
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l);
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }
}