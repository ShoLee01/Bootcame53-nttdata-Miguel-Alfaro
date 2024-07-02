package com.nttdata.bank.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AccountMicroserviceApplicationTests {

    private final ApplicationContext applicationContext;

    AccountMicroserviceApplicationTests(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "El contexto de la aplicación no debe ser nulo");
    }
}
