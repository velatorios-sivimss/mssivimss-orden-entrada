package com.imss.sivimss.orden.entrada.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ConsultaStockRequest{
	
	@JsonProperty
	private Integer idVelatorio;
	@JsonProperty
	private Integer idOrdenEntrada;
	@JsonProperty
	private Integer idCategoriaArticulo;
	@JsonProperty
	private Integer idTipoAsignacionArt;

}
