package com.dolphinss.protocolo.rtsp;

public class Play extends RespuestaRTSP {

	protected String rango="";
	
	public Play(int cseq) {
		super(cseq);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void generarCuerpo() {
		// TODO Auto-generated method stub
		cuerpo+="Session: " + session_id+CRLF+
		"Range: npt="+rango;
	}

	/**
	 * @return the range
	 */
	public String getRango() {
		return rango;
	}

	/**
	 * @param rango the range to set
	 */
	public void setRango(String rango) {
		this.rango = rango;
	}
	
	
}
