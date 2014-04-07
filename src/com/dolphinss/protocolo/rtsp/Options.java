/**
 * 
 */
package com.dolphinss.protocolo.rtsp;

/**
 * @author lobo
 *
 */
public class Options extends RespuestaRTSP {

	public Options(int cseq) {
		super(cseq);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see com.cs_rtsp.servidor.protocolo.rtp.RespuestaRTP#generarCuerpo()
	 */
	@Override
	protected void generarCuerpo() {
		// TODO Auto-generated method stub
		cuerpo="Public:DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE"/*+SL*/;
	}

}
