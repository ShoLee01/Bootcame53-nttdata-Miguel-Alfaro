package com.nttdata.bank.accounts.mapper;

import com.nttdata.bank.accounts.domain.DailyBalanceSummary;
import com.nttdata.bank.accounts.model.GetAverageDailyBalance200Response;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DailyBalanceSummaryMapper implements EntityMapper<GetAverageDailyBalance200Response, DailyBalanceSummary>{
    @Override
    public DailyBalanceSummary toDomain(GetAverageDailyBalance200Response model) {
        DailyBalanceSummary dailyBalanceSummary = new DailyBalanceSummary();
        BeanUtils.copyProperties(model, dailyBalanceSummary);
        return dailyBalanceSummary;
    }

    @Override
    public GetAverageDailyBalance200Response toModel(DailyBalanceSummary domain) {
        GetAverageDailyBalance200Response model = new GetAverageDailyBalance200Response();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
