package com.nttdata.bank.accounts.mapper;

import com.nttdata.bank.accounts.model.Account;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper implements EntityMapper<Account, com.nttdata.bank.accounts.domain.Account>{

    @Override
    public com.nttdata.bank.accounts.domain.Account toDomain(Account model) {
        com.nttdata.bank.accounts.domain.Account account = new com.nttdata.bank.accounts.domain.Account();
        BeanUtils.copyProperties(model, account);
        return account;
    }

    @Override
    public Account toModel(com.nttdata.bank.accounts.domain.Account domain) {
        Account account = new Account();
        BeanUtils.copyProperties(domain, account);
        return account;
    }
}
