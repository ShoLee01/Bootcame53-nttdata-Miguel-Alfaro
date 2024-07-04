package com.nttdata.bank.loans.service.impl;

import com.nttdata.bank.loans.domain.*;
import com.nttdata.bank.loans.repository.CreditRepository;
import com.nttdata.bank.loans.repository.TransactionRepository;
import com.nttdata.bank.loans.service.TransactionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final CreditRepository creditRepository;
    private final TransactionRepository transactionRepository;

    private static final double CHARGE_AMOUNT = 5.0;
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

    public TransactionServiceImpl(CreditRepository creditRepository, TransactionRepository transactionRepository) {
        this.creditRepository = creditRepository;
        this.transactionRepository = transactionRepository;
    }

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
                                transaction.setCurrentBalance(credit.getBalance() + operation.getAmount());
                                transaction.setCommission(0.0);
                                transaction.setProductId(credit.getProductId());
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
                    if (operation.getAmount() > credit.getBalance() || operation.getAmount() + CHARGE_AMOUNT > credit.getBalance()) {
                        return Mono.error(new RuntimeException("Charge amount exceeds available balance"));
                    }

                    credit.setBalance(credit.getBalance() - (operation.getAmount() + CHARGE_AMOUNT));
                    return creditRepository.save(credit)
                            .then(Mono.defer(() -> {
                                Transaction transaction = new Transaction();
                                transaction.setCreditId(credit_id);
                                transaction.setAmount(operation.getAmount());
                                transaction.setDate(new Date());
                                transaction.setType("charge");
                                transaction.setCurrentBalance(credit.getBalance());
                                transaction.setCommission(CHARGE_AMOUNT);
                                transaction.setProductId(credit.getProductId());
                                transaction.setDescription(operation.getDescription());
                                return transactionRepository.save(transaction);
                            }));
                });
    }

    @Override
    public Flux<Transaction> getTransactions(String credit_id) {
        return transactionRepository.findAll();
    }

    @Override
    public Flux<DailyBalanceSummary> generateDailyBalanceSummary (String customerId) {
        // Obtener el primer día del mes en curso
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        // Encontrar todas las cuentas del cliente y calcular el saldo promedio diario para cada una
        return creditRepository.findByCustomerId(customerId)
                .flatMap(credit -> calculateAverageDailyBalance(credit, startOfMonth, endOfMonth));
    }

    private Flux<DailyBalanceSummary> calculateAverageDailyBalance(Credit account, LocalDate startOfMonth, LocalDate endOfMonth) {
        // Convertir las fechas de inicio y fin del mes a objetos Date
        Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Encontrar todas las transacciones de la cuenta dentro del rango de fechas
        return transactionRepository.findByCreditIdAndDateBetween(account.getId(), startDate, endDate)
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
                    DailyBalanceSummary summary = new DailyBalanceSummary(account.getId(), account.getProductId() ,account.getCreditUsageType(), roundedAverage.doubleValue());
                    return Flux.just(summary);
                });
    }

    @Override
    public Flux<CommissionReport> generateCommissionReport(String startDateStr, String endDateStr) {
        return validateAndParseDate(startDateStr)
                .zipWith(validateAndParseDate(endDateStr))
                .flatMapMany(dates -> {
                    // De la tupla 1
                    Date startDate = dates.getT1();
                    // De la tupla 2
                    Date endDate = adjustEndDate(dates.getT2());

                    // Validar que la fecha de inicio no sea mayor que la fecha de fin
                    if (startDate.after(endDate)) {
                        return Flux.error(new IllegalArgumentException("The start date cannot be later than the end date."));
                    }

                    // Obtener las transacciones en el rango de fechas
                    return transactionRepository.findByDateBetween(startDate, endDate)
                            .filter(transaction -> transaction.getCommission() != null)
                            .groupBy(Transaction::getProductId) // Agrupar por producto
                            .flatMap(groupedFlux -> groupedFlux
                                    .collectList()
                                    .map(transactions -> {
                                        String productId = groupedFlux.key();
                                        Double totalCommission = transactions.stream()
                                                .mapToDouble(Transaction::getCommission)
                                                .sum();
                                        return new CommissionReport(productId, totalCommission);
                                    })
                            );
                });
    }

    private Mono<Date> validateAndParseDate(String dateStr) {
        // Validar el formato de la fecha
        if (!DATE_PATTERN.matcher(dateStr).matches()) {
            return Mono.error(new IllegalArgumentException("The dates must be in the format DD/MM/YYYY"));
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = dateFormat.parse(dateStr);
            return Mono.just(date);
        } catch (ParseException e) {
            return Mono.error(new IllegalArgumentException("Error parsing the dates"));
        }
    }
    // Par incluir a la misma fecha limite que se esta buscado por ejmplo si se busca hasta el 5 de agosto que tambien te salgan todas las de 5 de ese dia
    private Date adjustEndDate(Date endDate) {
        LocalDateTime endDateTime = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        endDateTime = endDateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
