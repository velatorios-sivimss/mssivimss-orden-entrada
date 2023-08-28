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
	private String idTipoAsignacionArt;
	@JsonProperty
	private String tipoReporte;
	private Double version =5.2D;

}
