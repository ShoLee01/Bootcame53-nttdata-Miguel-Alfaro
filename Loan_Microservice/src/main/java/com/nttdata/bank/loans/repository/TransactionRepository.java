package com.nttdata.bank.loans.repository;

import com.nttdata.bank.loans.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Date;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findByDateBetween(Date from, Date to);
    Flux<Transaction> findByCreditIdAndDateBetween(String creditId, Date from, Date to);
}
