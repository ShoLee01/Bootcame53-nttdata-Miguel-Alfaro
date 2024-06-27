package com.nttdata.bank.products.mapper;

public interface EntityMapper <D, E>{
    E toDomain(D model);
    D toModel(E domain);
}
