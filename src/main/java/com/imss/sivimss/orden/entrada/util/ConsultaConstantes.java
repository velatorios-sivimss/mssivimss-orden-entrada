package com.imss.sivimss.orden.entrada.util;

public class ConsultaConstantes {
	
	public static final String C_ID_CONTRATO_ID_CONTRATO = "C.ID_CONTRATO = :idContrato";
	public static final String ID_USUARIO_MODIFICA = "ID_USUARIO_MODIFICA";
	public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP()";
	public static final String FEC_ACTUALIZACION = "FEC_ACTUALIZACION";
	public static final String ID_USUARIO_ALTA = "ID_USUARIO_ALTA";
	public static final String SVT_ARTICULO_A = "SVT_ARTICULO A";
	public static final String SVT_CONTRATO_C = "SVT_CONTRATO C";
	public static final String ID_VELATORIO = "idVelatorio";
	public static final String ID_CONTRATO = "idContrato";
	public static final String SEPARADOR = "separador";
	public static final String ID_TABLA = "idTabla";
	public static final String FEC_ALTA = "FEC_ALTA";
	public static final String REPLACE = "replace";
	
	private ConsultaConstantes() {
		super();
	}
	
	public static Integer getIdVelatorio(Integer idVelatorio) {
		if(idVelatorio == null){
			return 0;
		}
		return idVelatorio;
	}

}
