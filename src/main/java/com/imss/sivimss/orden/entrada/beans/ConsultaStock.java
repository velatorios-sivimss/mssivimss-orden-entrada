package com.imss.sivimss.orden.entrada.beans;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.orden.entrada.model.request.CategoriaRequest;
import com.imss.sivimss.orden.entrada.model.request.ConsultaStockRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.ConsultaConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsultaStock {
	
	public DatosRequest consultarOrdenEntradaPorVelatorio(DatosRequest request, OrdenEntradaRequest ordenEntradaRequest) {
		log.info(" INICIO - consultarOrdenEntradaPorVelatorio");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SOE.ID_ODE AS ID_ODE","SOE.CVE_FOLIO AS NUM_FOLIO").from("SVT_ORDEN_ENTRADA SOE")
		.innerJoin("SVT_CONTRATO SC", "SC.ID_CONTRATO = SOE.ID_CONTRATO").where("SC.IND_ACTIVO = 1")
		.and("SOE.CVE_FOLIO LIKE'%"+ordenEntradaRequest.getNumFolioOrdenEntrada()+"%'")
		.and("SC.ID_VELATORIO = :idVelatorio").setParameter(ConsultaConstantes.ID_VELATORIO, ConsultaConstantes.getIdVelatorio(ordenEntradaRequest.getIdVelatorio()));
		final String query = queryUtil.build();
		log.info(" consultarOrdenEntradaPorVelatorio: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarOrdenEntradaPorVelatorio");
		return request;
	}
	
	public DatosRequest consultarProveedor(DatosRequest request, OrdenEntradaRequest ordenEntradaRequest) {
		log.info(" INICIO - consultarContratoProveedor");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil
				.select("SP.ID_PROVEEDOR AS FOLIO_PROVEEDOR","SP.NOM_PROVEEDOR AS NOM_PROVEEDOR")
				.from("SVT_PROVEEDOR SP")
				.where("SP.IND_ACTIVO = 1").and("SP.NOM_PROVEEDOR LIKE'%"+ordenEntradaRequest.getNomProveedor()+"%'");
		final String query = queryUtil.build();
		log.info(" consultarContratoProveedor: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarContratoProveedor");
		return request;
	}
	
	public DatosRequest consultarDescripcionCategoria(DatosRequest request, CategoriaRequest categoriaRequest) {
		log.info(" INICIO - consultarDescripcionCategoria");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DISTINCT SA.ID_CATEGORIA_ARTICULO AS ID_CATEGORIA_ARTICULO","SCA.DES_CATEGORIA_ARTICULO AS DES_CATEGORIA_ARTICULO").from("SVT_ARTICULO SA")
		.innerJoin("SVC_CATEGORIA_ARTICULO SCA", "SCA.ID_CATEGORIA_ARTICULO = SA.ID_CATEGORIA_ARTICULO").where("SA.IND_ACTIVO = 1")
		.and("SCA.DES_CATEGORIA_ARTICULO LIKE'%"+categoriaRequest.getDesCategoria()+"%'");
		final String query = queryUtil.build();
		log.info(" consultarDescripcionCategoria: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarDescripcionCategoria");
		return request;
	}
	
	public DatosRequest consultarTipoAsignacionArticulo(DatosRequest request) {
		log.info(" INICIO - consultarTipoAsignacionArticulo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("STAA.ID_TIPO_ASIGNACION_ART AS ID_TIPO_ASIGNACION_ART","STAA.DES_TIPO_ASIGNACION_ART AS DES_TIPO_ASIGNACION_ART").from("SVC_TIPO_ASIGNACION_ARTICULO STAA");
		final String query = queryUtil.build();
		log.info(" consultarTipoAsignacionArticulo: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarTipoAsignacionArticulo");
		return request;
	}
	
	public DatosRequest consultarStock(DatosRequest request, ConsultaStockRequest consultaStockRequest,  String formatoFecha) {
		log.info(" INICIO - consultarStock");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DATE_FORMAT(OE.FEC_INGRESO,'"+formatoFecha+"') AS FEC_ODE","OE.CVE_FOLIO AS NUM_FOLIO_ODE","SIA.CVE_FOLIO_ARTICULO AS FOLIO_ARTICULO","A.REF_MODELO_ARTICULO AS DES_MODELO_ARTICULO",
				"OE.ID_ESTATUS_ORDEN_ENTRADA AS ESTATUS_ORDEN_ENTRADA").from("SVT_INVENTARIO_ARTICULO SIA")
		.innerJoin("SVT_ORDEN_ENTRADA OE", "SIA.ID_ODE = OE.ID_ODE").and("SIA.IND_ESTATUS NOT IN(2)").and("SIA.IND_DEVOLUCION IS NULL")
		.innerJoin("SVT_ARTICULO A","A.ID_ARTICULO = SIA.ID_ARTICULO")
		.innerJoin("SVC_CATEGORIA_ARTICULO CA", "A.ID_CATEGORIA_ARTICULO  = CA.ID_CATEGORIA_ARTICULO")
		.where("IFNULL(SIA.ID_INVE_ARTICULO ,0) > 0");
		
		if (consultaStockRequest.getIdVelatorio() != null) {
			queryUtil.and("SIA.ID_VELATORIO = :idVelatorio").setParameter(ConsultaConstantes.ID_VELATORIO, ConsultaConstantes.getIdVelatorio(consultaStockRequest.getIdVelatorio()));
		}
		
		if (consultaStockRequest.getIdOrdenEntrada() != null) {
			queryUtil.and("OE.ID_ODE = :idOrdenEntrada").setParameter("idOrdenEntrada", consultaStockRequest.getIdOrdenEntrada());
		}
		
		if (consultaStockRequest.getIdCategoriaArticulo() != null) {
			queryUtil.and("CA.ID_CATEGORIA_ARTICULO = :idCategoriaArticulo").setParameter("idCategoriaArticulo", consultaStockRequest.getIdCategoriaArticulo());
		}
		
		if (consultaStockRequest.getIdTipoAsignacionArt() != null) {
			queryUtil.and("SIA.ID_TIPO_ASIGNACION_ART IN (".concat(consultaStockRequest.getIdTipoAsignacionArt()).concat(")"));
		}
		
		final String query = queryUtil.build();
		log.info(" consultarStock: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarStock");
		return request;
		
	}
	
	public String condicionconsultarStocka(ConsultaStockRequest consultaStockRequest) {
		StringBuilder query =new StringBuilder();
		if(consultaStockRequest.getIdVelatorio() != null) {
			query.append(" AND SIA.ID_VELATORIO = ").append(ConsultaConstantes.getIdVelatorio(consultaStockRequest.getIdVelatorio()));
		}
		
		if (consultaStockRequest.getIdOrdenEntrada() != null) {
			query.append(" AND OE.ID_ODE = ").append(consultaStockRequest.getIdOrdenEntrada());
		 }
		 
		if (consultaStockRequest.getIdCategoriaArticulo() != null) {
			query.append(" AND CA.ID_CATEGORIA_ARTICULO = ").append(consultaStockRequest.getIdCategoriaArticulo());
		}
		
		if (consultaStockRequest.getIdTipoAsignacionArt() != null) {
			query.append(" AND SIA.ID_TIPO_ASIGNACION_ART IN (").append(consultaStockRequest.getIdTipoAsignacionArt()).append(")");
		}
		return query.toString();
	}

}
