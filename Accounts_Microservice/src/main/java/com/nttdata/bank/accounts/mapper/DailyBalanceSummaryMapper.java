package com.nttdata.bank.accounts.mapper;
import com.nttdata.bank.accounts.domain.DailyBalanceSummary;
import com.nttdata.bank.accounts.model.AverageDailyBalanceSummary;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DailyBalanceSummaryMapper implements EntityMapper<AverageDailyBalanceSummary, DailyBalanceSummary>{

    @Override
    public DailyBalanceSummary toDomain(AverageDailyBalanceSummary model) {
        DailyBalanceSummary dailyBalanceSummary = new DailyBalanceSummary();
        BeanUtils.copyProperties(model, dailyBalanceSummary);
        return dailyBalanceSummary;
    }

    @Override
    public AverageDailyBalanceSummary toModel(DailyBalanceSummary domain) {
        AverageDailyBalanceSummary model = new AverageDailyBalanceSummary();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
