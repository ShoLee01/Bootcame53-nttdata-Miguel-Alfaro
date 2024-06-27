package com.nttdata.bank.loans.mapper;

public interface EntityMapper <D, E>{
    E toDomain(D model);
    D toModel(E domain);
}