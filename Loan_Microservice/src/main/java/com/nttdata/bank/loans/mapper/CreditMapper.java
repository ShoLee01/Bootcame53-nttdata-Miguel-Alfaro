package com.nttdata.bank.loans.mapper;

import com.nttdata.bank.loans.model.Credit;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CreditMapper implements EntityMapper<Credit, com.nttdata.bank.loans.domain.Credit>{
    @Override
    public com.nttdata.bank.loans.domain.Credit toDomain(Credit model) {
        com.nttdata.bank.loans.domain.Credit credit = new com.nttdata.bank.loans.domain.Credit();
        BeanUtils.copyProperties(model, credit);
        return credit;
    }

    @Override
    public Credit toModel(com.nttdata.bank.loans.domain.Credit domain) {
        Credit credit = new Credit();
        BeanUtils.copyProperties(domain, credit);
        return credit;
    }
}
