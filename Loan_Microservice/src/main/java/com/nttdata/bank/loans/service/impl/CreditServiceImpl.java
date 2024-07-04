package com.nttdata.bank.loans.service.impl;

import com.nttdata.bank.loans.domain.Balance;
import com.nttdata.bank.loans.domain.Credit;
import com.nttdata.bank.loans.repository.CreditRepository;
import com.nttdata.bank.loans.service.CreditService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CreditServiceImpl  implements CreditService {

    private final CreditRepository creditRepository;

    public CreditServiceImpl(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    @Override
    public Mono<Credit> save(Mono<Credit> credit) {
        return credit
                .flatMap(cred -> {
                    String creditUsageType = cred.getCreditUsageType();

                    return Mono.just(creditUsageType)
                            .filter(type -> "empresarial".equals(type) || "personal".equals(type))
                            .switchIfEmpty(Mono.error(new RuntimeException("The type must be 'empresarial' or 'personal'")))
                            .then(creditRepository.findByCustomerId(cred.getCustomerId()).collectList())
                            .flatMap(existingCredits -> validateCredit(cred, existingCredits))
                            .then(creditRepository.save(cred));
                });
    }

    private Mono<Void> validateCredit(Credit cred, List<Credit> existingCredits) {
        String creditUsageType = cred.getCreditUsageType();

        if ("personal".equals(creditUsageType)) {
            long count = existingCredits.stream()
                    .filter(c -> "personal".equals(c.getCreditUsageType()))
                    .count();
            if (count >= 1) {
                return Mono.error(new RuntimeException("A personal client can only have a single loan"));
            }
        }
        return Mono.empty();
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
