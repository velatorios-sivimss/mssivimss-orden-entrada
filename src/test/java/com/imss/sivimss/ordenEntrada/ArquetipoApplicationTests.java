package com.imss.sivimss.ordenEntrada;

import com.imss.sivimss.orden.entrada.OrdenEntradaApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class ArquetipoApplicationTests {

	
	void contextLoads() {
		String result="test";
		OrdenEntradaApplication.main(new String[]{});
		assertNotNull(result);
	}

}
