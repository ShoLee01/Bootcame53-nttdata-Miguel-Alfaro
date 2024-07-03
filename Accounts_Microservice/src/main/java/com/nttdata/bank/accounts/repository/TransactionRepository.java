package com.nttdata.bank.accounts.repository;

import com.nttdata.bank.accounts.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Date;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction,String> {
    Flux<Transaction> findByAccountId(String accountId);
    Flux<Transaction> findByAccountIdAndDateBetween(String accountId, Date from, Date to);
}
