package com.nttdata.bank.accounts.service;

import com.nttdata.bank.accounts.domain.DailyBalanceSummary;
import com.nttdata.bank.accounts.domain.Operation;
import com.nttdata.bank.accounts.domain.Transaction;
import com.nttdata.bank.accounts.repository.AccountRepository;
import com.nttdata.bank.accounts.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionServiceImpl implements TransactionService{


    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private static final double CHARGE_AMOUNT = 5.0;

    public TransactionServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Mono<Transaction> deposit(String accountId, Operation operation) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .flatMap(account -> transactionRepository.findByAccountId(accountId).count()
                        .flatMap(transactionCount -> {
                            double[] depositAmount = {operation.getAmount()};

                            if (depositAmount[0] < CHARGE_AMOUNT) {
                                return Mono.error(new RuntimeException("The deposit cannot be less than " + CHARGE_AMOUNT + " soles"));
                            }

                            if (transactionCount >= account.getMovementLimit()) {
                                depositAmount[0] -= CHARGE_AMOUNT;
                            }
                            account.setBalance(account.getBalance() + depositAmount[0]);
                            return accountRepository.save(account)
                                    .then(Mono.defer(() -> {
                                        Transaction transaction = new Transaction();
                                        transaction.setAccountId(accountId);
                                        transaction.setAmount(depositAmount[0]);
                                        transaction.setDate(new Date());
                                        transaction.setCurrentBalance(account.getBalance() + depositAmount[0]);
                                        transaction.setType("deposit");
                                        transaction.setDescription(operation.getDescription());
                                        return transactionRepository.save(transaction);
                                    }));
                        }));
    }

    @Override
    public Mono<Transaction> withdraw(String accountId, Operation operation) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .flatMap(account -> transactionRepository.findByAccountId(accountId).count()
                        .flatMap(transactionCount -> {
                            double[] withdrawAmount = {operation.getAmount()};

                            if (withdrawAmount[0] < CHARGE_AMOUNT) {
                                return Mono.error(new RuntimeException("The withdrawal amount cannot be less than " + CHARGE_AMOUNT + " soles"));
                            }


                            if (transactionCount >= account.getMovementLimit()) {
                                withdrawAmount[0] += CHARGE_AMOUNT;
                            }

                            if (account.getBalance() < withdrawAmount[0]) {
                                return Mono.error(new RuntimeException("You do not have enough funds"));
                            }
                            account.setBalance(account.getBalance() - withdrawAmount[0]);
                            return accountRepository.save(account)
                                    .then(Mono.defer(() -> {
                                        Transaction transaction = new Transaction();
                                        transaction.setAccountId(accountId);
                                        transaction.setAmount(operation.getAmount());
                                        transaction.setDate(new Date());
                                        transaction.setCurrentBalance(account.getBalance());
                                        transaction.setType("withdraw");
                                        transaction.setDescription(operation.getDescription());
                                        return transactionRepository.save(transaction);
                                    }));
                        })
                );
    }

    @Override
    public Flux<Transaction> getTransactions(String accountId) {
        return transactionRepository.findAll();
    }

    @Override
    public Mono<DailyBalanceSummary> generateDailyBalanceSummary(String customerId) {
        return accountRepository.findByCustomerId(customerId)
                .collectList()
                .flatMapMany(accounts -> Flux.fromIterable(accounts)
                        .flatMap(account -> transactionRepository.findByAccountId(account.getId())))
                .collectList()
                .flatMap(this::calculateDailyAverages)
                .map(averageBalance -> new DailyBalanceSummary(customerId, averageBalance));
    }

    private Mono<Double> calculateDailyAverages(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return Mono.just(0.0);
        }

        // Log the transactions to check for null dates
        transactions.forEach(transaction -> {
            System.out.println("Transaction ID: " + transaction.getId() + ", Date: " + transaction.getDate());
        });
        transactions.sort(Comparator.comparing(Transaction::getDate));
        LocalDate startDate = transactions.get(0).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        Map<LocalDate, Double> dailyBalances = new LinkedHashMap<>();
        AtomicReference<Double> previousBalance = new AtomicReference<>(transactions.get(0).getCurrentBalance());
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            double finalPreviousBalance = previousBalance.get();
            LocalDate finalDate = date;
            transactions.stream()
                    .filter(t -> t.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(finalDate))
                    .forEach(t -> previousBalance.set(t.getCurrentBalance()));
            dailyBalances.put(date, finalPreviousBalance);
        }

        double totalBalance = dailyBalances.values().stream().mapToDouble(Double::doubleValue).sum();
        double averageBalance = totalBalance / dailyBalances.size();

        return Mono.just(averageBalance);
    }
}
