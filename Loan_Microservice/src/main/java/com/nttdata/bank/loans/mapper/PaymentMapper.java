package com.nttdata.bank.loans.mapper;

import com.nttdata.bank.loans.domain.Operation;
import com.nttdata.bank.loans.model.MakeCreditPaymentRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper implements EntityMapper<MakeCreditPaymentRequest, Operation>{
    @Override
    public Operation toDomain(MakeCreditPaymentRequest model) {
        Operation operation = new Operation();
        BeanUtils.copyProperties(model, operation);
        return operation;
    }

    @Override
    public MakeCreditPaymentRequest toModel(Operation domain) {
        MakeCreditPaymentRequest request = new MakeCreditPaymentRequest();
        BeanUtils.copyProperties(domain, request);
        return request;
    }
}
