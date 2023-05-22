package com.imss.sivimss.ordenEntrada;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.orden.entrada.OrdenEntradaApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ArquetipoApplicationTests {

	@Test
	void contextLoads() {
		String result="test";
		OrdenEntradaApplication.main(new String[]{});
		assertNotNull(result);
	}

}
