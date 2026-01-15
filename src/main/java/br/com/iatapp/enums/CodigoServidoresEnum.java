package br.com.iatapp.enums;

public enum CodigoServidoresEnum {
	
	SERVIDOR_DEV(1),
	SERVIDOR_PROD(2);
	
	private int codigoServidor;
	
	CodigoServidoresEnum(int codigoServidor) {
		this.codigoServidor = codigoServidor;
	}
	
	public int getCodigoServidor() {
		return codigoServidor;
	}
	
	public static CodigoServidoresEnum valueOf(int codigoServidor) {
		
		if (codigoServidor == SERVIDOR_PROD.getCodigoServidor()) {
			return SERVIDOR_PROD;
		}		
		if (codigoServidor == SERVIDOR_DEV.getCodigoServidor()) {
			return SERVIDOR_DEV;	
		}
		
		return SERVIDOR_DEV;
	}

}
