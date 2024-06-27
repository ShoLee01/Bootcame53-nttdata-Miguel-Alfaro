package com.nttdata.bank.products.mapper;

import com.nttdata.bank.products.model.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements EntityMapper<Product, com.nttdata.bank.products.domain.Product>{

    @Override
    public com.nttdata.bank.products.domain.Product toDomain(Product model) {
        com.nttdata.bank.products.domain.Product domain = new com.nttdata.bank.products.domain.Product();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    @Override
    public Product toModel(com.nttdata.bank.products.domain.Product domain) {
        Product model = new Product();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
