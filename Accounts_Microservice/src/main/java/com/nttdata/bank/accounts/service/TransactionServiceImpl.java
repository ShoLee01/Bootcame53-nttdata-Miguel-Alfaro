package com.nttdata.bank.accounts.service;

import com.nttdata.bank.accounts.domain.Account;
import com.nttdata.bank.accounts.domain.DailyBalanceSummary;
import com.nttdata.bank.accounts.domain.Operation;
import com.nttdata.bank.accounts.domain.Transaction;
import com.nttdata.bank.accounts.repository.AccountRepository;
import com.nttdata.bank.accounts.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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
                            // Almacenamos el withdrawAmount que se descontará de la cuenta
                            double[] withdrawAmount = {operation.getAmount()};

                            // Vemos si tiene el saldo disponible
                            // Vemos si se supero el límite se transacciones y tiene suficiente saldo para pagar la comisióm
                            if (account.getBalance() < withdrawAmount[0] ||
                                    transactionCount >= account.getMovementLimit() &&
                                            withdrawAmount[0] + CHARGE_AMOUNT > account.getBalance()) {
                                return Mono.error(new RuntimeException("You do not have enough funds"));
                            }

                            // Se le agrega al retiro la comisión
                            if (transactionCount >= account.getMovementLimit()) {
                                withdrawAmount[0] += CHARGE_AMOUNT;
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
    public Flux<DailyBalanceSummary> generateDailyBalanceSummary(String customerId) {
        // Obtener el primer día del mes en curso
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        // Encontrar todas las cuentas del cliente y calcular el saldo promedio diario para cada una
        return accountRepository.findByCustomerId(customerId)
                .flatMap(account -> calculateAverageDailyBalance(account, startOfMonth, endOfMonth));
    }

    private Flux<DailyBalanceSummary> calculateAverageDailyBalance(Account account, LocalDate startOfMonth, LocalDate endOfMonth) {
        // Convertir las fechas de inicio y fin del mes a objetos Date
        Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Encontrar todas las transacciones de la cuenta dentro del rango de fechas
        return transactionRepository.findByAccountIdAndDateBetween(account.getId(), startDate, endDate)
                .collectList()
                .flatMapMany(transactions -> {
                    // Mapa para almacenar los saldos diarios
                    Map<LocalDate, Double> dailyBalances = new HashMap<>();
                    // Obtener el saldo actual de la cuenta
                    Double currentBalance = account.getBalance();
                    // Fecha actual comenzando desde el último día del mes
                    LocalDate currentDate = endOfMonth;

                    // Añadir el saldo actual al mapa de saldos diarios
                    dailyBalances.put(currentDate, currentBalance);

                    // Iterar sobre las transacciones para actualizar los saldos diarios
                    for (Transaction transaction : transactions) {
                        LocalDate transactionDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        while (!currentDate.isEqual(transactionDate)) {
                            dailyBalances.put(currentDate, currentBalance);
                            currentDate = currentDate.minusDays(1);
                        }
                        currentBalance = transaction.getCurrentBalance();
                    }

                    // Rellenar los días restantes si es necesario
                    while (!currentDate.isBefore(startOfMonth)) {
                        dailyBalances.put(currentDate, currentBalance);
                        currentDate = currentDate.minusDays(1);
                    }

                    // Calcular el saldo promedio diario
                    double sumBalances = dailyBalances.values().stream().mapToDouble(Double::doubleValue).sum();
                    double averageDailyBalance = sumBalances / dailyBalances.size();
                    // Redondear a dos decimales
                    BigDecimal roundedAverage = BigDecimal.valueOf(averageDailyBalance).setScale(2, RoundingMode.HALF_UP);

                    // Crear el resumen para esta cuenta
                    DailyBalanceSummary summary = new DailyBalanceSummary(account.getId(), account.getAccountType() ,account.getAccountUsageType(), roundedAverage.doubleValue());
                    return Flux.just(summary);
                });
    }

}
