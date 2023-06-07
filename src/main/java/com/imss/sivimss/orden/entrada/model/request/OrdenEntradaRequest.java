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
	private Integer numArticulo;
	@JsonProperty
	private String numFolioArticulo;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String fecIngreso;
	@JsonProperty
	private String nomProveedor;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String fechaInicio;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String fechaFin;

}
