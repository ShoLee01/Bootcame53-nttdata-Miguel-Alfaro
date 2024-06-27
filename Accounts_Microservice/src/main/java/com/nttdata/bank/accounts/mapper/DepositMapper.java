package com.nttdata.bank.accounts.mapper;

import com.nttdata.bank.accounts.domain.Operation;
import com.nttdata.bank.accounts.model.DepositToAccountRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DepositMapper implements EntityMapper<DepositToAccountRequest, Operation>{
    @Override
    public Operation toDomain(DepositToAccountRequest model) {
        Operation operation = new Operation();
        BeanUtils.copyProperties(model, operation);
        return operation;
    }

    @Override
    public DepositToAccountRequest toModel(Operation domain) {
        DepositToAccountRequest request = new DepositToAccountRequest();
        BeanUtils.copyProperties(domain, request);
        return request;
    }
}
