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
@Document(collection = "credit")
public class Credit {
    @Id
    private String id;
    private String customerId;
    private String productId;
    private String creditUsageType;
    private Double creditLimit;
    private Double balance;
    private Double interestRate;
    private Date dueDate;
    private Date createdAt;
}
