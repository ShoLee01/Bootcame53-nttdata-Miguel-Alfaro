package com.nttdata.bank.loans.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyBalanceSummary {
    private String creditId;
    private String productId;
    private String creditUsageType;
    private Double averageDailyBalance;

    public DailyBalanceSummary(String creditId, String productId, String creditUsageType, Double averageDailyBalance) {
        this.creditId = creditId;
        this.productId = productId;
        this.creditUsageType = creditUsageType;
        this.averageDailyBalance = averageDailyBalance;
    }
}