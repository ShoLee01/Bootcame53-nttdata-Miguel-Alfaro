package com.nttdata.bank.loans.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommissionReport {
    private String productId;
    private Double totalCommission;

    public CommissionReport(String productId, Double totalCommission) {
        this.productId = productId;
        this.totalCommission = totalCommission;
    }
}
