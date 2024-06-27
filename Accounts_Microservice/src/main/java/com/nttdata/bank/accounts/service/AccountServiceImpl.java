package com.nttdata.bank.accounts.service;

import com.nttdata.bank.accounts.domain.Account;
import com.nttdata.bank.accounts.domain.Balance;
import com.nttdata.bank.accounts.repository.AccountRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@NoArgsConstructor
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Mono<Account> save(Mono<Account> account) {
        return account.flatMap(accountRepository::save);
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
                .flatMap(a -> accountRepository.delete(a));
    }

    @Override
    public Mono<Balance> getBalance(String id) {
        return accountRepository.findById(id)
        .map(a -> {
           Balance b = new Balance();
           b.setBalance(a.getBalance());
           return b;
        });
    }
}
