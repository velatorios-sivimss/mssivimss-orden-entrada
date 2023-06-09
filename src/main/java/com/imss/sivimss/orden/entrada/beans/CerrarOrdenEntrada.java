package com.imss.sivimss.orden.entrada.beans;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.orden.entrada.model.request.ActualizarMultiRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.util.ConsultaConstantes;
import com.imss.sivimss.orden.entrada.util.QueryHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CerrarOrdenEntrada {
	
	public ActualizarMultiRequest actualizarOrdenEntrada(OrdenEntradaRequest ordenEntradaRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - actualizarInventarioArticulo");
		ActualizarMultiRequest actualizarMultiRequest = new ActualizarMultiRequest();
		List<String> updates = new ArrayList<>();

		final QueryHelper q = new QueryHelper("UPDATE SVT_ORDEN_ENTRADA ");
		q.agregarParametroValues("ID_ESTATUS_ORDEN_ENTRADA", String.valueOf(3));
		q.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q.addWhere("ID_ODE = " + ordenEntradaRequest.getIdOrdenEntrada());
		
		String query = q.obtenerQueryActualizar();
		log.info(" actualizarInventarioArticulo: " + query);
		updates.add(DatatypeConverter.printBase64Binary(q.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		
		final QueryHelper q1 = new QueryHelper("UPDATE SVT_INVENTARIO_ARTICULO ");
		q1.agregarParametroValues("IND_CERRAR", String.valueOf(1));
		q1.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q1.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q1.addWhere("ID_ODE = " + ordenEntradaRequest.getIdOrdenEntrada());
		
		String query1 = q1.obtenerQueryActualizar();
		log.info(" actualizarOrdenEntrada: " + query1);
		updates.add(DatatypeConverter.printBase64Binary(q1.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		actualizarMultiRequest.setUpdates(updates);
		
		log.info(" TERMINO - actualizarInventarioArticulo");
		return actualizarMultiRequest;
	}

}
