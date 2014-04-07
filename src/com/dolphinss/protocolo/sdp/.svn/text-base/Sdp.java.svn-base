package com.dolphinss.protocolo.sdp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.dolphinss.Servidor;
import com.dolphinss.protocolo.rtsp.RespuestaRTSP;

/**
 * Clase para definir la parte SDP de una respuesta a DESCRIBE.
 * 
 * @version 1.0
 * @author mloobo@gmail.com
 *
 */
public class Sdp {
	/**
	 * Define el nombre del archivo o recurso solicitado por el cliente.
	 * En el caso de ser un archivo físico, se precederá de la cadena "file:"
	 */
	private String archivo = "";
	
	private boolean javasound=false;
	
	private boolean webcam=false;
	
	private boolean screen=false;
	
	private boolean file=false;
	
	private int puertoClienteAudio=Servidor.PUERTO_CLIENTE_AUDIO;
	private int puertoClienteVideo=Servidor.PUERTO_CLIENTE_VIDEO;
	
	/**
	 * Objeto para obtener información de formatos del archivo solicitado.
	 */
	private InfoArchivo info;
	
	/**
	 * Valor del rango de pistas del archivo solicitado por el cliente.
	 */
	private double TrackRange;
	
	private String ip="";

	/**
	 * Constructor
	 * @param archivo El nombre del archivo o recurso solicitado por el cliente.
	 */
	public Sdp(String archivo) {
		if(archivo.compareToIgnoreCase("sonido")==0){
			javasound=true;
			this.archivo = "javasound://0";
		}else{
			if(archivo.compareToIgnoreCase("webcam")==0){
				webcam=true;
				this.archivo = "vfw://0";
			}else{
				if(archivo.compareToIgnoreCase("screen")==0){
					screen=true;
					this.archivo = "screen://0,0,1280,800/25";
				}else{
					this.archivo = "file:"+archivo;
					getInfoArchivo();
					file=true;
				}
			}
		}
		
	}

	/**
	 * Método que obtiene la información del archivo o recurso solicitado.
	 */
	private void getInfoArchivo() {
		info = new InfoArchivo(this.archivo);
		if (info.makeInfos()) {
			while (!info.canRead()) {
			}
			TrackRange = info.getTrackTime();
		}
	}

	/**
	 * Definición y valores del <payload type>
	 * @see http://www.ietf.org/rfc/rfc1890.txt
	 * @return El mensaje SDP para la respuesta a un DESCRIBE.
	 * @throws UnknownHostException
	 */
	public String getSdp() throws UnknownHostException {
		StringBuffer buf = new StringBuffer();

		ip = Servidor.IP_SERVIDOR;
		
		buf.append("v=0"+RespuestaRTSP.CRLF);
		buf.append("o="+Servidor.nombre+RespuestaRTSP.CRLF);
		buf.append("i=Descripcion de la sesion"+RespuestaRTSP.CRLF);
		buf.append("e=Marcos Fermin Lobo <uo174203@uniovi.es>"+RespuestaRTSP.CRLF);
		buf.append("s="+archivo+RespuestaRTSP.CRLF);
		buf.append("t=0 0"+RespuestaRTSP.CRLF);
		buf.append("a=charset:ISO-8859-1"+RespuestaRTSP.CRLF);

		if(file){
			buf.append(getSDPFile());
		}else{
			if(javasound){
				buf.append(getSDPJavasound());
			}else{
				if(webcam){
					buf.append(getSDPWebcam());
				}else{
					buf.append(getSDPantalla());
				}
			}
		}
		

		return buf.toString();
	}
		
