package com.nttdata.bank.accounts.controller;

import com.nttdata.bank.accounts.api.AccountsApi;
import com.nttdata.bank.accounts.mapper.*;
import com.nttdata.bank.accounts.model.*;
import com.nttdata.bank.accounts.service.AccountService;
import com.nttdata.bank.accounts.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController

public class AccountController implements AccountsApi {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final DepositMapper depositMapper;
    private final WithdrawMapper withdrawMapper;
    private final DailyBalanceSummaryMapper dailyBalanceSummaryMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper, BalanceMapper balanceMapper, TransactionService transactionService, TransactionMapper transactionMapper, DepositMapper depositMapper, WithdrawMapper withdrawMapper, DailyBalanceSummaryMapper dailyBalanceSummaryMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.depositMapper = depositMapper;
        this.withdrawMapper = withdrawMapper;
        this.dailyBalanceSummaryMapper = dailyBalanceSummaryMapper;
    }

    @Override
    public Mono<ResponseEntity<Account>> addAccount(Mono<Account> account, ServerWebExchange exchange) {
        return accountService.save(account.map(accountMapper::toDomain))
                .map(accountMapper::toModel)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c));
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
                .map(c ->ResponseEntity.status(HttpStatus.OK).body(c))
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

    @Override
    public Mono<ResponseEntity<GetAverageDailyBalance200Response>> getAverageDailyBalance(String customerId, ServerWebExchange exchange) {
        return transactionService.generateDailyBalanceSummary(customerId)
                .map(dailyBalanceSummaryMapper::toModel)
                .map(c -> ResponseEntity.status(HttpStatus.OK).body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
