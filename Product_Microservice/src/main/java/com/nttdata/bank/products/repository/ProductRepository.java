package com.nttdata.bank.products.repository;

import com.nttdata.bank.products.domain.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
