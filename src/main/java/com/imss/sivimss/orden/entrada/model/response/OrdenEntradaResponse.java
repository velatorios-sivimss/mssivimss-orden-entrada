package com.imss.sivimss.orden.entrada.model.response;

import lombok.Data;

@Data
public class OrdenEntradaResponse {
	
	private Integer idOrdenEntrada;
	
	private String numFolio;
	
	private Integer idArticulo;
	
	private Integer idInventarioArticulo;
	
	private Integer cantidadUnidadArticulo;
	
	private Integer cantidadInventarioArticulo;
	
	private Integer numArticulo;

}
