/**
 * 
 */
package com.dolphinss.protocolo.rtsp;

import java.net.UnknownHostException;

import com.dolphinss.protocolo.sdp.Sdp;

/**
 * @author lobo
 * 
 */
public class Describe extends RespuestaRTSP {

	protected String rtp_session = "";
	protected String archivoSolicitado = "";
	protected String contentBase="";

	public Describe(int cseq) {
		super(cseq);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cs_rtsp.servidor.protocolo.rtp.RespuestaRTP#generarCuerpo()
	 */
	@Override
	protected void generarCuerpo() {
		Sdp sdp = new Sdp(archivoSolicitado);

		String cadenaSDP = "";
		try {
			cadenaSDP = CRLF2+sdp.getSdp();// ojo que aqui meto 2 saltos de linea porque así lo indica la especificación de este método
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cuerpo += "Content-base: "+contentBase + CRLF
		+ "Content-Type: application/sdp"+ CRLF 
		+ "Content-Length: "+cadenaSDP.length()
		+ cadenaSDP;

	}
	
	/**
	 * @return the archivoSolicitado
	 */
	public String getArchivoSolicitado() {
		return archivoSolicitado;
	}

	public void setArchivoSolicitado(String archivo) {
		archivoSolicitado = archivo;
	}

	/**
	 * @return the contentBase
	 */
	public String getContentBase() {
		return contentBase;
	}

	/**
	 * @param contentBase the contentBase to set
	 */
	public void setContentBase(String contentBase) {
		this.contentBase = contentBase;
	}
	
}
