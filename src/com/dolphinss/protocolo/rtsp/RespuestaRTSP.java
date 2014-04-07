package com.dolphinss.protocolo.rtsp;

import java.util.Date;

import com.dolphinss.Servidor;

/**
 * Clase abstracta para modelar las respuestas RTSP.
 * 
 * @version 1.0
 * @author mloobo@gmail.com
 *
 */
public abstract class RespuestaRTSP {
	/**
	 * La cadena de texto que representa la propia respuesta, compuesta
	 * por la cabecera y el cuerpo del mensaje.
	 */
	protected String respuesta="";
	
	/**
	 * El número de secuencia de mensaje enviado/recibido
	 */
	protected int cseq=0;
	
	/**
	 * El número de sesión. Se envía por primera vez en la respuesta al SETUP y es, en principio,
	 * un valor aleatorio.
	 */
	protected static int session_id = -1;
	
	/**
	 * Indica si que quiere que vuelva a crear un valor para la variable "session_id".
	 * En ocasiones, como las que el archivo o recurso solicitado tiene más de una pista, 
	 * es interesante que NO  se regenere dicho valor.
	 */
	protected boolean crearNuevoSessionId=true;
	
	/**
	 * La parte de la cadena de texto que representa el cuerpo del mensaje (sin la cabecera).
	 */
	protected String cuerpo="";
	
	/**
	 * CR = <US-ASCII CR, carriage return (13)>
	 * LF = <US-ASCII LF, linefeed (10)>
	 * CRLF = CR LF
	 */
	public static final String CRLF="\r\n";
	public static final String CRLF2="\r\n\r\n";
	public static final String SEP=" ";
	
	public RespuestaRTSP(int cseq){
		this.cseq=cseq;
	}
	
	@SuppressWarnings("deprecation")
	protected String ok(){
		return "RTSP/1.0"+SEP+"200"+SEP+"OK"+CRLF
		+cseq()+CRLF+
		"Date: "+new Date().toGMTString()+CRLF+
		"Server: "+getServer()+CRLF;
	}
	
	protected String cseq(){
		return "CSeq:"+SEP+getCseq();
	}
	
	/**
	 * @return the respuesta
	 */
	protected String getRespuesta() {
		return respuesta;
	}

	/**
	 * @param respuesta the respuesta to set
	 */
	protected void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	protected String getServer(){
		return Servidor.nombre+"/"+Servidor.version+" (Platform/"+Servidor.plataforma+"; Release/"+Servidor.release+")";
	}
	
	/**
	 * @return the cseq
	 */
	protected int getCseq() {
		return cseq;
	}

	/**
	 * @param cseq the cseq to set
	 */
	protected void setCseq(int cseq) {
		this.cseq = cseq;
	}

	/**
	 * @return the cuerpo
	 */
	protected String getCuerpo() {
		return cuerpo;
	}

	/**
	 * @param cuerpo the cuerpo to set
	 */
	protected void setCuerpo(String cuerpo) {
		this.cuerpo = cuerpo;
	}

	protected void generar(){
		respuesta+=ok();
		respuesta+=getCuerpo()+CRLF2;
		//es importante que las respuestas terminen con 2 saltos de línea.
	}
	
	protected abstract void generarCuerpo();
	
	public String toString(){
		generarCuerpo();
		generar();
		return respuesta;
	}
	
	public void crearSessionId(boolean bool){
		crearNuevoSessionId=bool;
	}
}
