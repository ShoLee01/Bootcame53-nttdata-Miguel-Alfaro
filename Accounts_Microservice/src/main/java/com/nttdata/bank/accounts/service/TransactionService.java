package com.nttdata.bank.accounts.service;

import com.nttdata.bank.accounts.domain.DailyBalanceSummary;
import com.nttdata.bank.accounts.domain.Operation;
import com.nttdata.bank.accounts.domain.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transaction> deposit(String accountId, Operation operation);
    Mono<Transaction> withdraw(String accountId, Operation operation);
    Flux<Transaction> getTransactions(String accountId);
    Flux<DailyBalanceSummary> generateDailyBalanceSummary(String customerId);
}
