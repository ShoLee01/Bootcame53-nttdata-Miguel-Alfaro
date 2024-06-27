package com.nttdata.bank.loans.service;

import com.nttdata.bank.loans.domain.Balance;
import com.nttdata.bank.loans.domain.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditService {
    Mono<Credit> save(Mono<Credit> credit);
    Mono<Credit> findById(String id);
    Flux<Credit> getAllCredits();
    Mono<Balance> getBalance(String id);
}
