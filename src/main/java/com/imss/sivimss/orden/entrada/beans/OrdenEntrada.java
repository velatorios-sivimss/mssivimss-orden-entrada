package com.imss.sivimss.orden.entrada.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;


import com.imss.sivimss.orden.entrada.model.request.ContratoRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.SelectQueryUtil;

public class OrdenEntrada {
	
	public DatosRequest ultimoRegistroOrdenEntrada(DatosRequest request) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("MAX(OE.ID_ODE) + 1 AS ID_ODE").from("SVT_ORDEN_ENTRADA OE");
		final String query = queryUtil.build();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest consultarContratoProveedor(DatosRequest request, ContratoRequest contrato) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("P.ID_PROVEEDOR AS folioProveedor","P.NOM_PROVEEDOR AS nomProveedor").from("SVT_PROVEEDOR P")
		.innerJoin("SVT_CONTRATO C", "P.ID_PROVEEDOR = C.ID_PROVEEDOR").and("P.IND_ACTIVO = 1")
		.where("C.ID_CONTRATO = :idContrato").setParameter("idContrato", contrato.getIdContrato());
		final String query = queryUtil.build();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest consultarContratoArticulo(DatosRequest request, ContratoRequest contrato) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCA.DES_CATEGORIA_ARTICULO AS DES_CATEGORIA_ARTICULO","A.DES_MODELO_ARTICULO AS DES_MODELO_ARTICULO",
				"CA.MON_COSTO_UNITARIO as MON_COSTO_UNITARIO","CA.MON_PRECIO AS MON_PRECIO")
		.from("SVT_CONTRATO C")
		.innerJoin("SVT_CONTRATO_ARTICULOS CA", "C.ID_CONTRATO = CA.ID_CONTRATO")
		.innerJoin("SVT_ARTICULO A", "CA.ID_ARTICULO = A.ID_ARTICULO")
		.innerJoin("SVC_CATEGORIA_ARTICULO SCA", "A.ID_CATEGORIA_ARTICULO = SCA.ID_CATEGORIA_ARTICULO")
		.where("C.ID_CONTRATO = :idContrato").setParameter("idContrato", contrato.getIdContrato());
		final String query = queryUtil.build();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
		
	}
	
	public DatosRequest consultarDescripcionVelatorio(DatosRequest request, UsuarioDto usuarioDto) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("V.DES_VELATORIO").from("SVC_VELATORIO V")
		.where("V.ID_VELATORIO = :idVelatorio").setParameter("idVelatorio", usuarioDto.getIdVelatorio());
		final String query = queryUtil.build();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest insertarOrdenEntrada() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		return request;
	}

}
