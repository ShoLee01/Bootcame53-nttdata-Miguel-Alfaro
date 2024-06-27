package com.nttdata.bank.accounts.mapper;

import com.nttdata.bank.accounts.domain.Operation;
import com.nttdata.bank.accounts.model.WithdrawFromAccountRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class WithdrawMapper implements EntityMapper<WithdrawFromAccountRequest, Operation>{
    @Override
    public Operation toDomain(WithdrawFromAccountRequest model) {
        Operation operation = new Operation();
        BeanUtils.copyProperties(model, operation);
        return operation;
    }

    @Override
    public WithdrawFromAccountRequest toModel(Operation domain) {
        WithdrawFromAccountRequest request = new WithdrawFromAccountRequest();
        BeanUtils.copyProperties(domain, request);
        return request;
    }
}
