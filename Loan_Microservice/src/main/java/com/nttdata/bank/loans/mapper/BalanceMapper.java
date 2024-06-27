package com.nttdata.bank.loans.mapper;

import com.nttdata.bank.loans.domain.Balance;
import com.nttdata.bank.loans.model.GetCreditBalance200Response;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BalanceMapper implements EntityMapper<GetCreditBalance200Response, Balance>{
    @Override
    public Balance toDomain(GetCreditBalance200Response model) {
        Balance balance = new Balance();
        BeanUtils.copyProperties(model, balance);
        return balance;
    }

    @Override
    public GetCreditBalance200Response toModel(Balance domain) {
        GetCreditBalance200Response model = new GetCreditBalance200Response();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}