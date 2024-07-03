package com.nttdata.bank.accounts.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyBalanceSummary {
    private String accountId;
    private String accountType;
    private String accountUsageType;
    private Double averageDailyBalance;

    public DailyBalanceSummary(String accountId, String accountType, String accountUsageType, Double averageDailyBalance) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.accountUsageType = accountUsageType;
        this.averageDailyBalance = averageDailyBalance;
    }
}
