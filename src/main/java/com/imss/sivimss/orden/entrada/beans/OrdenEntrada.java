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
				.and("C.FEC_FIN_VIG IS NULL").innerJoin(ConsultaConstantes.SVT_CONTRATO_ARTICULOS_SCA, "C.ID_CONTRATO = SCA.ID_CONTRATO")
				.innerJoin("SVT_PROVEEDOR P", "C.ID_PROVEEDOR = P.ID_PROVEEDOR").and("P.IND_ACTIVO = 1")
				.innerJoin("SVC_VELATORIO V", "C.ID_VELATORIO = V.ID_VELATORIO").and("V.IND_ACTIVO = 1")
				.innerJoin(ConsultaConstantes.SVT_ARTICULO_A,ConsultaConstantes. A_ID_ARTICULO_SCA_ID_ARTICULO)
				.innerJoin(ConsultaConstantes.SVC_CATEGORIA_ARTICULO_CA, "CA.ID_CATEGORIA_ARTICULO  = A.ID_CATEGORIA_ARTICULO");
		
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
	
	public DatosRequest consultaFolioOrdenEntrada(DatosRequest request, OrdenEntradaRequest ordenEntradaRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - consultaFolioOrdenEntrada");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("(SELECT IFNULL(MAX(OE.ID_ODE),0) + 1 FROM SVT_ORDEN_ENTRADA OE ) AS idOrdenEntrada",
				"CONCAT_WS('', (SELECT LPAD (IFNULL(MAX(OE.ID_ODE),0) + 1, 4, '0') FROM SVT_ORDEN_ENTRADA OE),'ODE',(SELECT SUBSTRING(SV.DES_VELATORIO,1,3) AS DESVELATORIO FROM SVC_VELATORIO SV WHERE SV.ID_VELATORIO = "+usuarioDto.getIdVelatorio()+")) AS numFolio",
				"(SELECT MAX(IA.ID_INVE_ARTICULO) FROM SVT_INVENTARIO_ARTICULO IA ) AS idInventarioArticulo",
				"(SELECT A.CAN_UNIDAD FROM SVT_ARTICULO A WHERE A.ID_ARTICULO = "+ordenEntradaRequest.getIdArticulo()+" ) AS cantidadUnidadArticulo")
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
		.innerJoin("SVC_VELATORIO V", "V.ID_VELATORIO = C.ID_VELATORIO").where("C.ID_TIPO_ASIGNACION = 2").and("C.ID_TIPO_CONTRATO = 1")
		.and("C.ID_VELATORIO = :idVelatorio").setParameter(ConsultaConstantes.ID_VELATORIO, ConsultaConstantes.getIdVelatorio(usuarioDto.getIdVelatorio()));
		final String query = queryUtil.build();
		log.info(" consultarOrdenEntradaPorVelatorio: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarOrdenEntradaPorVelatorio");
		return request;
	}
	
	public DatosRequest consultarContratoProveedor(DatosRequest request, ContratoRequest contrato) {
		log.info(" INICIO - consultarContratoProveedor");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil
				.select("SC.ID_CONTRATO AS ID_CONTRATO","SP.ID_PROVEEDOR AS FOLIO_PROVEEDOR","SP.NOM_PROVEEDOR AS NOM_PROVEEDOR",
						"SV.DES_VELATORIO as DES_VELATORIO")
				.from(ConsultaConstantes.SVT_CONTRATO_SC)
				.innerJoin("SVT_PROVEEDOR SP", "SP.ID_PROVEEDOR = SC.ID_PROVEEDOR")
				.innerJoin("SVC_VELATORIO SV", "SV.ID_VELATORIO = SC.ID_VELATORIO")
				.where(ConsultaConstantes.SC_ID_CONTRATO_ID_CONTRATO).setParameter(ConsultaConstantes.ID_CONTRATO, contrato.getIdContrato());
		final String query = queryUtil.build();
		log.info(" consultarContratoProveedor: " + query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		log.info(" TERMINO - consultarContratoProveedor");
		return request;
	}
	
	public DatosRequest consultarContratoCategoria(DatosRequest request, ContratoRequest contrato) {
		log.info(" INICIO - consultarContratoCategoria");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DISTINCT CA.ID_CATEGORIA_ARTICULO AS ID_CATEGORIA_ARTICULO","CA.DES_CATEGORIA_ARTICULO AS DES_CATEGORIA_ARTICULO")
		.from(ConsultaConstantes.SVT_CONTRATO_ARTICULOS_SCA)
		.innerJoin(ConsultaConstantes.SVT_CONTRATO_SC, ConsultaConstantes.SC_ID_CONTRATO_SCA_ID_CONTRATO)
		.innerJoin(ConsultaConstantes.SVT_ARTICULO_A, ConsultaConstantes.A_ID_ARTICULO_SCA_ID_ARTICULO)
		.innerJoin(ConsultaConstantes.SVC_CATEGORIA_ARTICULO_CA, ConsultaConstantes.A_ID_CATEGORIA_ARTICULO_CA_ID_CATEGORIA_ARTICULO)
		.where(ConsultaConstantes.SC_ID_CONTRATO_ID_CONTRATO).setParameter(ConsultaConstantes.ID_CONTRATO, contrato.getIdContrato());
		final String query = queryUtil.build();
		log.info(" consultarContratoCategoria: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarContratoCategoria");
		return request;
	}
	
	public DatosRequest consultarContratoModelo(DatosRequest request, ContratoRequest contrato) {
		log.info(" INICIO - consultarContratoModelo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("A.ID_ARTICULO AS ID_ARTICULO","A.DES_MODELO_ARTICULO  AS DES_MODELO_ARTICULO")
		.from(ConsultaConstantes.SVT_CONTRATO_ARTICULOS_SCA)
		.innerJoin(ConsultaConstantes.SVT_CONTRATO_SC, ConsultaConstantes.SC_ID_CONTRATO_SCA_ID_CONTRATO)
		.innerJoin(ConsultaConstantes.SVT_ARTICULO_A, ConsultaConstantes.A_ID_ARTICULO_SCA_ID_ARTICULO)
		.innerJoin(ConsultaConstantes.SVC_CATEGORIA_ARTICULO_CA, ConsultaConstantes.A_ID_CATEGORIA_ARTICULO_CA_ID_CATEGORIA_ARTICULO)
		.where(ConsultaConstantes.SC_ID_CONTRATO_ID_CONTRATO).setParameter(ConsultaConstantes.ID_CONTRATO, contrato.getIdContrato())
		.and("CA.ID_CATEGORIA_ARTICULO = :idCategoriaArticulo").setParameter("idCategoriaArticulo", contrato.getIdCategoriaArticulo());
		final String query = queryUtil.build();
		log.info(" consultarContratoModelo: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarContratoModelo");
		return request;
	}
	
	public DatosRequest consultarContratoCosto(DatosRequest request, ContratoRequest contrato) {
		log.info(" INICIO - consultarContratoModelo");
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCA.NUM_CANTIDAD - COUNT(SIA.ID_ODE) AS NUM_CANTIDAD_DISPONIBLE","A.ID_ARTICULO AS ID_ARTICULO",
				"SCA.MON_COSTO_UNITARIO AS MON_COSTO_UNITARIO","SCA.MON_PRECIO AS MON_PRECIO")
		.from("SVT_ORDEN_ENTRADA SOE")
		.innerJoin("SVT_INVENTARIO_ARTICULO SIA", "SOE.ID_ODE = SIA.ID_ODE")
		.rightJoin(ConsultaConstantes.SVT_CONTRATO_SC, "SC.ID_CONTRATO = SOE.ID_CONTRATO")
		.innerJoin(ConsultaConstantes.SVT_CONTRATO_ARTICULOS_SCA,  ConsultaConstantes.SC_ID_CONTRATO_SCA_ID_CONTRATO)
		.innerJoin(ConsultaConstantes.SVT_ARTICULO_A, ConsultaConstantes.A_ID_ARTICULO_SCA_ID_ARTICULO)
		.innerJoin(ConsultaConstantes.SVC_CATEGORIA_ARTICULO_CA, ConsultaConstantes.A_ID_CATEGORIA_ARTICULO_CA_ID_CATEGORIA_ARTICULO)
		.where(ConsultaConstantes.SC_ID_CONTRATO_ID_CONTRATO).setParameter(ConsultaConstantes.ID_CONTRATO, contrato.getIdContrato())
		.and("SCA.ID_ARTICULO = :idArticulo").setParameter("idArticulo", contrato.getIdArticulo()).and("CA.ID_CATEGORIA_ARTICULO = :idCategoriaArticulo")
		.setParameter("idCategoriaArticulo", contrato.getIdCategoriaArticulo());
		final String query = queryUtil.build();
		log.info(" consultarContratoModelo: " + query );
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		
		log.info(" TERMINO - consultarContratoModelo");
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
			q.agregarParametroValues("IND_ESTATUS", String.valueOf(0));
			query.append("$$").append(q.obtenerQueryInsertar());
		}
		log.info(" TERMINO - insertInventarioArticulo");
		return query.toString();
	}
	
	public DatosRequest actualizaArticulor(OrdenEntradaRequest ordenEntradaRequest, UsuarioDto usuarioDto) {
		log.info(" INICIO - actualizaArticulor");
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();

		final QueryHelper q = new QueryHelper("UPDATE SVT_ARTICULO");
		q.agregarParametroValues("CAN_UNIDAD", Integer.toString(ordenEntradaRequest.getCantidadUnidadArticulo()+ordenEntradaRequest.getNumArticulo()));
		q.agregarParametroValues(ConsultaConstantes.ID_USUARIO_MODIFICA, String.valueOf(usuarioDto.getIdUsuario()));
		q.agregarParametroValues(ConsultaConstantes.FEC_ACTUALIZACION, ConsultaConstantes.CURRENT_TIMESTAMP);
		q.addWhere("ID_ARTICULO = " + ordenEntradaRequest.getIdArticulo());
		
		String query = q.obtenerQueryActualizar();
		log.info(" actualizaArticulor: " + query);
		parametro.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(q.obtenerQueryActualizar().getBytes(StandardCharsets.UTF_8)));
		request.setDatos(parametro);
		log.info(" TERMINO - actualizaArticulor");
		return request;
	}

}