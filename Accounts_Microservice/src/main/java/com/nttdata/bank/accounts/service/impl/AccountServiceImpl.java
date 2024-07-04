package com.nttdata.bank.accounts.service.impl;

import com.nttdata.bank.accounts.domain.Account;
import com.nttdata.bank.accounts.domain.Balance;
import com.nttdata.bank.accounts.repository.AccountRepository;
import com.nttdata.bank.accounts.service.AccountService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Mono<Account> save(Mono<Account> account) {
        return account
                .flatMap(acc -> {
                    String accountUsageType = acc.getAccountUsageType();

                    return Mono.just(accountUsageType)
                            .filter(type -> "empresarial".equals(type) || "personal".equals(type))
                            .switchIfEmpty(Mono.error(new RuntimeException("The type must be 'empresarial' or 'personal'")))
                            .then(accountRepository.findByCustomerId(acc.getCustomerId()).collectList())
                            .flatMap(existingAccounts -> validateAccount(acc, existingAccounts))
                            .then(accountRepository.save(acc));
                });
    }

    private Mono<Void> validateAccount(Account acc, List<Account> existingAccounts) {
        String accountUsageType = acc.getAccountUsageType();

        if ("personal".equals(accountUsageType)) {
            long count = existingAccounts.stream()
                    .filter(a -> "ahorro".equals(a.getAccountType()) ||
                            "corriente".equals(a.getAccountType()) ||
                            "plazo_fijo".equals(a.getAccountType()))
                    .count();
            if (count >= 1) {
                return Mono.error(new RuntimeException("A personal client can only have a maximum of one savings, checking, or fixed-term account"));
            }
        } else if ("empresarial".equals(accountUsageType)) {
            boolean hasForbiddenAccountType = existingAccounts.stream()
                    .anyMatch(a -> "ahorro".equals(a.getAccountType()) ||
                            "plazo_fijo".equals(a.getAccountType()));
            if (hasForbiddenAccountType) {
                return Mono.error(new RuntimeException("A business client cannot have a savings or fixed-term account."));
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<Account> findById(String id) {
        return accountRepository.findById(id);
    }

    @Override
    public Mono<Account> update(String id, Mono<Account> account) {
        return accountRepository.findById(id)
                .flatMap(a -> account)
                .doOnNext(e -> e.setId(id))
                .flatMap(accountRepository::save);
    }

    @Override
    public Mono<Void> delete(String id) {
        return accountRepository.findById(id)
                .flatMap(accountRepository::delete);
    }

    @Override
    public Mono<Balance> getBalance(String id) {
        return accountRepository.findById(id)
        .map(a -> {
           Balance b = new Balance();
           b.setCurrentBalance(a.getBalance());
           return b;
        });
    }
}
