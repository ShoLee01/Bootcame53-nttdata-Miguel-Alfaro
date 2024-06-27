package com.nttdata.bank.accounts.repository;

import com.nttdata.bank.accounts.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction,String> {
}
