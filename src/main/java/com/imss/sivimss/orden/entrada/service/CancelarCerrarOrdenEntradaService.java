package com.imss.sivimss.orden.entrada.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.Response;

public interface CancelarCerrarOrdenEntradaService {
	
	Response<Object> consultarDetalleOrdenEntrada(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> actualizarOrdenEntrada(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> verificarOrdenEntradaRelacionOrdenServicio(DatosRequest request, Authentication authentication) throws IOException;

}
