package com.imss.sivimss.orden.entrada.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.orden.entrada.beans.OrdenEntrada;
import com.imss.sivimss.orden.entrada.model.request.ContratoRequest;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.model.response.OrdenEntradaResponse;
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
	
	@Autowired 
	private ModelMapper modelMapper;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlModCatalogos;
	
	@Autowired
	private LogUtil logUtil;

	@Override
	public Response<Object> consultarContratoProveedor(DatosRequest request, Authentication authentication)
			throws IOException {
		ContratoRequest contratoRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ContratoRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato proveedor ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarContratoProveedor(request, contratoRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			e.printStackTrace();
			String consulta = new OrdenEntrada().consultarContratoProveedor(request, contratoRequest).getDatos().get(AppConstantes.QUERY).toString();
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
				List<OrdenEntradaResponse> ordenEntradaResponse;
				Response<Object> response = providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultaFolioOrdenEntrada(request, ordenEntradaRequest, usuarioDto).getDatos(),
						urlModCatalogos.concat(CONSULTA_GENERICA), authentication);
				if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
						ordenEntradaResponse=Arrays.asList(modelMapper.map(response.getDatos(), OrdenEntradaResponse[].class));
						ordenEntradaRequest.setIdOrdenEntrada(ordenEntradaResponse.get(0).getIdOrdenEntrada());
						ordenEntradaRequest.setNumFolioOrdenEntrada(ordenEntradaResponse.get(0).getNumFolio());
						ordenEntradaRequest.setIdInventarioArticulo(ordenEntradaResponse.get(0).getIdInventarioArticulo());
						ordenEntradaRequest.setCantidadUnidadArticulo(ordenEntradaResponse.get(0).getCantidadUnidadArticulo());
						response = providerRestTemplate.consumirServicioObject(new OrdenEntrada().insertarOrdenEntrada(ordenEntradaRequest, usuarioDto).getDatos(),urlModCatalogos.concat("/crearMultiple"), authentication);
						if(response.getCodigo()==200) {
							return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().actualizaArticulor(ordenEntradaRequest, usuarioDto).getDatos(),urlModCatalogos.concat("/actualizar"), authentication));
						}
						
				}
				return MensajeResponseUtil.mensajeResponseObject(response);
        } catch (Exception e) {
        	e.printStackTrace();
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
			e.printStackTrace();
			String consulta = new OrdenEntrada().consultarOrdenEntrada(request, ordenEntradaRequest, usuarioDto).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarContratoPorVelatorio(DatosRequest request, Authentication authentication)
			throws IOException {
		 UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try {
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato por velatorio ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarContratoPorVelatorio(request,  usuarioDto).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			e.printStackTrace();
			String consulta = new OrdenEntrada().consultarContratoPorVelatorio(request, usuarioDto).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarContratoCategoria(DatosRequest request, Authentication authentication)
			throws IOException {
		ContratoRequest contratoRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ContratoRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato categoria ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarContratoCategoria(request, contratoRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			e.printStackTrace();
			String consulta = new OrdenEntrada().consultarContratoCategoria(request, contratoRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarContratoModelo(DatosRequest request, Authentication authentication)
			throws IOException {
		ContratoRequest contratoRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ContratoRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato modelo ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarContratoModelo(request, contratoRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			e.printStackTrace();
			String consulta = new OrdenEntrada().consultarContratoModelo(request, contratoRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> consultarContratoCosto(DatosRequest request, Authentication authentication)
			throws IOException {
		ContratoRequest contratoRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), ContratoRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato costo ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new OrdenEntrada().consultarContratoCosto(request, contratoRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			e.printStackTrace();
			String consulta = new OrdenEntrada().consultarContratoCosto(request, contratoRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

}