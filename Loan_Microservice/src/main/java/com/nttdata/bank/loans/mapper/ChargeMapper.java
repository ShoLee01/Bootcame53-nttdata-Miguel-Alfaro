package com.nttdata.bank.loans.mapper;

import com.nttdata.bank.loans.domain.Operation;
import com.nttdata.bank.loans.model.MakeCreditChargeRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ChargeMapper implements EntityMapper<MakeCreditChargeRequest, Operation>{
    @Override
    public Operation toDomain(MakeCreditChargeRequest model) {
        Operation operation = new Operation();
        BeanUtils.copyProperties(model, operation);
        return operation;
    }

    @Override
    public MakeCreditChargeRequest toModel(Operation domain) {
        MakeCreditChargeRequest request = new MakeCreditChargeRequest();
        BeanUtils.copyProperties(domain, request);
        return request;
    }
}
