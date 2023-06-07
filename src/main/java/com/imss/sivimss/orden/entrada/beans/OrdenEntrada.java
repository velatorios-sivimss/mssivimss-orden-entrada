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

	public DatosRequest ultimoRegistroOrdenEntrada(DatosRequest request) {
		log.info(" INICIO - ultimoRegistroOrdenEntrada");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("MAX(OE.ID_ODE) + 1 AS ID_ODE").from("SVT_ORDEN_ENTRADA OE");
		final String query = queryUtil.build();
		log.info(" ultimoRegistroOrdenEntrada: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - ultimoRegistroOrdenEntrada");
		return request;
	}

	public DatosRequest consultarContratoProveedor(DatosRequest request, ContratoRequest contrato) {
		log.info(" INICIO - consultarContratoProveedor");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("P.ID_PROVEEDOR AS FOLIO_PROVEEDOR", "P.NOM_PROVEEDOR AS NOM_PROVEEDOR")
				.from("SVT_PROVEEDOR P").innerJoin(ConsultaConstantes.SVT_CONTRATO_C, "P.ID_PROVEEDOR = C.ID_PROVEEDOR")
				.and("P.IND_ACTIVO = 1").where(ConsultaConstantes.C_ID_CONTRATO_ID_CONTRATO)
				.setParameter(ConsultaConstantes.ID_CONTRATO, contrato.getIdContrato());
		final String query = queryUtil.build();
		log.info(" consultarContratoProveedor: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarContratoProveedor");
		return request;
	}

	public DatosRequest consultarContratoArticulo(DatosRequest request, ContratoRequest contrato) {
		log.info(" INICIO - consultarContratoArticulo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil
				.select("A.ID_ARTICULO AS ID_ARTICULO", "CA.DES_CATEGORIA_ARTICULO AS DES_CATEGORIA_ARTICULO",
						"A.DES_MODELO_ARTICULO AS DES_MODELO_ARTICULO", "COA.MON_COSTO_UNITARIO as MON_COSTO_UNITARIO",
						"COA.MON_PRECIO AS MON_PRECIO")
				.from("SVT_CONTRATO_ARTICULOS COA")
				.innerJoin(ConsultaConstantes.SVT_CONTRATO_C, "COA.ID_CONTRATO  = C.ID_CONTRATO")
				.and("C.FEC_FIN_VIG IS NULL")
				.innerJoin(ConsultaConstantes.SVT_ARTICULO_A, "A.ID_ARTICULO = COA.ID_ARTICULO ")
				.innerJoin("SVC_CATEGORIA_ARTICULO CA", "A.ID_CATEGORIA_ARTICULO  = CA.ID_CATEGORIA_ARTICULO")
				.where(ConsultaConstantes.C_ID_CONTRATO_ID_CONTRATO)
				.setParameter(ConsultaConstantes.ID_CONTRATO, contrato.getIdContrato());
		final String query = queryUtil.build();
		log.info(" consultarContratoCategoriaArticulo: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarContratoArticulo");
		return request;
	}

	public DatosRequest consultarDescripcionVelatorio(DatosRequest request, UsuarioDto usuarioDto) {
		log.info(" INICIO - consultarDescripcionVelatorio");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("V.DES_VELATORIO").from("SVC_VELATORIO V").where("V.ID_VELATORIO = :idVelatorio").setParameter(
				ConsultaConstantes.ID_VELATORIO, ConsultaConstantes.getIdVelatorio(usuarioDto.getIdVelatorio()));
		final String query = queryUtil.build();
		log.info(" consultarDescripcionVelatorio: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarDescripcionVelatorio");
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
		for (int i = 1; i <= ordenEntradaRequest.getNumArticulo(); i++) {
			final QueryHelper q = new QueryHelper("INSERT INTO SVT_INVENTARIO_ARTICULO");
			q.agregarParametroValues("ID_ODE", String.valueOf(ordenEntradaRequest.getIdOrdenEntrada()));
			q.agregarParametroValues("ID_ARTICULO", String.valueOf(ordenEntradaRequest.getIdArticulo()));
			q.agregarParametroValues("FOLIO_ARTICULO", "'" +Integer.toString(ordenEntradaRequest.getIdOrdenEntrada()).concat(Integer.toString(i).concat(ordenEntradaRequest.getNumFolioArticulo()))+ "'");
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
