package com.imss.sivimss.orden.entrada.util;

public class ConsultaConstantes {
	
	public static final String A_ID_CATEGORIA_ARTICULO_CA_ID_CATEGORIA_ARTICULO = "A.ID_CATEGORIA_ARTICULO  = CA.ID_CATEGORIA_ARTICULO";
	public static final String SC_ID_CONTRATO_SCA_ID_CONTRATO = "SC.ID_CONTRATO = SCA.ID_CONTRATO";
	public static final String C_NUM_CONTRATO_NUM_CONTRATO = "C.NUM_CONTRATO = :numContrato";
	public static final String A_ID_ARTICULO_SCA_ID_ARTICULO = "A.ID_ARTICULO = SCA.ID_ARTICULO";
	public static final String SC_ID_CONTRATO_ID_CONTRATO = "SC.ID_CONTRATO  = :idContrato";
	public static final String SVT_CONTRATO_ARTICULOS_SCA = "SVT_CONTRATO_ARTICULOS SCA";
	public static final String SVC_CATEGORIA_ARTICULO_CA = "SVC_CATEGORIA_ARTICULO CA";
	public static final String ID_USUARIO_MODIFICA = "ID_USUARIO_MODIFICA";
	public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP()";
	public static final String FEC_ACTUALIZACION = "FEC_ACTUALIZACION";
	public static final String ID_USUARIO_ALTA = "ID_USUARIO_ALTA";
	public static final String SVT_CONTRATO_SC = "SVT_CONTRATO SC";
	public static final String SVT_ARTICULO_A = "SVT_ARTICULO A";
	public static final String SVT_CONTRATO_C = "SVT_CONTRATO C";
	public static final String ID_VELATORIO = "idVelatorio";
	public static final String NUM_CONTRATO= "numContrato";
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
	
	public static String filter(String word) {

	     StringBuilder builder = new StringBuilder();

	    // Se crea un arreglo que contiene todas las palabras
	    String palabras[] = word.split(" ");

	        //Se recorren todas las palabras del arreglo
	        for (String palabra : palabras) {
	         //si la longitud de la palabra es igual o mayor a 3 caracteres 
	         //se extraen los primeros 3
	            if (palabra.length() >= 3) {
	                builder.append(palabra.substring(0, 3));
	            } else {
	         //si la longitud es menor a 3 se agrega la palagra completa
	                builder.append(palabra);
	            }
	        }
	      //se retorna el string formado por las iniciales
	      return builder.toString();
	}

}
