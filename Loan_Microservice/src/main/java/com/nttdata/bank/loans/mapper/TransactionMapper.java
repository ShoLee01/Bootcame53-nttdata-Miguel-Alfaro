package com.nttdata.bank.loans.mapper;

import com.nttdata.bank.loans.model.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper implements EntityMapper<Transaction, com.nttdata.bank.loans.domain.Transaction>{
    @Override
    public com.nttdata.bank.loans.domain.Transaction toDomain(Transaction model) {
        com.nttdata.bank.loans.domain.Transaction transaction = new com.nttdata.bank.loans.domain.Transaction();
        BeanUtils.copyProperties(model, transaction);
        return transaction;
    }

    @Override
    public Transaction toModel(com.nttdata.bank.loans.domain.Transaction domain) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(domain, transaction);
        return transaction;
    }
}
