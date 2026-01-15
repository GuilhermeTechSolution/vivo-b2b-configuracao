package br.com.iatapp.model;

public class SerialAtrelada {
	
	public String strTipoInterface;
	public String strInterface;
	public boolean active;
	public boolean needMonitor;    // Flag para indicar que deve monitorar (runtime)
	public String comandoErrorTest;
	public String comandoErrorTestSec;
	public String comandoClear;
	public String comandoShowLogging;
	public String comandoShowInterfaces;
	public String comandoShowControllers;
	public String comandoCpeCrcWan;
	public boolean statusDownDown;
	public String E1;
	public boolean isMassiva;
	public String massivaReason;
	
	public String[] resultadoTesteErro = new String[2];       // [0] código [1] Descricao
	public String[] resultadoShowLogging = new String[2];     // [0] código [1] Descricao
	public String[] resultadoCpeCrcWan = new String[2];       // [0] código [1] Descricao
	public String[] resultadoShowInterfaces = new String[2];  // [0] código [1] Descricao
	public String[] resultadoShowControllers = new String[2]; // [0] código [1] Descricao
	
	public SerialAtrelada (String strTipoInterface, String strInterface, boolean active) {
		this.strTipoInterface = strTipoInterface;
		this.strInterface = strInterface;
		this.active = active;
	}
}
