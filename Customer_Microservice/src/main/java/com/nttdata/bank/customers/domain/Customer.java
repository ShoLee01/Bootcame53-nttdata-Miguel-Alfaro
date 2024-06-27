package com.nttdata.bank.customers.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "customer")
public class Customer {
    @Id
    private String id;
    private String names;
    private String surnames;
    private String type;
    @Indexed(unique = true)
    private String email;
    private String phone;
    private String address;
    @CreatedDate
    private Date createdAt;
}