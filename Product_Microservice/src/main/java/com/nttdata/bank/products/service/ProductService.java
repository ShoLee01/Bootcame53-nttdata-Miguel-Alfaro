package com.nttdata.bank.products.service;

import com.nttdata.bank.products.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<Product> save (Mono<Product> product);
    Mono<Product> findById(String id);
    Flux<Product> findAll();
}
