package com.nttdata.bank.loans.repository;

import com.nttdata.bank.loans.domain.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
}