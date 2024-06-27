package com.nttdata.bank.loans.repository;

import com.nttdata.bank.loans.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
}
