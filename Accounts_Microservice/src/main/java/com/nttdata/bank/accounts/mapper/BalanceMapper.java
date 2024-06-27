package com.nttdata.bank.accounts.mapper;

import com.nttdata.bank.accounts.domain.Balance;
import com.nttdata.bank.accounts.model.GetAccountBalance200Response;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BalanceMapper implements EntityMapper<GetAccountBalance200Response, Balance>{
    @Override
    public Balance toDomain(GetAccountBalance200Response model) {
        Balance balance = new Balance();
        BeanUtils.copyProperties(model, balance);
        return balance;
    }

    @Override
    public GetAccountBalance200Response toModel(Balance domain) {
        GetAccountBalance200Response model = new GetAccountBalance200Response();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
