/**
 * 
 */
package com.dolphinss.protocolo.rtsp;

import java.util.Random;

import com.dolphinss.Servidor;

/**
 * @author marcos.fermin
 * 
 */
public class Setup extends RespuestaRTSP {

	private int cliente_rtp, cliente_rtcp;
	
	private String cliente_ip="";
	
	private int[] interleaved;
	
	private String protocoloTransporte="";
	
	private String tipoSession="";

	/**
	 * @param cseq
	 */
	public Setup(int cseq) {
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
		// TODO Auto-generated method stub
		crearSessionId();
		cuerpo += "Session: " + session_id + CRLF
			+ "Transport: "+protocoloTransporte+";"+tipoSession+";";
		if(interleaved==null){
			cuerpo+="source="+Servidor.IP_SERVIDOR+";"+getPuertosMensaje();
		}else{
			cuerpo+=getIntervaledMensaje();
		}
	}
	
	private String getPuertosMensaje(){
		String r= "client_port="+ cliente_rtp + "-" + cliente_rtcp + ";" + 
		"server_port="+Servidor.PUERTO_RTSP_RTP[0]+"-"+Servidor.PUERTO_RTSP_RTP[1];
		Servidor.PUERTO_RTSP_RTP[0]+=Servidor.unidadAumentarPuertos;
		Servidor.PUERTO_RTSP_RTP[1]+=Servidor.unidadAumentarPuertos;
		return r;
	}
	
	private String getIntervaledMensaje(){
		return "client_ip="+cliente_ip+";interleaved="+interleaved[0]+"-"+interleaved[1];
	}

	/**
	 * Creates a random Session ID.
	 */
	private final void crearSessionId() {
		Random r = new Random();
		int id = r.nextInt();
		if (id < 0) {
			id *= -1;
		}
		if(crearNuevoSessionId){
			session_id = id;
		}
	}

	public void setPuertoCliente(int port) {
		cliente_rtp = port;
		cliente_rtcp = port + 1;
	}

	/**
	 * @return the protocoloTransporte
	 */
	public String getProtocoloTransporte() {
		return protocoloTransporte;
	}

	/**
	 * @param protocoloTransporte the protocoloTransporte to set
	 */
	public void setProtocoloTransporte(String protocoloTransporte) {
		this.protocoloTransporte = protocoloTransporte;
	}

	/**
	 * @return the tipoSession
	 */
	public String getTipoSession() {
		return tipoSession;
	}

	/**
	 * @param tipoSession the tipoSession to set
	 */
	public void setTipoSession(String tipoSession) {
		this.tipoSession = tipoSession;
	}

	/**
	 * @return the intervaled
	 */
	public int[] getIntervaled() {
		return interleaved;
	}

	/**
	 * @param intervaled the intervaled to set
	 */
	public void setIntervaled(int[] intervaled) {
		this.interleaved = intervaled;
	}

	/**
	 * @return the cliente_ip
	 */
	public String getCliente_ip() {
		return cliente_ip;
	}

	/**
	 * @param cliente_ip the cliente_ip to set
	 */
	public void setCliente_ip(String cliente_ip) {
		this.cliente_ip = cliente_ip;
	}
	
}
