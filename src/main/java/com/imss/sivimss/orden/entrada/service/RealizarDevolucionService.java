package com.imss.sivimss.orden.entrada.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.Response;

public interface RealizarDevolucionService {
	
	Response<Object> consultarFolioArticulo(DatosRequest request, Authentication authentication)throws IOException;
	
	Response<Object> actualizarInventarioArticulo(DatosRequest request, Authentication authentication) throws IOException;

}
