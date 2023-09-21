package com.imss.sivimss.orden.entrada.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.orden.entrada.beans.ConsultaStock;
import com.imss.sivimss.orden.entrada.beans.OrdenEntrada;
import com.imss.sivimss.orden.entrada.model.request.ConsultaStockRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.service.GeneraReporteService;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.LogUtil;
import com.imss.sivimss.orden.entrada.util.MensajeResponseUtil;
import com.imss.sivimss.orden.entrada.util.ProviderServiceRestTemplate;
import com.imss.sivimss.orden.entrada.util.Response;



@Service
public class GeneraReporteServiceImpl  implements GeneraReporteService {
	
	private static final String ERROR_AL_DESCARGAR_DOCUMENTO= "64"; // Error en la descarga del documento.Intenta nuevamente.
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlConsultar;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Value("${reporte.consulta-orden-entrada}")
	private String consultaOrdenEntrada;
	
	@Value("${reporte.consulta-stock}")
	private String consultaStock;
	
	@Autowired
	private LogUtil logUtil;
	
	private static final Logger log = LoggerFactory.getLogger(GeneraReporteServiceImpl.class);
	
	@Override
	public Response<Object> generarDocumentoConsultaOrdenEntrada(DatosRequest request, Authentication authentication)
			throws IOException {
		try {
			OrdenEntradaRequest ordenEntradaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
			Map<String, Object> envioDatos = generaReporteConsultaOrdenEntrada(ordenEntradaRequest, new OrdenEntrada().condicionConsultaOrdenEntrada(ordenEntradaRequest));
			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication), ERROR_AL_DESCARGAR_DOCUMENTO);
		} catch (Exception e) {
			log.error("Error.. {}", e.getMessage());
            logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "Fallo al ejecutar el reporte : " + e.getMessage(), CONSULTA, authentication);
            throw new IOException("64", e.getCause());
		}
	}

	@Override
	public Response<Object> generarDocumentoConsultaStock(DatosRequest request, Authentication authentication)
			throws IOException {
		try {
			ConsultaStockRequest consultaStockRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ConsultaStockRequest.class);
			Map<String, Object> envioDatos = generaReporteConsultaStock(consultaStockRequest, new ConsultaStock().condicionconsultarStocka(consultaStockRequest));
			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication), ERROR_AL_DESCARGAR_DOCUMENTO);
		} catch (Exception e) {
			log.error("Error.. {}", e.getMessage());
            logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), "Fallo al ejecutar el reporte : " + e.getMessage(), CONSULTA, authentication);
            throw new IOException("64", e.getCause());
		}
	}
	
	private Map<String, Object> generaReporteConsultaOrdenEntrada(OrdenEntradaRequest ordenEntradaRequest, String query) {
		log.info(" INICIO - generaReporteConsultaOrdenEntrada : ");
		 
		Map<String, Object> envioDatos = new HashMap<>();
		envioDatos.put("version", ordenEntradaRequest.getVersion());
		envioDatos.put("condicion", query);
		envioDatos.put("tipoReporte", ordenEntradaRequest.getTipoReporte());
		envioDatos.put("rutaNombreReporte", consultaOrdenEntrada);
		if(ordenEntradaRequest.getTipoReporte().equals("xls")) {
			log.info(" entro xls");
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}
		
		log.info(" TERMINO - generaReporteConsultaOrdenEntrada");
		return envioDatos;
	}
	
	private Map<String, Object> generaReporteConsultaStock(ConsultaStockRequest consultaStockRequest, String query) {
		log.info(" INICIO - generaReporteConsultaStock : ");
		 
		Map<String, Object> envioDatos = new HashMap<>();
		envioDatos.put("version", consultaStockRequest.getVersion());
		envioDatos.put("condicion", query);
		envioDatos.put("tipoReporte", consultaStockRequest.getTipoReporte());
		envioDatos.put("rutaNombreReporte", consultaStock);
		if(consultaStockRequest.getTipoReporte().equals("xls")) {
			log.info(" entro xls");
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}
		
		log.info(" TERMINO - generaReporteConsultaStock");
		return envioDatos;
	}

}
