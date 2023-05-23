package com.imss.sivimss.orden.entrada.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrdenEntradaRequest {
	
	@JsonProperty
	private Integer idOrdenEntrada;

}
