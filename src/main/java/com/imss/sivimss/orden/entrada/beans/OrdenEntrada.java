package com.imss.sivimss.orden.entrada.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.orden.entrada.model.request.ContratoRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.ConsultaConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.QueryHelper;
import com.imss.sivimss.orden.entrada.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrdenEntrada {

	public DatosRequest consultarOrdenEntrada(DatosRequest request, OrdenEntradaRequest ordenEntradaRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - consultarOrdenEntrada");
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
				.innerJoin("SVC_CATEGORIA_ARTICULO CA", "CA.ID_CATEGORIA_ARTICULO  = A.ID_CATEGORIA_ARTICULO");
		
		if(usuarioDto.getIdVelatorio() != null) {
			queryUtil.where("C.ID_VELATORIO = :idVelatorio").setParameter(ConsultaConstantes.ID_VELATORIO, ConsultaConstantes.getIdVelatorio(usuarioDto.getIdVelatorio()));
		}
		
		if (ordenEntradaRequest.getNumFolioOrdenEntrada() != null) {
		 queryUtil.and("OE.NUM_FOLIO = :numFolioOrdenEntrada").setParameter("numFolioOrdenEntrada", ordenEntradaRequest.getNumFolioOrdenEntrada()); 
		 }
		 
		if (ordenEntradaRequest.getNomProveedor() != null) {
			queryUtil.and("P.NOM_PROVEEDOR = :nomProveedor").setParameter("nomProveedor",ordenEntradaRequest.getNomProveedor());
		}
		if (ordenEntradaRequest.getFechaInicio() != null && ordenEntradaRequest.getFechaFin() != null) {
			queryUtil.and("OE.FEC_INGRESO >= :fecInicio")
					.setParameter("fecInicio", ordenEntradaRequest.getFechaInicio()).and("OE.FEC_INGRESO <= :fecFin")
					.setParameter("fecFin", ordenEntradaRequest.getFechaFin());
		}
		final String query = queryUtil.build();
		log.info(" consultarOrdenEntrada: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarOrdenEntrada");
		return request;
	}
	
	public DatosRequest consultaFolioOrdenEntrada(DatosRequest request, UsuarioDto usuarioDto) {
		log.info(" INICIO - consultaFolioOrdenEntrada");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("(SELECT MAX(OE.ID_ODE) + 1 FROM SVT_ORDEN_ENTRADA OE ) AS idOrdenEntrada",
				"CONCAT_WS('', (SELECT LPAD (MAX(OE.ID_ODE) + 1, 4, '0') FROM SVT_ORDEN_ENTRADA OE),'ODE',(SELECT SUBSTRING(SV.DES_VELATORIO,1,3) AS DESVELATORIO FROM SVC_VELATORIO SV WHERE SV.ID_VELATORIO = "+usuarioDto.getIdVelatorio()+")) AS numFolio",
				"(SELECT MAX(IA.ID_INVE_ARTICULO) FROM SVT_INVENTARIO_ARTICULO IA ) AS idInventarioArticulo")
		.from("DUAL");
		final String query = queryUtil.build();
		log.info(" consultaFolioOrdenEntrada: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultaFolioOrdenEntrada");
		return request;
	}
	
	public DatosRequest consultarContratoPorVelatorio(DatosRequest request, UsuarioDto usuarioDto) {
		log.info(" INICIO - consultarOrdenEntradaPorVelatorio");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("C.ID_CONTRATO AS ID_CONTRATO","C.NUM_CONTRATO AS NUM_CONTRATO").from("SVT_CONTRATO C")
		.innerJoin("SVC_VELATORIO V", "V.ID_VELATORIO = C.ID_VELATORIO").where("C.ID_TIPO_ASIGNACION  = 2")
		.and("C.ID_VELATORIO = :idVelatorio").setParameter(ConsultaConstantes.ID_VELATORIO, ConsultaConstantes.getIdVelatorio(usuarioDto.getIdVelatorio()));
		final String query = queryUtil.build();
		log.info(" consultarOrdenEntradaPorVelatorio: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarOrdenEntradaPorVelatorio");
		return request;
	}

	public DatosRequest consultarContratoProveedorArticulo(DatosRequest request, ContratoRequest contrato) {
		log.info(" INICIO - consultarContratoProveedorArticulo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil
				.select("C.ID_CONTRATO AS ID_CONTRATO","P.ID_PROVEEDOR AS FOLIO_PROVEEDOR","P.NOM_PROVEEDOR AS NOM_PROVEEDOR",
						"A.ID_ARTICULO AS ID_ARTICULO", "CA.DES_CATEGORIA_ARTICULO AS DES_CATEGORIA_ARTICULO",
						"A.DES_MODELO_ARTICULO AS DES_MODELO_ARTICULO", "COA.MON_COSTO_UNITARIO as MON_COSTO_UNITARIO",
						"COA.MON_PRECIO AS MON_PRECIO","V.DES_VELATORIO as DES_VELATORIO")
				.from("SVT_CONTRATO_ARTICULOS COA")
				.innerJoin(ConsultaConstantes.SVT_CONTRATO_C, "COA.ID_CONTRATO  = C.ID_CONTRATO").and("C.FEC_FIN_VIG IS NULL")
				.innerJoin("SVT_PROVEEDOR P", "P.ID_PROVEEDOR = C.ID_PROVEEDOR")
				.innerJoin("SVC_VELATORIO V", "V.ID_VELATORIO = C.ID_VELATORIO")
				.innerJoin(ConsultaConstantes.SVT_ARTICULO_A, "A.ID_ARTICULO = COA.ID_ARTICULO ")
				.innerJoin("SVC_CATEGORIA_ARTICULO CA", "A.ID_CATEGORIA_ARTICULO  = CA.ID_CATEGORIA_ARTICULO")
				.where(ConsultaConstantes.C_NUM_CONTRATO_NUM_CONTRATO)
				.setParameter(ConsultaConstantes.NUM_CONTRATO, contrato.getNumContrato());
		final String query = queryUtil.build();
		log.info(" consultarContratoProveedorArticulo: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarContratoProveedorArticulo");
		return request;
	}

	public DatosRequest insertarOrdenEntrada(OrdenEntradaRequest ordenEntradaRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - insertarOrdenEntrada");
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ORDEN_ENTRADA");
		q.agregarParametroValues("ID_ODE", String.valueOf(ordenEntradaRequest.getIdOrdenEntrada()));
		q.agregarParametroValues("NUM_FOLIO", "'" + ordenEntradaRequest.getNumFolioOrdenEntrada() + "'");
		q.agregarParametroValues("ID_CONTRATO", String.valueOf(ordenEntradaRequest.getIdContrato()));
		q.agregarParametroValues("NUM_ARTICULO", String.valueOf(ordenEntradaRequest.getNumArticulo()));
		q.agregarParametroValues("FEC_INGRESO", "'" + ordenEntradaRequest.getFecIngreso() + "'");
		q.agregarParametroValues("IND_ACTIVO", String.valueOf(1));
		q.agregarParametroValues(ConsultaConstantes.ID_USUARIO_ALTA, String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues(ConsultaConstantes.FEC_ALTA, ConsultaConstantes.CURRENT_TIMESTAMP);
		q.agregarParametroValues("ID_ESTATUS_ORDEN_ENTRADA", String.valueOf(1));
		String query = q.obtenerQueryInsertar() + insertInventarioArticulo(ordenEntradaRequest, usuarioDto);
		log.info(" insertarOrdenEntrada: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		parametro.put(ConsultaConstantes.SEPARADOR, "$$");
		parametro.put(ConsultaConstantes.REPLACE, ConsultaConstantes.ID_TABLA);

		request.setDatos(parametro);

		log.info(" TERMINO - insertarOrdenEntrada");
		return request;
	}

	public String insertInventarioArticulo(OrdenEntradaRequest ordenEntradaRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - insertInventarioArticulo");
		StringBuilder query = new StringBuilder();
		for (int i = 0; i < ordenEntradaRequest.getNumArticulo(); i++) {
			final QueryHelper q = new QueryHelper("INSERT INTO SVT_INVENTARIO_ARTICULO");
			q.agregarParametroValues("ID_ODE", String.valueOf(ordenEntradaRequest.getIdOrdenEntrada()));
			q.agregarParametroValues("ID_ARTICULO", String.valueOf(ordenEntradaRequest.getIdArticulo()));
			q.agregarParametroValues("FOLIO_ARTICULO", "'" +Integer.toString(ordenEntradaRequest.getIdInventarioArticulo()+i).concat(Integer.toString(ordenEntradaRequest.getFolioProveedor()).concat(ConsultaConstantes.filter(ordenEntradaRequest.getDesModeloArticulo()).toUpperCase()))+ "'");
			q.agregarParametroValues("ID_TIPO_ASIGNACION_ART", String.valueOf(1));
			q.agregarParametroValues(ConsultaConstantes.ID_USUARIO_ALTA, String.valueOf(usuarioDto.getIdUsuario()));
			q.agregarParametroValues(ConsultaConstantes.FEC_ALTA, ConsultaConstantes.CURRENT_TIMESTAMP);
			q.agregarParametroValues("ID_VELATORIO", String.valueOf(usuarioDto.getIdVelatorio()));
			query.append("$$").append(q.obtenerQueryInsertar());
		}
		log.info(" TERMINO - insertInventarioArticulo");
		return query.toString();
	}

}
