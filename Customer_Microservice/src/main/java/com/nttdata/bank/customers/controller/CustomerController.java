package com.nttdata.bank.customers.controller;

import com.nttdata.bank.customers.api.CustomersApi;
import com.nttdata.bank.customers.exception.GlobalErrorHandler;
import com.nttdata.bank.customers.mapper.CustomerMapper;
import com.nttdata.bank.customers.model.Customer;
import com.nttdata.bank.customers.service.CustomerService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@NoArgsConstructor
public class CustomerController implements CustomersApi {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> addCustomer(Mono<Customer> customer, ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        return customerService.save(customer.map(customerMapper::toDomain))
                .map(customerMapper::toModel)
                .map(c -> {
                   response.put("customer", c);
                   response.put("message", "Customer created successfully");
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .onErrorResume(WebExchangeBindException.class, GlobalErrorHandler.getThrowableFunction(response));
    }

    @Override
    public Mono<ResponseEntity<Customer>> getCustomerById(String id, ServerWebExchange exchange) {
        return customerService.findById(id)
                .map(customerMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> updateCustomer(String id, Mono<Customer> customer, ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        return customerService.update(id, customer.map(customerMapper::toDomain))
                .map(customerMapper::toModel)
                .map(c -> {
                    response.put("customer", c);
                    response.put("message", "Customer updated successfully");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                })
                .onErrorResume(WebExchangeBindException.class, GlobalErrorHandler.getThrowableFunction(response))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(String id, ServerWebExchange exchange) {
        return customerService.findById(id)
                .flatMap(c -> customerService.delete(id)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
