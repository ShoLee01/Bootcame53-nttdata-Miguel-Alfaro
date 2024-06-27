package com.nttdata.bank.accounts.controller;

import com.nttdata.bank.accounts.api.AccountsApi;
import com.nttdata.bank.accounts.exception.GlobalErrorHandler;
import com.nttdata.bank.accounts.mapper.*;
import com.nttdata.bank.accounts.model.*;
import com.nttdata.bank.accounts.service.AccountService;
import com.nttdata.bank.accounts.service.TransactionService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@NoArgsConstructor
public class AccountController implements AccountsApi {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private DepositMapper depositMapper;

    @Autowired
    private WithdrawMapper withdrawMapper;

    @Override
    public Mono<ResponseEntity<Account>> addAccount(Mono<Account> account, ServerWebExchange exchange) {
        return accountService.save(account.map(accountMapper::toDomain))
                .map(accountMapper::toModel)
                .map(c -> {
                    return ResponseEntity.status(HttpStatus.CREATED).body(c);
                });
    }

    @Override
    public Mono<ResponseEntity<Account>> getAccountById(String id, ServerWebExchange exchange) {
        return accountService.findById(id)
                .map(accountMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Account>> updateAccount(String id, Mono<Account> account, ServerWebExchange exchange) {
        return accountService.update(id, account.map(accountMapper::toDomain))
                .map(accountMapper::toModel)
                .map(c -> {
                    return ResponseEntity.status(HttpStatus.OK).body(c);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(String id, ServerWebExchange exchange) {
        return accountService.findById(id)
                .flatMap(c -> accountService.delete(id)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<GetAccountBalance200Response>> getAccountBalance(String id, ServerWebExchange exchange) {
        return accountService.getBalance(id)
                .map(balanceMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Transaction>> depositToAccount(String id, Mono<DepositToAccountRequest> depositToAccountRequest, ServerWebExchange exchange) {
        return depositToAccountRequest
                .map(depositMapper::toDomain)
                .flatMap(deposit -> transactionService.deposit(id, deposit))
                .map(transactionMapper::toModel)
                .map(transaction -> ResponseEntity.status(HttpStatus.OK).body(transaction));
    }

    @Override
    public Mono<ResponseEntity<Transaction>> withdrawFromAccount(String id, Mono<WithdrawFromAccountRequest> withdrawFromAccountRequest, ServerWebExchange exchange) {
        return withdrawFromAccountRequest
                .map(withdrawMapper::toDomain)
                .flatMap(withdraw -> transactionService.withdraw(id, withdraw))
                .map(transactionMapper::toModel)
                .map(transaction -> ResponseEntity.status(HttpStatus.OK).body(transaction));
    }

    @Override
    public Mono<ResponseEntity<Flux<Transaction>>> getAccountTransactions(String id, ServerWebExchange exchange) {
        Flux<Transaction> creditsFlux = transactionService.getTransactions(id)
                .map(transactionMapper::toModel);

        return Mono.just(ResponseEntity.ok(creditsFlux))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
