package com.nttdata.bank.loans.controller;

import com.nttdata.bank.loans.api.CreditsApi;
import com.nttdata.bank.loans.mapper.*;
import com.nttdata.bank.loans.model.*;
import com.nttdata.bank.loans.service.CreditService;
import com.nttdata.bank.loans.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class LoanController implements CreditsApi {

    private final CreditService creditService;
    private final TransactionService transactionService;
    private final CreditMapper creditMapper;
    private final TransactionMapper transactionMapper;
    private final ChargeMapper chargeMapper;
    private final BalanceMapper balanceMapper;
    private final PaymentMapper paymentMapper;
    private final DailyBalanceSummaryMapper dailyBalanceSummaryMapper;
    private final CommissionReportMapper commissionReportMapper;

    public LoanController(CreditService creditService, TransactionService transactionService, CreditMapper creditMapper,
                          TransactionMapper transactionMapper, ChargeMapper chargeMapper, BalanceMapper balanceMapper,
                          PaymentMapper paymentMapper, DailyBalanceSummaryMapper dailyBalanceSummaryMapper,
                          CommissionReportMapper commissionReportMapper) {
        this.creditService = creditService;
        this.transactionService = transactionService;
        this.creditMapper = creditMapper;
        this.transactionMapper = transactionMapper;
        this.chargeMapper = chargeMapper;
        this.balanceMapper = balanceMapper;
        this.paymentMapper = paymentMapper;
        this.dailyBalanceSummaryMapper = dailyBalanceSummaryMapper;
        this.commissionReportMapper = commissionReportMapper;
    }

    @Override
    public Mono<ResponseEntity<Credit>> addCredit(Mono<Credit> credit, ServerWebExchange exchange) {
        return creditService.save(credit.map(creditMapper::toDomain))
                .map(creditMapper::toModel)
                .map(c ->  ResponseEntity.status(HttpStatus.CREATED).body(c));
    }

    @Override
    public Mono<ResponseEntity<Flux<AverageDailyBalanceSummary>>> getAverageDailyBalance(String customerId, ServerWebExchange exchange) {
        Flux<AverageDailyBalanceSummary> creditsFlux = transactionService.generateDailyBalanceSummary(customerId)
                .map(dailyBalanceSummaryMapper::toModel);

        return Mono.just(ResponseEntity.ok(creditsFlux))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<CommissionReport>>> getCommissionReport(String startDate, String endDate, ServerWebExchange exchange) {
        Flux<CommissionReport> creditsFlux = transactionService.generateCommissionReport(startDate,endDate)
                .map(commissionReportMapper::toModel);

        return Mono.just(ResponseEntity.ok(creditsFlux))
                .defaultIfEmpty(ResponseEntity.notFound().build());
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
