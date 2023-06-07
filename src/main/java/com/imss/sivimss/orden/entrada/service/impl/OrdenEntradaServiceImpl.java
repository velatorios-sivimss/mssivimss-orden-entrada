package com.imss.sivimss.orden.entrada.service.impl;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.orden.entrada.beans.OrdenEntrada;
import com.imss.sivimss.orden.entrada.model.request.ContratoRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.service.OrdenEntradaService;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.LogUtil;
import com.imss.sivimss.orden.entrada.util.MensajeResponseUtil;
import com.imss.sivimss.orden.entrada.util.ProviderServiceRestTemplate;
import com.imss.sivimss.orden.entrada.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrdenEntradaServiceImpl  implements OrdenEntradaService {
	
	private static final String NO_SE_ENCONTRO_INFORMACION = "45"; // No se encontró información relacionada a tu
	private static final String ERROR_AL_EJECUTAR_EL_QUERY = "Error al ejecutar el query ";
	private static final String FALLO_AL_EJECUTAR_EL_QUERY = "Fallo al ejecutar el query: ";
	private static final String ERROR_INFORMACION = "52";  // Error al consultar la información.
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String CONSULTA = "consulta";
	private static final String ALTA = "alta";
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlModCatalogos;
	
	@Autowired
	private LogUtil logUtil;

	@Override
	public Response<Object> ultimoRegistroOrdenEntrada(DatosRequest request, Authentication authentication)
			throws IOException {
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar ultimo registro orden entrada ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().ultimoRegistroOrdenEntrada(request).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			String consulta = new OrdenEntrada().ultimoRegistroOrdenEntrada(request).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}
	
	@Override
	public Response<Object> consultarContratoProveedor(DatosRequest request, Authentication authentication) throws IOException {
		ContratoRequest contratoRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ContratoRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato proveedor ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarContratoProveedor(request, contratoRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			String consulta = new OrdenEntrada().consultarContratoProveedor(request, contratoRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarContratoArticulo(DatosRequest request, Authentication authentication)
			throws IOException {
		ContratoRequest contratoRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ContratoRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato articulo ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarContratoArticulo(request, contratoRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			String consulta = new OrdenEntrada().consultarContratoArticulo(request, contratoRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarDescripcionVelatorio(DatosRequest request, Authentication authentication) throws IOException {
		UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato articulo ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarDescripcionVelatorio(request, usuarioDto).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			String consulta = new OrdenEntrada().consultarDescripcionVelatorio(request, usuarioDto).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> insertarOrdenEntrada(DatosRequest request, Authentication authentication) throws IOException {
		OrdenEntradaRequest ordenEntradaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
		UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try {
				logUtil.crearArchivoLog(Level.INFO.toString(),this.getClass().getSimpleName(),this.getClass().getPackage().toString()," insertar orden entrada ", ALTA,authentication);
				return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().insertarOrdenEntrada(ordenEntradaRequest, usuarioDto).getDatos(),
						urlModCatalogos.concat("/crearMultiple"), authentication));
        } catch (Exception e) {
            String consulta = new OrdenEntrada().insertarOrdenEntrada(ordenEntradaRequest, usuarioDto).getDatos().get(AppConstantes.QUERY).toString();
            String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
            log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
            logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, ALTA, authentication);
            throw new IOException("5", e.getCause());
        }
	}

	@Override
	public Response<Object> consultarOrdenEntrada(DatosRequest request, Authentication authentication)
			throws IOException {
		OrdenEntradaRequest ordenEntradaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
		UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar orden entrada ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarOrdenEntrada(request, ordenEntradaRequest, usuarioDto).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication),NO_SE_ENCONTRO_INFORMACION);

		} catch (Exception e) {
			String consulta = new OrdenEntrada().consultarOrdenEntrada(request, ordenEntradaRequest, usuarioDto).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

}
