package com.evidenlibrary.backend.apirest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Verificar que el contexto de Spring se carga correctamente
		assertNotNull(applicationContext);
	}
	
	@Test
	void mainMethodStartsApplication() {
		// Prueba que el método main se ejecuta sin errores
		BackendApplication.main(new String[] {});
	}
}
