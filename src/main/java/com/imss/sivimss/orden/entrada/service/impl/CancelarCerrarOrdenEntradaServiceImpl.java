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
import com.imss.sivimss.orden.entrada.beans.CancelarCerrarOrdenEntrada;
import com.imss.sivimss.orden.entrada.model.request.OrdenEntradaRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.model.response.OrdenEntradaResponse;
import com.imss.sivimss.orden.entrada.service.CancelarCerrarOrdenEntradaService;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.LogUtil;
import com.imss.sivimss.orden.entrada.util.MensajeResponseUtil;
import com.imss.sivimss.orden.entrada.util.ProviderServiceRestTemplate;
import com.imss.sivimss.orden.entrada.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CancelarCerrarOrdenEntradaServiceImpl implements CancelarCerrarOrdenEntradaService {
	
	private static final String NO_SE_ENCONTRO_INFORMACION = "45"; // No se encontró información relacionada a tu
	private static final String ERROR_AL_EJECUTAR_EL_QUERY = "Error al ejecutar el query ";
	private static final String FALLO_AL_EJECUTAR_EL_QUERY = "Fallo al ejecutar el query: ";
	private static final String ERROR_INFORMACION = "52";  // Error al consultar la información.
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired 
	private ModelMapper modelMapper;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlModCatalogos;
	
	@Autowired
	private LogUtil logUtil;
	
	@Override
	public Response<Object> consultarDetalleOrdenEntrada(DatosRequest request, Authentication authentication)
			throws IOException {
		OrdenEntradaRequest ordenEntradaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consulta detalle orden entrada ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicioObject(new CancelarCerrarOrdenEntrada().consultarDetalleOrdenEntrada(request, ordenEntradaRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication),NO_SE_ENCONTRO_INFORMACION);

		} catch (Exception e) {
			String consulta = new CancelarCerrarOrdenEntrada().consultarDetalleOrdenEntrada(request, ordenEntradaRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

	@Override
	public Response<Object> actualizarOrdenEntrada(DatosRequest request, Authentication authentication)
			throws IOException {
		OrdenEntradaRequest ordenEntradaRequest= new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
		UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try {
				logUtil.crearArchivoLog(Level.INFO.toString(),this.getClass().getSimpleName(),this.getClass().getPackage().toString()," actualizar inventario articulo ", MODIFICACION, authentication);
				List<OrdenEntradaResponse> ordenEntradaResponse;
				Response<Object> response = providerRestTemplate.consumirServicioObject(new CancelarCerrarOrdenEntrada().consultarCantidadArticulo(request, ordenEntradaRequest).getDatos(),
						urlModCatalogos.concat(CONSULTA_GENERICA), authentication);
				if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
					ordenEntradaResponse=Arrays.asList(modelMapper.map(response.getDatos(), OrdenEntradaResponse[].class));
					log.info(ordenEntradaResponse.get(0).getIdArticulo()+":"+ordenEntradaResponse.get(0).getCantidadUnidadArticulo()+":"+ordenEntradaResponse.get(0).getCantidadInventarioArticulo());
					ordenEntradaRequest.setIdArticulo(ordenEntradaResponse.get(0).getIdArticulo());
					ordenEntradaRequest.setCantidadUnidadArticulo(ordenEntradaResponse.get(0).getCantidadUnidadArticulo());
					ordenEntradaRequest.setCantidadInventarioArticulo(ordenEntradaResponse.get(0).getCantidadInventarioArticulo());
					return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicio(new CancelarCerrarOrdenEntrada().actualizarOrdenEntrada(ordenEntradaRequest, usuarioDto),urlModCatalogos.concat("/actualizar/multiples"),authentication));
				}
				return MensajeResponseUtil.mensajeResponseObject(response);
        } catch (Exception e) {
            String consulta = new CancelarCerrarOrdenEntrada().actualizarOrdenEntrada(ordenEntradaRequest, usuarioDto).toString();
            String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
            log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
            logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, MODIFICACION, authentication);
            throw new IOException("5", e.getCause());
        }
	}

	@Override
	public Response<Object> verificarOrdenEntradaRelacionOrdenServicio(DatosRequest request,
			Authentication authentication) throws IOException {
		OrdenEntradaRequest ordenEntradaRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), OrdenEntradaRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " verifica orden entrada relacion orden servicio ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicioObject(new CancelarCerrarOrdenEntrada().verificaOrdenEntradaRelacionOrdenServicio(request, ordenEntradaRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication),NO_SE_ENCONTRO_INFORMACION);

		} catch (Exception e) {
			String consulta = new CancelarCerrarOrdenEntrada().consultarDetalleOrdenEntrada(request, ordenEntradaRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}

}
