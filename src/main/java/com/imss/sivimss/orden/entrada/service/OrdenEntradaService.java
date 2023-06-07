package com.imss.sivimss.orden.entrada.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.Response;

public interface OrdenEntradaService {
	
	Response<Object> consultarOrdenEntrada(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> ultimoRegistroOrdenEntrada(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> consultarContratoProveedor(DatosRequest request, Authentication authentication) throws IOException ;
	
	Response<Object> consultarContratoArticulo(DatosRequest request, Authentication authentication) throws IOException ;
	
	Response<Object> consultarDescripcionVelatorio(DatosRequest request, Authentication authentication) throws IOException ;
	
	Response<Object> insertarOrdenEntrada(DatosRequest request, Authentication authentication)throws IOException;

}
