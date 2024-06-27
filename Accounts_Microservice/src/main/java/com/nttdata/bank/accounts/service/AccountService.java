package com.nttdata.bank.accounts.service;

import com.nttdata.bank.accounts.domain.Account;
import com.nttdata.bank.accounts.domain.Balance;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<Account> save(Mono<Account> account);
    Mono<Account> findById(String id);
    Mono<Account> update(String id, Mono<Account> account);
    Mono<Void> delete(String id);
    Mono<Balance> getBalance(String id);
}
