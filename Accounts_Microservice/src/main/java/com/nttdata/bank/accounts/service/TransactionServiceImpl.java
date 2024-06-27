package com.nttdata.bank.accounts.service;

import com.nttdata.bank.accounts.domain.Operation;
import com.nttdata.bank.accounts.domain.Transaction;
import com.nttdata.bank.accounts.repository.AccountRepository;
import com.nttdata.bank.accounts.repository.TransactionRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Date;

@Service
@NoArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    public Mono<Transaction> deposit(String accountId, Operation operation) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .flatMap(account -> {
                    account.setBalance(account.getBalance() + operation.getAmount());
                    return accountRepository.save(account)
                            .then(Mono.defer(() -> {
                                Transaction transaction = new Transaction();
                                transaction.setAccountId(accountId);
                                transaction.setAmount(operation.getAmount());
                                transaction.setDate(new Date());
                                transaction.setType("deposit");
                                transaction.setDescription(operation.getDescription());
                                return transactionRepository.save(transaction);
                            }));
                });
    }

    @Override
    public Mono<Transaction> withdraw(String accountId, Operation operation) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .flatMap(account -> {
                    if(account.getBalance() < operation.getAmount()) {
                        return Mono.error(new RuntimeException("You do not have enough funds"));
                    }
                    account.setBalance(account.getBalance() - operation.getAmount());
                    return accountRepository.save(account)
                            .then(Mono.defer(() -> {
                                Transaction transaction = new Transaction();
                                transaction.setAccountId(accountId);
                                transaction.setAmount(operation.getAmount());
                                transaction.setDate(new Date());
                                transaction.setType("withdraw");
                                transaction.setDescription(operation.getDescription());
                                return transactionRepository.save(transaction);
                            }));
                });
    }

    @Override
    public Flux<Transaction> getTransactions(String accountId) {
        return transactionRepository.findAll();
    }
}
