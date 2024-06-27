package com.nttdata.bank.loans.service;

import com.nttdata.bank.loans.domain.Balance;
import com.nttdata.bank.loans.domain.Credit;
import com.nttdata.bank.loans.repository.CreditRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@NoArgsConstructor
public class CreditServiceImpl  implements CreditService {

    @Autowired
    private CreditRepository creditRepository;

    @Override
    public Mono<Credit> save(Mono<Credit> credit) {
        return credit.flatMap(creditRepository::save);
    }

    @Override
    public Mono<Credit> findById(String id) {
        return creditRepository.findById(id);
    }

    @Override
    public Flux<Credit> getAllCredits() {
        return creditRepository.findAll();
    }

    @Override
    public Mono<Balance> getBalance(String id) {
        return creditRepository.findById(id)
                .map(a -> {
                    Balance b = new Balance();
                    b.setBalance(a.getBalance());
                    return b;
                });
    }
}
