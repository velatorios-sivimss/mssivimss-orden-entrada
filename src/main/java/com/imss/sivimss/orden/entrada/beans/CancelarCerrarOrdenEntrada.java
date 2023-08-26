package com.imss.sivimss.orden.entrada.beans;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.orden.entrada.model.request.ActualizarMultiRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.ConsultaConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.QueryHelper;
import com.imss.sivimss.orden.entrada.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CancelarCerrarOrdenEntrada {

	
	

	public DatosRequest consultarDetalleOrdenEntrada(DatosRequest request, OrdenEntradaRequest ordenEntradaRequest, String formatoFecha ) {
		log.info(" INICIO - consultarDetalleOrdenEntrada");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil
		.select("DISTINCT  SOE.ID_ODE AS ID_ODE", "SOE.CVE_FOLIO AS NUM_FOLIO_ODE", "SC.DES_CONTRATO AS DES_CONTRATO",
				"SP.NOM_PROVEEDOR AS NOM_PROVEEDOR", "SP.ID_PROVEEDOR  AS FOLIO_PROVEEDOR",
				"CA.DES_CATEGORIA_ARTICULO AS DES_CATEGORIA_ARTICULO",
				"SA.DES_MODELO_ARTICULO AS DES_MODELO_ARTICULO", "SV.DES_VELATORIO AS DES_VELATORIO",
				"SCA.MON_COSTO_UNITARIO AS MON_COSTO_UNITARIO", "SCA.MON_PRECIO AS MON_PRECIO",
				"SOE.NUM_ARTICULO AS NUM_ARTICULO", "DATE_FORMAT(SOE.FEC_INGRESO,'"+ formatoFecha+"') AS FEC_ODE",
				"SOE.ID_ESTATUS_ORDEN_ENTRADA AS ESTATUS_ORDEN_ENTRADA")
		.from(ConsultaConstantes.SVT_ORDEN_ENTRADA_SOE)
		.innerJoin(ConsultaConstantes.SVT_INVENTARIO_ARTICULO_SIA, "SIA.ID_ODE = SOE.ID_ODE")
		.innerJoin("SVT_CONTRATO SC", "SC.ID_CONTRATO = SOE.ID_CONTRATO").and("SC.IND_ACTIVO = 1")
		.innerJoin(ConsultaConstantes.SVT_CONTRATO_ARTICULOS_SCA, "SC.ID_CONTRATO = SCA.ID_CONTRATO").and("SIA.ID_ARTICULO = SCA.ID_ARTICULO")
		.innerJoin("SVT_PROVEEDOR SP", "SC.ID_PROVEEDOR = SP.ID_PROVEEDOR").and("SP.IND_ACTIVO = 1")
		.innerJoin("SVC_VELATORIO SV", "SC.ID_VELATORIO = SV.ID_VELATORIO").and("SV.IND_ACTIVO = 1")
		.innerJoin("SVT_ARTICULO SA","SA.ID_ARTICULO = SCA.ID_ARTICULO")
		.innerJoin(ConsultaConstantes.SVC_CATEGORIA_ARTICULO_CA, "CA.ID_CATEGORIA_ARTICULO  = SA.ID_CATEGORIA_ARTICULO")
		.where("IFNULL(SOE.ID_ODE,0) > 0")
		.and("SOE.ID_ODE = :idOrdenEntrada").setParameter(ConsultaConstantes.ID_ORDEN_ENTRADA, ordenEntradaRequest.getIdOrdenEntrada()); 

		final String query = queryUtil.build();
		log.info(" consultarDetalleOrdenEntrada: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarDetalleOrdenEntrada");
		return request;
	}
	public DatosRequest consultarCantidadArticulo(DatosRequest request,OrdenEntradaRequest ordenEntradaRequest ) {
		log.info(" INICIO - consultarCantidadArticulo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DISTINCT SA.ID_ARTICULO AS idArticulo"," SA.CAN_UNIDAD AS cantidadUnidadArticulo","COUNT(SOE.ID_ODE) AS cantidadInventarioArticulo")
		.from("SVT_ARTICULO SA")
		.innerJoin(ConsultaConstantes.SVT_INVENTARIO_ARTICULO_SIA,"SIA.ID_ARTICULO = SA.ID_ARTICULO")
		.innerJoin(ConsultaConstantes.SVT_ORDEN_ENTRADA_SOE, "SOE.ID_ODE = SIA.ID_ODE")
		.and("SOE.ID_ODE= :idOrdenEntrada").setParameter(ConsultaConstantes.ID_ORDEN_ENTRADA, ordenEntradaRequest.getIdOrdenEntrada());
		final String query = queryUtil.build();
		log.info(" consultarCantidadArticulo: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarCantidadArticulo");
		return request;
	}
	
	public DatosRequest verificaOrdenEntradaRelacionOrdenServicio(DatosRequest request,OrdenEntradaRequest ordenEntradaRequest ) {
		log.info(" INICIO - verificaOrdenEntradaRelacionOrdenServicio");
		SelectQueryUtil selectQueryUtilUnionTemp= new SelectQueryUtil();
		
		selectQueryUtilUnionTemp.select("COUNT(*) AS cantidadInventarioArticulo").from("SVC_DETALLE_CARAC_PRESUP_TEMP SDCPT")
		.where("ID_INVE_ARTICULO  IN (SELECT SIA.ID_INVE_ARTICULO  FROM SVT_ORDEN_ENTRADA SOE INNER JOIN SVT_INVENTARIO_ARTICULO SIA".concat(
				" ON SOE.ID_ODE = SIA.ID_ODE WHERE SOE.ID_ODE = "+ordenEntradaRequest.getIdOrdenEntrada()+")")).and("SDCPT.IND_ACTIVO=1");
		
		final String query = selectQueryUtilUnionTemp.build();
		log.info(" verificaOrdenEntradaRelacionOrdenServicio: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - verificaOrdenEntradaRelacionOrdenServicio");
		return request;
	}
	
	public DatosRequest verificaOrdenEntradaRelacionOrdenServicio2(DatosRequest request,OrdenEntradaRequest ordenEntradaRequest ) {
		log.info(" INICIO - verificaOrdenEntradaRelacionOrdenServicio");
		SelectQueryUtil selectQueryUtilUnion= new SelectQueryUtil();
		
		selectQueryUtilUnion.select("COUNT(*) AS cantidadInventarioArticulo").from("SVC_DETALLE_CARAC_PRESUP SDCP")
		.where("ID_INVE_ARTICULO  IN (SELECT SIA.ID_INVE_ARTICULO  FROM SVT_ORDEN_ENTRADA SOE INNER JOIN SVT_INVENTARIO_ARTICULO SIA".concat(
				" ON SOE.ID_ODE = SIA.ID_ODE WHERE SOE.ID_ODE = "+ordenEntradaRequest.getIdOrdenEntrada()+")")).and("SDCP.IND_ACTIVO=1");
		
		final String query = selectQueryUtilUnion.build();
		log.info(" verificaOrdenEntradaRelacionOrdenServicio: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - verificaOrdenEntradaRelacionOrdenServicio");
		return request;
	}
	
	public ActualizarMultiRequest actualizarOrdenEntrada(OrdenEntradaRequest ordenEntradaRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - actualizarOrdenEntrada");
		ActualizarMultiRequest actualizarMultiRequest = new ActualizarMultiRequest();
		List<String> updates = new ArrayList<>();

		final QueryHelper q = new QueryHelper("UPDATE SVT_ORDEN_ENTRADA ");
		q.agregarParametroValues("ID_ESTATUS_ORDEN_ENTRADA", String.valueOf(ordenEntradaRequest.getIndEstatus()));
		q.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q.addWhere("ID_ODE = " + ordenEntradaRequest.getIdOrdenEntrada());
		
		String query = q.obtenerQueryActualizar();
		log.info(" actualizarOrdenEntrada: " + query);
		updates.add(DatatypeConverter.printBase64Binary(q.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		
		final QueryHelper q1 = new QueryHelper("UPDATE SVT_INVENTARIO_ARTICULO ");
		q1.agregarParametroValues("IND_ESTATUS", String.valueOf(ordenEntradaRequest.getIndEstatus()));
		q1.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q1.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q1.addWhere("ID_ODE = " + ordenEntradaRequest.getIdOrdenEntrada());
		
		String query1 = q1.obtenerQueryActualizar();
		log.info(" actualizarInventarioArticulo: " + query1);
		updates.add(DatatypeConverter.printBase64Binary(q1.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		
		final QueryHelper q2 = new QueryHelper("UPDATE SVT_ARTICULO");
		q2.agregarParametroValues("CAN_UNIDAD", Integer.toString(ordenEntradaRequest.getCantidadUnidadArticulo()-ordenEntradaRequest.getCantidadInventarioArticulo()));
		q2.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q2.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q2.addWhere("ID_ARTICULO = " + ordenEntradaRequest.getIdArticulo());
		
		String query2 = q2.obtenerQueryActualizar();
		log.info(" actualizarArticulo: " + query2);
		updates.add(DatatypeConverter.printBase64Binary(q2.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		
		log.info(" TERMINO - actualizarOrdenEntrada");
		return actualizarMultiRequest;
	}

}
