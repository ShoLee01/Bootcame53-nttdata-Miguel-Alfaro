package com.nttdata.bank.loans.mapper;

import com.nttdata.bank.loans.model.CommissionReport;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CommissionReportMapper implements EntityMapper<CommissionReport, com.nttdata.bank.loans.domain.CommissionReport>{
    @Override
    public com.nttdata.bank.loans.domain.CommissionReport toDomain(CommissionReport model) {
        com.nttdata.bank.loans.domain.CommissionReport domain = new com.nttdata.bank.loans.domain.CommissionReport();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    @Override
    public CommissionReport toModel(com.nttdata.bank.loans.domain.CommissionReport domain) {
        CommissionReport model = new CommissionReport();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
