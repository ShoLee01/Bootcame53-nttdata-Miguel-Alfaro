package com.nttdata.bank.accounts.repository;

import com.nttdata.bank.accounts.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction,String> {
    Flux<Transaction> findByAccountId(String accountId);
}
