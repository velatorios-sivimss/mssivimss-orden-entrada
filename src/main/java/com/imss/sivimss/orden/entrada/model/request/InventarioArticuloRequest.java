package com.imss.sivimss.orden.entrada.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InventarioArticuloRequest {
	
	@JsonProperty
	private Integer idInventarioArticulo;
	@JsonProperty
	private String numFolioArticulo;
	@JsonProperty
	private String desMotivoDevolucion;

}
