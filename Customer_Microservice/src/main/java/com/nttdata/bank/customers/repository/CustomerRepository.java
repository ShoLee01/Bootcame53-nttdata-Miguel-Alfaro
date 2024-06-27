package com.nttdata.bank.customers.repository;

import com.nttdata.bank.customers.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
