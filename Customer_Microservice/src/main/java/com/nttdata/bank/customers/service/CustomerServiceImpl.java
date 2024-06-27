package com.nttdata.bank.customers.service;

import com.nttdata.bank.customers.domain.Customer;
import com.nttdata.bank.customers.repository.CustomerRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@NoArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Mono<Customer> save(Mono<Customer> customer) {
        return customer.flatMap(customerRepository::insert);
    }

    @Override
    public Mono<Customer> findById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Mono<Customer> update(String id, Mono<Customer> customer) {
        return customerRepository.findById(id)
                .flatMap(c -> customer)
                .doOnNext(e -> e.setId(id))
                .flatMap(customerRepository::save);
    }

    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.findById(id)
                .flatMap(c -> customerRepository.deleteById(id));
    }
}