	private StringBuffer getSDPFile() throws UnknownHostException{
		StringBuffer buf = new StringBuffer();
		boolean metioAudio=false;
		TrackRange += 1.0;
		int track_range = (int) TrackRange;
		
		String rango = "a=range:npt=0-";
		rango += Integer.toString(track_range);

		/**
		 * PENDIENTE
		 * Tanto para el audio como para el vídeo que hacer que la cadena del
		 * SDP se construya en función del formato (payload type) y la frecuencia (clock rate)
		 * que tenga el fichero que solicita el cliente.
		 * De momento, está puesto de manera fija de la siguiente manera:
		 * 		* Audio: payload type=14 (MPA) a 90000
		 * 		* Video: payload type=32 (MPV) a 90000 
		 */
		
		// audio
		if (info.getAudio()) {
			//m=<media> <port> <proto> <fmt>
			buf.append("m=audio "+puertoClienteAudio+" RTP/AVP 14"+RespuestaRTSP.CRLF); // explicacion en pagina 80 de RFC
			//a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
			buf.append("a=rtpmap:14 MPA/90000"+RespuestaRTSP.CRLF);
			buf.append("a=control:rtsp://");
			buf.append(ip);
			buf.append("/audio"+RespuestaRTSP.CRLF);
			buf.append("a=mimetype: audio/MPA"+RespuestaRTSP.CRLF);
			buf.append(rango);
			metioAudio=true;
		}

		// video
		if (info.getVideo()) {
			if(metioAudio){
				buf.append(RespuestaRTSP.CRLF);
			}
			buf.append("m=video "+puertoClienteVideo+" RTP/AVP 32"+RespuestaRTSP.CRLF);
			buf.append("a=rtpmap:32 MPV/90000"+RespuestaRTSP.CRLF);
			buf.append("a=control:rtsp://");
			buf.append(ip);
			buf.append("/video"+RespuestaRTSP.CRLF);
			buf.append("a=mimetype: video/MPV"+RespuestaRTSP.CRLF);
			buf.append(rango);
		}
		return buf;
	}
	
	private StringBuffer getSDPJavasound(){
		StringBuffer buf = new StringBuffer();
		String rango = "a=range:npt=0-";
		//m=<media> <port> <proto> <fmt>
		buf.append("m=audio "+puertoClienteAudio+" RTP/AVP 14"+RespuestaRTSP.CRLF); // explicacion en pagina 80 de RFC
		//a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
		buf.append("a=rtpmap:14 MPA/90000"+RespuestaRTSP.CRLF);
		buf.append("a=control:rtsp://");
		buf.append(ip);
		buf.append("/audio"+RespuestaRTSP.CRLF);
		buf.append("a=mimetype: audio/MPA"+RespuestaRTSP.CRLF);
		buf.append(rango);
		return buf;
	}
	
	private StringBuffer getSDPantalla(){
		StringBuffer buf = new StringBuffer();
		String rango = "a=range:npt=0-100";
		buf.append("m=video "+puertoClienteVideo+" RTP/AVP 26"+RespuestaRTSP.CRLF);
		buf.append("a=rtpmap:26 JPEG/90000"+RespuestaRTSP.CRLF);
		buf.append("a=control:rtsp://");
		buf.append(ip);
		buf.append("/video"+RespuestaRTSP.CRLF);
		buf.append("a=mimetype: video/JPEG"+RespuestaRTSP.CRLF);
		buf.append(rango);
		return buf;
	}
	
	private StringBuffer getSDPWebcam(){
		StringBuffer buf = new StringBuffer();
		String rango = "a=range:npt=0-100";
		buf.append("m=video "+puertoClienteVideo+" RTP/AVP 26"+RespuestaRTSP.CRLF);
		buf.append("a=rtpmap:26 JPEG/90000"+RespuestaRTSP.CRLF);
		buf.append("a=control:rtsp://");
		buf.append(ip);
		buf.append("/video"+RespuestaRTSP.CRLF);
		buf.append("a=mimetype: video/JPEG"+RespuestaRTSP.CRLF);
		buf.append(rango);
		return buf;
	}

	/**
	 * Devuelve el nombre del archivo o recurso.
	 * En el caso de ser un archivo físico, se precederá de la cadena "file:"
	 * @return
	 */
	public String getArchivo() {
		return archivo;
	}

	/**
	 * Establece el nombre del archivo o recurso.
	 * En el caso de ser un archivo físico, se precederá de la cadena "file:"
	 * @param archivo
	 */
	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

	/**
	 * @return the puertoClienteAudio
	 */
	public int getPuertoClienteAudio() {
		return puertoClienteAudio;
	}

	/**
	 * @param puertoClienteAudio the puertoClienteAudio to set
	 */
	public void setPuertoClienteAudio(int puertoClienteAudio) {
		this.puertoClienteAudio = puertoClienteAudio;
	}

	/**
	 * @return the puertoClienteVideo
	 */
	public int getPuertoClienteVideo() {
		return puertoClienteVideo;
	}

	/**
	 * @param puertoClienteVideo the puertoClienteVideo to set
	 */
	public void setPuertoClienteVideo(int puertoClienteVideo) {
		this.puertoClienteVideo = puertoClienteVideo;
	}
	
}
