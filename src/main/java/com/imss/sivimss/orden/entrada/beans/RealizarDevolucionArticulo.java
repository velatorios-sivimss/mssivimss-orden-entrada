package com.imss.sivimss.orden.entrada.beans;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.orden.entrada.model.request.ActualizarMultiRequest;
import com.imss.sivimss.orden.entrada.model.request.InventarioArticuloRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.ConsultaConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.QueryHelper;
import com.imss.sivimss.orden.entrada.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealizarDevolucionArticulo {
	
	public DatosRequest consultarFolioArticulo(DatosRequest request, InventarioArticuloRequest inventarioArticuloRequest, String formatoFecha) {
		log.info(" INICIO - consultarFolioArticulo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SIA.ID_ODE AS ID_ODE","SOE.CVE_FOLIO AS NUM_FOLIO_ODE",
		"DATE_FORMAT(SOE.FEC_INGRESO,'"+ formatoFecha+"') AS FEC_ODE","SIA.ID_INVE_ARTICULO AS ID_INVE_ARTICULO",
		"SIA.CVE_FOLIO_ARTICULO AS FOLIO_ARTICULO","A.DES_MODELO_ARTICULO AS DES_MODELO_ARTICULO")
		.from("SVT_INVENTARIO_ARTICULO SIA").innerJoin("SVT_ORDEN_ENTRADA SOE", "SOE.ID_ODE  = SIA.ID_ODE")
		.innerJoin("SVT_ARTICULO A", "A.ID_ARTICULO = SIA.ID_ARTICULO").where("SIA.CVE_FOLIO_ARTICULO = :numFolioArticulo")
		.setParameter("numFolioArticulo", inventarioArticuloRequest.getNumFolioArticulo());
		final String query = queryUtil.build();
		log.info(" consultarFolioArticulo: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarFolioArticulo");
		return request;
	}
	
	public DatosRequest consultarOrdenEntrada(DatosRequest request, InventarioArticuloRequest inventarioArticuloRequest) {
		log.info(" INICIO - consultarOrdenEntradaPorVelatorio");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SOE.NUM_ARTICULO AS numArticulo").from("SVT_ORDEN_ENTRADA SOE")
		.where("SOE.ID_ODE = :idOrdenEntrada").setParameter("idOrdenEntrada", inventarioArticuloRequest.getIdOrdenEntrada());
		final String query = queryUtil.build();
		log.info(" consultarOrdenEntradaPorVelatorio: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarOrdenEntradaPorVelatorio");
		return request;
	}
	
	
	public ActualizarMultiRequest actualizarInventarioArticulo(InventarioArticuloRequest inventarioArticuloRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - actualizarInventarioArticulo");
		ActualizarMultiRequest actualizarMultiRequest = new ActualizarMultiRequest();
		List<String> updates = new ArrayList<>();

		final QueryHelper q = new QueryHelper("UPDATE SVT_INVENTARIO_ARTICULO ");
		q.agregarParametroValues("IND_DEVOLUCION", String.valueOf(1));
		q.agregarParametroValues("DES_MOTIVO_DEVOLUCION", "'" + inventarioArticuloRequest.getDesMotivoDevolucion() +"'");
		q.agregarParametroValues("IND_ESTATUS", String.valueOf(0));
		q.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q.addWhere("ID_INVE_ARTICULO = " + inventarioArticuloRequest.getIdInventarioArticulo());
		
		String query = q.obtenerQueryActualizar();
		log.info(" actualizarInventarioArticulo: " + query);
		updates.add(DatatypeConverter.printBase64Binary(q.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		
		final QueryHelper q1 = new QueryHelper("UPDATE SVT_ORDEN_ENTRADA ");
		q1.agregarParametroValues("NUM_ARTICULO", String.valueOf(inventarioArticuloRequest.getNumArticulo()-1));
		q1.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q1.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q1.addWhere("ID_ODE = " + inventarioArticuloRequest.getIdOrdenEntrada());
		
		String query1 = q1.obtenerQueryActualizar();
		log.info(" actualizarOrdenEntrada: " + query1);
		updates.add(DatatypeConverter.printBase64Binary(q1.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		
		log.info(" TERMINO - actualizarInventarioArticulo");
		return actualizarMultiRequest;
	}

}
