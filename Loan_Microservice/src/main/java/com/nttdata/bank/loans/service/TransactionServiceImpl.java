package com.nttdata.bank.loans.service;

import com.nttdata.bank.loans.domain.Operation;
import com.nttdata.bank.loans.domain.Transaction;
import com.nttdata.bank.loans.repository.CreditRepository;
import com.nttdata.bank.loans.repository.TransactionRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@NoArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Mono<Transaction> payment(String credit_id, Operation operation) {
        return creditRepository.findById(credit_id)
                .switchIfEmpty(Mono.error(new RuntimeException("Credit not found")))
                .flatMap(credit -> {
                    Double debt =  credit.getCreditLimit() - credit.getBalance();
                    if(operation.getAmount() > debt) {
                        return Mono.error(new RuntimeException("Payment amount exceeds outstanding debt"));
                    }
                    credit.setBalance(credit.getBalance() + operation.getAmount());
                    return creditRepository.save(credit)
                            .then(Mono.defer(() -> {
                                Transaction transaction = new Transaction();
                                transaction.setCreditId(credit_id);
                                transaction.setAmount(operation.getAmount());
                                transaction.setDate(new Date());
                                transaction.setType("payment");
                                transaction.setDescription(operation.getDescription());
                                return transactionRepository.save(transaction);
                            }));
                });
    }

    @Override
    public Mono<Transaction> charge(String credit_id, Operation operation) {
        return creditRepository.findById(credit_id)
                .switchIfEmpty(Mono.error(new RuntimeException("Credit not found")))
                .flatMap(credit -> {
                    if (operation.getAmount() > credit.getBalance()) {
                        return Mono.error(new RuntimeException("Charge amount exceeds available balance"));
                    }
                    credit.setBalance(credit.getBalance() - operation.getAmount());
                    return creditRepository.save(credit)
                            .then(Mono.defer(() -> {
                                Transaction transaction = new Transaction();
                                transaction.setCreditId(credit_id);
                                transaction.setAmount(operation.getAmount());
                                transaction.setDate(new Date());
                                transaction.setType("charge");
                                transaction.setDescription(operation.getDescription());
                                return transactionRepository.save(transaction);
                            }));
                });
    }

    @Override
    public Flux<Transaction> getTransactions(String credit_id) {
        return transactionRepository.findAll();
    }
}
