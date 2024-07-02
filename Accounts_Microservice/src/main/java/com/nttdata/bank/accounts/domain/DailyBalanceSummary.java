package com.nttdata.bank.accounts.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyBalanceSummary {
    private String customerId;
    private Double averageBalance;

    public DailyBalanceSummary(String customerId, Double averageBalance) {
        this.customerId = customerId;
        this.averageBalance = averageBalance;
    }
}
