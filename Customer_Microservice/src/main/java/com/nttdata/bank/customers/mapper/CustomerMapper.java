package com.nttdata.bank.customers.mapper;


import com.nttdata.bank.customers.model.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper implements EntityMapper<Customer, com.nttdata.bank.customers.domain.Customer>{
    @Override
    public com.nttdata.bank.customers.domain.Customer toDomain(Customer model) {
        com.nttdata.bank.customers.domain.Customer customer = new com.nttdata.bank.customers.domain.Customer();
        BeanUtils.copyProperties(model, customer);
        return customer;
    }

    @Override
    public Customer toModel(com.nttdata.bank.customers.domain.Customer domain) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(domain, customer);
        return customer;
    }
}
