package com.nttdata.bank.loans;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CreditMicroserviceApplicationTests {

	private final ApplicationContext applicationContext;

	CreditMicroserviceApplicationTests(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "El contexto de la aplicación no debe ser nulo");
	}

}
