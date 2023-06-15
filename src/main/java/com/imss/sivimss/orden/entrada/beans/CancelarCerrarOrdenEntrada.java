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
	
	public DatosRequest consultarDetalleOrdenEntrada(DatosRequest request, OrdenEntradaRequest ordenEntradaRequest ) {
		log.info(" INICIO - consultarDetalleOrdenEntrada");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil
				.select("OE.ID_ODE AS ID_ODE", "OE.NUM_FOLIO AS NUM_FOLIO_ODE", "C.DES_CONTRATO AS DES_CONTRATO",
						"P.NOM_PROVEEDOR AS NOM_PROVEEDOR", "P.ID_PROVEEDOR  AS FOLIO_PROVEEDOR",
						"CA.DES_CATEGORIA_ARTICULO AS DES_CATEGORIA_ARTICULO",
						"A.DES_MODELO_ARTICULO AS DES_MODELO_ARTICULO", "V.DES_VELATORIO AS DES_VELATORIO",
						"SCA.MON_COSTO_UNITARIO AS MON_COSTO_UNITARIO", "SCA.MON_PRECIO AS MON_PRECIO",
						"OE.NUM_ARTICULO AS NUM_ARTICULO", "OE.FEC_INGRESO AS FEC_ODE",
						"OE.ID_ESTATUS_ORDEN_ENTRADA AS ESTATUS_ORDEN_ENTRADA")
				.from("SVT_ORDEN_ENTRADA OE").innerJoin("SVT_CONTRATO C", "OE.ID_CONTRATO = C.ID_CONTRATO")
				.and("C.FEC_FIN_VIG IS NULL").innerJoin("SVT_CONTRATO_ARTICULOS SCA", "C.ID_CONTRATO = SCA.ID_CONTRATO")
				.innerJoin("SVT_PROVEEDOR P", "C.ID_PROVEEDOR = P.ID_PROVEEDOR").and("P.IND_ACTIVO = 1")
				.innerJoin("SVC_VELATORIO V", "C.ID_VELATORIO = V.ID_VELATORIO").and("V.IND_ACTIVO = 1")
				.innerJoin("SVT_ARTICULO A", "A.ID_ARTICULO = SCA.ID_ARTICULO")
				.innerJoin("SVC_CATEGORIA_ARTICULO CA", "CA.ID_CATEGORIA_ARTICULO  = A.ID_CATEGORIA_ARTICULO")
				.where("OE.ID_ODE = :idOrdenEntrada").setParameter("idOrdenEntrada", ordenEntradaRequest.getIdOrdenEntrada()); 

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
		.innerJoin("SVT_INVENTARIO_ARTICULO SIA","SIA.ID_ARTICULO = SA.ID_ARTICULO")
		.innerJoin("SVT_ORDEN_ENTRADA SOE", "SOE.ID_ODE = SIA.ID_ODE")
		.and("SOE.ID_ODE= :idOrdenEntrada").setParameter("idOrdenEntrada", ordenEntradaRequest.getIdOrdenEntrada());
		final String query = queryUtil.build();
		log.info(" consultarCantidadArticulo: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarCantidadArticulo");
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
