package com.imss.sivimss.orden.entrada.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.Response;

public interface ConsultaStockService {
	
	Response<Object> consultarOrdenEntradaPorVelatorio(DatosRequest request, Authentication authentication)throws IOException;
	
	Response<Object> consultarDescripcionCategoria(DatosRequest request, Authentication authentication)throws IOException;
	
	Response<Object> consultarTipoAsignacionArticulo(DatosRequest request, Authentication authentication)throws IOException;
	
	Response<Object> consultarStock(DatosRequest request, Authentication authentication) throws IOException ;

}
