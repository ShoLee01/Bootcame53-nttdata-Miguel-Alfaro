package com.nttdata.bank.products.service;

import com.nttdata.bank.products.domain.Product;
import com.nttdata.bank.products.repository.ProductRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@NoArgsConstructor
public class ProductServiceImpl implements  ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Mono<Product> save(Mono<Product> product) {
        return product.flatMap(productRepository::save);
    }

    @Override
    public Mono<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Flux<Product> findAll() {
        return productRepository.findAll();
    }
}
