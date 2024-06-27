package com.nttdata.bank.customers.mapper;

public interface EntityMapper <D, E>{
    E toDomain(D model);
    D toModel(E domain);
}
