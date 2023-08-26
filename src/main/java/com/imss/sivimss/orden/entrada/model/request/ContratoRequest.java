package com.imss.sivimss.orden.entrada.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ContratoRequest {
	
	@JsonProperty
	private Integer idContrato;
	@JsonProperty
	private String numContrato;
	@JsonProperty
	private Integer idArticulo;
	@JsonProperty
	private Integer idCategoriaArticulo;

}
