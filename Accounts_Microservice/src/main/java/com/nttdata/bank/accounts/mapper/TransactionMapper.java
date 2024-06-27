package com.nttdata.bank.accounts.mapper;

import com.nttdata.bank.accounts.model.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper implements EntityMapper<Transaction, com.nttdata.bank.accounts.domain.Transaction>{

    @Override
    public com.nttdata.bank.accounts.domain.Transaction toDomain(Transaction model) {
        com.nttdata.bank.accounts.domain.Transaction domain = new com.nttdata.bank.accounts.domain.Transaction();
        BeanUtils.copyProperties(model,domain);
        return domain;
    }

    @Override
    public Transaction toModel(com.nttdata.bank.accounts.domain.Transaction domain) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(domain,transaction);
        return transaction;
    }
}
