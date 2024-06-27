package com.nttdata.bank.accounts.repository;

import com.nttdata.bank.accounts.domain.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
}
