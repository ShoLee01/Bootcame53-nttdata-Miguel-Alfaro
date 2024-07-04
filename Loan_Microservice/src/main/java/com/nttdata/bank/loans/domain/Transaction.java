package com.nttdata.bank.loans.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "transaction")
public class Transaction {
    @Id
    private String id;
    private String creditId;
    private Double amount;
    private Date date;
    private String type;
    private Double currentBalance;
    private Double commission;
    private String productId;
    private String description;
}
