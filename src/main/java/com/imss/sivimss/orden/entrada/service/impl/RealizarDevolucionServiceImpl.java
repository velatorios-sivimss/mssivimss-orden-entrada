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
import com.imss.sivimss.orden.entrada.beans.RealizarDevolucionArticulo;
import com.imss.sivimss.orden.entrada.model.request.InventarioArticuloRequest;
import com.imss.sivimss.orden.entrada.model.request.UsuarioDto;
import com.imss.sivimss.orden.entrada.model.response.OrdenEntradaResponse;
import com.imss.sivimss.orden.entrada.service.RealizarDevolucionService;
import com.imss.sivimss.orden.entrada.util.AppConstantes;
import com.imss.sivimss.orden.entrada.util.DatosRequest;
import com.imss.sivimss.orden.entrada.util.LogUtil;
import com.imss.sivimss.orden.entrada.util.MensajeResponseUtil;
import com.imss.sivimss.orden.entrada.util.ProviderServiceRestTemplate;
import com.imss.sivimss.orden.entrada.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RealizarDevolucionServiceImpl implements RealizarDevolucionService {
	
	private static final String ERROR_AL_EJECUTAR_EL_QUERY = "Error al ejecutar el query ";
	private static final String FALLO_AL_EJECUTAR_EL_QUERY = "Fallo al ejecutar el query: ";
	private static final String ERROR_INFORMACION = "52";  // Error al consultar la información.
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Value("${endpoints.mod-catalogos}")
	private String urlModCatalogos;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired 
	private ModelMapper modelMapper;
	
	@Override
	public Response<Object> consultarFolioArticulo(DatosRequest request, Authentication authentication)throws IOException {
		InventarioArticuloRequest inventarioArticuloRequest = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), InventarioArticuloRequest.class);
		try {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), " consultar contrato articulo ", CONSULTA, authentication);

			return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicioObject(new RealizarDevolucionArticulo().consultarFolioArticulo(request, inventarioArticuloRequest).getDatos(),
					urlModCatalogos.concat(CONSULTA_GENERICA), authentication));

		} catch (Exception e) {
			e.printStackTrace();
			String consulta = new RealizarDevolucionArticulo().consultarFolioArticulo(request, inventarioArticuloRequest).getDatos().get(AppConstantes.QUERY).toString();
			String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, CONSULTA,
					authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
	}
	
	@Override
	public Response<Object> actualizarInventarioArticulo(DatosRequest request, Authentication authentication) throws IOException {
		InventarioArticuloRequest inventarioArticuloRequest= new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), InventarioArticuloRequest.class);
		UsuarioDto usuarioDto = new Gson().fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try {
				logUtil.crearArchivoLog(Level.INFO.toString(),this.getClass().getSimpleName(),this.getClass().getPackage().toString()," actualizar inventario articulo ", MODIFICACION, authentication);
				List<OrdenEntradaResponse> ordenEntradaResponse;
				Response<Object> response =  providerRestTemplate.consumirServicioObject(new RealizarDevolucionArticulo().consultarOrdenEntrada(request, inventarioArticuloRequest).getDatos(),
						urlModCatalogos.concat(CONSULTA_GENERICA), authentication);
				if (response.getCodigo()==200 && !response.getDatos().toString().contains("[]")) {
					ordenEntradaResponse = Arrays.asList(modelMapper.map(response.getDatos(), OrdenEntradaResponse[].class));
					inventarioArticuloRequest.setNumArticulo(ordenEntradaResponse.get(0).getNumArticulo());
					return MensajeResponseUtil.mensajeResponseObject(providerRestTemplate.consumirServicio(new RealizarDevolucionArticulo().actualizarInventarioArticulo(inventarioArticuloRequest, usuarioDto),urlModCatalogos.concat("/actualizar/multiples"),authentication));
				}
				return MensajeResponseUtil.mensajeResponseObject(response);
        } catch (Exception e) {
        	e.printStackTrace();
            String consulta = new RealizarDevolucionArticulo().actualizarInventarioArticulo(inventarioArticuloRequest, usuarioDto).toString();
            String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
            log.error(ERROR_AL_EJECUTAR_EL_QUERY + decoded);
            logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), FALLO_AL_EJECUTAR_EL_QUERY + decoded, MODIFICACION, authentication);
            throw new IOException("5", e.getCause());
        }
	}

}
