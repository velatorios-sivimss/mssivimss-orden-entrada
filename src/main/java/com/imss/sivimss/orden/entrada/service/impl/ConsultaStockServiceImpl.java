package com.imss.sivimss.orden.entrada.service.impl;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.orden.entrada.beans.ConsultaStock;
import com.imss.sivimss.orden.entrada.model.request.CategoriaRequest;
import com.imss.sivimss.orden.entrada.model.request.ConsultaStockRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.service.ConsultaStockService;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.LogUtil;
import com.imss.sivimss.orden.entrada.util.MensajeResponseUtil;
import com.imss.sivimss.orden.entrada.util.ProviderServiceRestTemplate;
import com.imss.sivimss.orden.entrada.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConsultaStockServiceImpl implements ConsultaStockService {
	
	private static final String NO_SE_ENCONTRO_INFORMACION = "45"; // No se encontró información relacionada a tu
	private static final String ERROR_AL_EJECUTAR_EL_QUERY = "Error al ejecutar el query ";
	private static final String FALLO_AL_EJECUTAR_EL_QUERY = "Fallo al ejecutar el query: ";
	private static final String ERROR_INFORMACION = "52";  // Error al consultar la información.
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String CONSULTA_PAGINADO = "/paginado";
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlModCatalogos;
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${formato_fecha}")
	private String formatoFecha;

	@Override
	public Response<Object> consultarOrdenEntradaPorVelatorio(DatosRequest request, Authentication authentication)
			throws IOException {
		String consulta = "";
		try {
			OrdenEntradaRequest ordenEntradaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar orden entrada por velatorio ", CONSULTA, authentication);

			 consulta = new ConsultaStock().consultarOrdenEntradaPorVelatorio(request, ordenEntradaRequest).getDatos().get(AppConstantes.QUERY).toString();
			
			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new ConsultaStock().consultarOrdenEntradaPorVelatorio(request,  ordenEntradaRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarDescripcionCategoria(DatosRequest request, Authentication authentication)
			throws IOException {
		String consulta = "";
		try {
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar descripcion categoria ", CONSULTA, authentication);

			CategoriaRequest categoriaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), CategoriaRequest.class);
			
			consulta = new ConsultaStock().consultarDescripcionCategoria(request, categoriaRequest).getDatos().get(AppConstantes.QUERY).toString();
			
			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new ConsultaStock().consultarDescripcionCategoria(request, categoriaRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}


	@Override
	public Response<Object> consultarTipoAsignacionArticulo(DatosRequest request, Authentication authentication)
			throws IOException {
		String consulta = "";
		try {
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar tipo asignacion articulo ", CONSULTA, authentication);

			consulta = new ConsultaStock().consultarTipoAsignacionArticulo(request).getDatos().get(AppConstantes.QUERY).toString();
			
			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new ConsultaStock().consultarTipoAsignacionArticulo(request).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarStock(DatosRequest request, Authentication authentication) throws IOException {
		String consulta = "";
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar stock", CONSULTA, authentication);
			
			ConsultaStockRequest consultaStockRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ConsultaStockRequest.class);
			
			consulta = new ConsultaStock().consultarStock(request, consultaStockRequest, formatoFecha).getDatos().get(AppConstantes.QUERY).toString();
			
			Response<Object> response = providerRestTemplate.consumirServicioObject(new ConsultaStock().consultarStock(request, consultaStockRequest, formatoFecha).getDatos(),
					urlModCatalogos.concat(CONSULTA_PAGINADO), authentication);
			if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
				return MensajeResponseUtil.mensajeResponseObject(response);
			}

			return MensajeResponseUtil.mensajeResponse(response,NO_SE_ENCONTRO_INFORMACION );

		} catch (Exception e) {
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarProveedor(DatosRequest request, Authentication authentication) throws IOException {
		String consulta = "";
		try {
			OrdenEntradaRequest ordenEntradaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar orden entrada por velatorio ", CONSULTA, authentication);

			 consulta = new ConsultaStock().consultarProveedor(request, ordenEntradaRequest).getDatos().get(AppConstantes.QUERY).toString();
			
			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new ConsultaStock().consultarProveedor(request,  ordenEntradaRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

}
