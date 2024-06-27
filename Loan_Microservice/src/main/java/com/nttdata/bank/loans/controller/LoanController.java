package com.nttdata.bank.loans.controller;

import com.nttdata.bank.loans.api.CreditsApi;
import com.nttdata.bank.loans.mapper.*;
import com.nttdata.bank.loans.model.*;
import com.nttdata.bank.loans.service.CreditService;
import com.nttdata.bank.loans.service.TransactionService;
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
public class LoanController implements CreditsApi {

    @Autowired
    private CreditService creditService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CreditMapper creditMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private PaymentMapper paymentMapper;


    @Override
    public Mono<ResponseEntity<Credit>> addCredit(Mono<Credit> credit, ServerWebExchange exchange) {
        return creditService.save(credit.map(creditMapper::toDomain))
                .map(creditMapper::toModel)
                .map(c -> {
                    return ResponseEntity.status(HttpStatus.CREATED).body(c);
                });
    }

    @Override
    public Mono<ResponseEntity<GetCreditBalance200Response>> getCreditBalance(String id, ServerWebExchange exchange) {
        return creditService.getBalance(id)
                .map(balanceMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Credit>> getCreditById(String id, ServerWebExchange exchange) {
        return creditService.findById(id)
                .map(creditMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<Transaction>>> getCreditTransactions(String id, ServerWebExchange exchange) {
        Flux<Transaction> transactionsFlux = transactionService.getTransactions(id)
                .map(transactionMapper::toModel);

        return Mono.just(ResponseEntity.ok(transactionsFlux))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<Credit>>> getCredits(ServerWebExchange exchange) {
        Flux<Credit> creditsFlux = creditService.getAllCredits()
                .map(creditMapper::toModel);

        return Mono.just(ResponseEntity.ok(creditsFlux))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Transaction>> makeCreditCharge(String id, Mono<MakeCreditChargeRequest> makeCreditChargeRequest, ServerWebExchange exchange) {
        return makeCreditChargeRequest
                .map(chargeMapper::toDomain)
                .flatMap(charge -> transactionService.charge(id, charge))
                .map(transactionMapper::toModel)
                .map(transaction -> ResponseEntity.status(HttpStatus.OK).body(transaction));
    }

    @Override
    public Mono<ResponseEntity<Transaction>> makeCreditPayment(String id, Mono<MakeCreditPaymentRequest> makeCreditPaymentRequest, ServerWebExchange exchange) {
        return makeCreditPaymentRequest
                .map(paymentMapper::toDomain)
                .flatMap(charge -> transactionService.payment(id, charge))
                .map(transactionMapper::toModel)
                .map(transaction -> ResponseEntity.status(HttpStatus.OK).body(transaction));
    }
}
