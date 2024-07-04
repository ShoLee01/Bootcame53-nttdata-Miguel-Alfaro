package com.nttdata.bank.loans.service;

import com.nttdata.bank.loans.domain.CommissionReport;
import com.nttdata.bank.loans.domain.DailyBalanceSummary;
import com.nttdata.bank.loans.domain.Operation;
import com.nttdata.bank.loans.domain.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transaction> payment(String credit_id, Operation operation);
    Mono<Transaction> charge(String credit_id, Operation operation);
    Flux<Transaction> getTransactions(String credit_id);
    Flux<DailyBalanceSummary> generateDailyBalanceSummary (String customerId);
    Flux<CommissionReport> generateCommissionReport(String startDateStr, String endDateStr);
}
