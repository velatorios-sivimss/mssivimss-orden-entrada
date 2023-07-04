package com.imss.sivimss.orden.entrada.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.Response;


public interface GeneraReporteService {
	
	Response<Object> generarDocumentoConsultaOrdenEntrada(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> generarDocumentoConsultaStock(DatosRequest request, Authentication authentication) throws IOException;

}
