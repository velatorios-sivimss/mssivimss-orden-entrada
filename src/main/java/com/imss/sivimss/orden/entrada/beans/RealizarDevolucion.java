package com.imss.sivimss.orden.entrada.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.orden.entrada.model.request.InventarioArticuloRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.ConsultaConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.QueryHelper;
import com.imss.sivimss.orden.entrada.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealizarDevolucion {
	
	public DatosRequest consultarFolioArticulo(DatosRequest request, InventarioArticuloRequest inventarioArticuloRequest) {
		log.info(" INICIO - consultarFolioArticulo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SIA.ID_INVE_ARTICULO AS ID_INVE_ARTICULO","SIA.FOLIO_ARTICULO AS FOLIO_ARTICULO","A.DES_MODELO_ARTICULO AS DES_MODELO_ARTICULO")
		.from("SVT_INVENTARIO_ARTICULO SIA").innerJoin("SVT_ARTICULO A", "A.ID_ARTICULO = SIA.ID_ARTICULO").where("SIA.FOLIO_ARTICULO = :numFolioArticulo")
		.setParameter("numFolioArticulo", inventarioArticuloRequest.getNumFolioArticulo());
		final String query = queryUtil.build();
		log.info(" consultarFolioArticulo: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarFolioArticulo");
		return request;
	}
	
	
	public DatosRequest actualizarInventarioArticulo(InventarioArticuloRequest inventarioArticuloRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - actualizarInventarioArticulo");
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		final QueryHelper q = new QueryHelper("UPDATE SVT_INVENTARIO_ARTICULO ");
		q.agregarParametroValues("IND_DEVOLUCION", String.valueOf(1));
		q.agregarParametroValues("DES_MOTIVO_DEVOLUCION", "'" + inventarioArticuloRequest.getDesMotivoDevolucion() +"'");
		q.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q.addWhere("ID_INVE_ARTICULO = " + inventarioArticuloRequest.getIdInventarioArticulo());
		
		String query = q.obtenerQueryActualizar();
		log.info(" actualizarInventarioArticulo: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		log.info(" TERMINO - actualizarInventarioArticulo");
		return request;
	}

}
