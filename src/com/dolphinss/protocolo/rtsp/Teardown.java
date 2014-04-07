package com.dolphinss.protocolo.rtsp;

public class Teardown extends RespuestaRTSP {

	public Teardown(int cseq) {
		super(cseq);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void generar(){
		respuesta+=ok();
		respuesta+=getCuerpo()+CRLF;
		//es importante que las respuestas terminen con 2 saltos de línea,... 
		//... pero en esta le quito uno porque sino van 3 saltos.
	}
	
	@Override
	protected void generarCuerpo() {
		// TODO Auto-generated method stub
		cuerpo+="";//no lleva cuerpo
	}

}
