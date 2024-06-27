package com.nttdata.bank.accounts.mapper;

public interface EntityMapper <D, E>{
    E toDomain(D model);
    D toModel(E domain);
}