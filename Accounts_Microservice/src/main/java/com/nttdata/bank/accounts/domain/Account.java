package com.nttdata.bank.accounts.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "account")
public class Account {
    @Id
    private String id;
    @NotNull
    private String customerId;
    private String accountType;
    private Double balance;
    private Double maintenanceFee;
    private Integer movementLimit;
    @CreatedDate
    private Date fixedTermDate;
    private Flux<String> holders = Flux.empty();
    private Flux<String> authorizedSigners = Flux.empty();
    private Date createdAt;
}
