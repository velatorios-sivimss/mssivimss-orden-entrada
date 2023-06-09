package com.imss.sivimss.orden.entrada.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrdenEntradaRequest {
	
	@JsonProperty
	private Integer idOrdenEntrada;
	@JsonProperty
	private String numFolioOrdenEntrada;
	@JsonProperty
	private Integer idContrato;
	@JsonProperty
	private Integer idArticulo;
	@JsonProperty
	private Integer idInventarioArticulo;
	@JsonProperty
	private Integer numArticulo;
	@JsonProperty
	private String numFolioArticulo;
	@JsonProperty
	private Integer folioProveedor;
	@JsonProperty
	private String desModeloArticulo;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String fecIngreso;
	@JsonProperty
	private String nomProveedor;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String fechaInicio;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String fechaFin;

}
