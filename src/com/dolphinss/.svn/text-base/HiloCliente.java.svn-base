package com.dolphinss;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import com.dolphinss.protocolo.rtp.UnicastRtp;
import com.dolphinss.protocolo.rtp.UnicastRtpPantalla;
import com.dolphinss.protocolo.rtp.UnicastRtpWebcam;
import com.dolphinss.protocolo.rtsp.Describe;
import com.dolphinss.protocolo.rtsp.ErrorRTSP;
import com.dolphinss.protocolo.rtsp.Options;
import com.dolphinss.protocolo.rtsp.Parser;
import com.dolphinss.protocolo.rtsp.Pause;
import com.dolphinss.protocolo.rtsp.Play;
import com.dolphinss.protocolo.rtsp.RespuestaRTSP;
import com.dolphinss.protocolo.rtsp.Setup;
import com.dolphinss.protocolo.rtsp.Teardown;
import com.dolphinss.protocolo.sdp.InfoArchivo;

public class HiloCliente extends Thread implements Runnable {
	private Socket RTSPsocket;

	// RTSP variables
	// ----------------
	// rtsp states
	public final static int INICIAL = 0;
	public final static int LISTO = 1;
	public final static int EMITIENDO = 2;
	// rtsp message types
	public final static int SETUP = 3;
	public final static int PLAY = 4;
	public final static int PAUSE = 5;
	public final static int TEARDOWN = 6;
	public final static int OPTIONS = 7;
	public final static int DESCRIBE = 8;

	// para leer y escribir por el socket
	private DataInputStream RTSPBufferedReader;
	private PrintStream RTSPBufferedWriter;

	/**
	 * Vble para albergar cada ua de las respuestas que se enviarán al cliente.
	 */
	private RespuestaRTSP respRTP;
	
	/**
	 * Se guardará la URL solicitada por el cliente para pdoer usarla en la cabecera
	 * de respuesta a la petición DESCRIBE.
	 */
	private String contentBase="";

	// parseador de informacion sobre una linea de petición
	private Parser parser;

	public final static String CRLF = "\r\n";

	private int cseq = 1;
	private int estado;

	// para sacar los mensajes de debug del sistema
	public final static boolean DEBUG = true;

	private int[] puertosCliente =new int[2];
	private int contPuertosCliente=0;
	private int puertoLocalEnviar=Servidor.PUERTO_RTSP_RTP[0];
	
	private String archivoSolicitado;
	private boolean esArchivo=false;
	private InfoArchivo infoArchivo;


	/**
	 * Constructor
	 * 
	 * @param Socket s
	 */
	public HiloCliente(Socket s) {
		RTSPsocket = s;
		try {
			RTSPBufferedReader = new DataInputStream(s.getInputStream());
			RTSPBufferedWriter = new PrintStream(s.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser = new Parser();
	}

	@Override
	public void run() {
		String respuesta = "";

		try {
			int tipoPeticion;
			boolean estadoListo = false;

			// Mientras no lleguemos al estado LISTO...
			while (!estadoListo) {
				tipoPeticion = parseaPeticionRTSP();// parse_RTSP_request();

				// obtenemos la respuesta para la peticion realizada por el
				// cliente
				respuesta = respRTP.toString();

				switch (tipoPeticion) {
				case OPTIONS:
					estado = INICIAL;
					break;

				case DESCRIBE:
					estado = INICIAL;
					break;

				case SETUP:
					estadoListo = true;
					estado = LISTO;
					break;

				default:
					break;
				}

				// Envio la respuesta al cliente
				RTSPBufferedWriter.print(respuesta);

				if (DEBUG) {
					System.out.print("RESPUESTA:\r\n" + respuesta);
				}
			}

			if (DEBUG) {
				System.out
					.println("[Servidor]->Empieza la transferencia de datos...");
			}

			do {
				tipoPeticion = parseaPeticionRTSP();

				// obtenemos la respuesta para la peticion realizada por el
				// cliente
				respuesta = respRTP.toString();

				// Envio la respuesta al cliente
				RTSPBufferedWriter.print(respuesta);

				if (DEBUG) {
					System.out.print("RESPUESTA:\r\n" + respuesta);
				}

				if ((tipoPeticion == PLAY) && (estado == LISTO)) {
					estado = EMITIENDO;
					
					if(DEBUG){
						System.out.println("Emitiendo!: "+archivoSolicitado);
					}

					enviarArchivoUnicast();
					
				} else if ((tipoPeticion == PAUSE) && (estado == EMITIENDO)) {
					estado = LISTO;
					if(DEBUG){
						System.out.println("New RTSP state: READY");
					}
				} else if (tipoPeticion == TEARDOWN) {
					// stop timer
					estado=TEARDOWN;
				}
			} while (tipoPeticion != TEARDOWN);

			RTSPsocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[Servidor]->termino run() de HiloCliente "
				+ RTSPsocket);
	}

	/**
	 * Funcion que queda a la espera de leer peticiones que realice el cliente
	 * Cuando lee una, crea una respuesta y devuelve un entero que indica el
	 * tipo de respuesta que se ha creado.
	 * @return Entero que indica el tipo de respuesta creada en función de la petición.
	 */
	private int parseaPeticionRTSP() {
		String lineasPeticion = "";
		try {
			lineasPeticion = Parser.leerPeticion(RTSPBufferedReader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (DEBUG) {
			System.out.print("CLIENTE:\r\n"+lineasPeticion);
		}

		int tipoPeticion = Parser.getTipoOrden(lineasPeticion);
		
		//Cojo la URL solicitada para el parámetro "Content-base" que irá en la
		//... respuesta del DESCRIBE.
		if(contentBase.isEmpty()){
			contentBase=Parser.getContentBase(lineasPeticion);
		}

		try {
			if (!lineasPeticion.isEmpty()) {
				cseq = parser.getCseq(lineasPeticion);
			}
			switch (tipoPeticion) {
			case OPTIONS:
				respRTP = new Options(cseq);
				break;

			case DESCRIBE:
				respDescribe(lineasPeticion);
				break;

			case SETUP:
				respSetup(lineasPeticion);
				break;
				
			case PAUSE:
				respRTP=new Pause(cseq);
				break;

			case TEARDOWN:
				respRTP = new Teardown(cseq);
				break;

			case PLAY:
				respRTP = new Play(cseq);
				((Play) respRTP).setRango(parser.getRangoPlay(lineasPeticion));
				break;

			default:
				if(lineasPeticion.isEmpty()){
					System.out.println("Nos envia una orden vacia. Enviamos OK");
					respRTP=new ErrorRTSP(cseq);
				}else{
					System.out.println("Atencion, no se entiende la orden: ->"
							+ lineasPeticion + "<-. Se manda un OK");
					respRTP=new ErrorRTSP(cseq);
					System.exit(0);
				}
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tipoPeticion;
	}

	/**
	 * Método que compone una respuesta a un DESCRIBE
	 * @param lineasPeticion Las líneas que nos envió el cliente.
	 * @throws Exception
	 */
	private void respDescribe(String lineasPeticion) throws Exception{
		respRTP = new Describe(cseq);
		archivoSolicitado = parser.getArchivoSolicitado(lineasPeticion);
		esArchivo=parser.esFile(archivoSolicitado);
		if(esArchivo){
			infoArchivo=new InfoArchivo(archivoSolicitado);
			archivoSolicitado=Servidor.DIR_MULTIMEDIOS
			+ archivoSolicitado;
			((Describe) respRTP).setArchivoSolicitado(archivoSolicitado);
		}else{
			((Describe) respRTP)
			.setArchivoSolicitado(archivoSolicitado);
			if(archivoSolicitado.compareTo("screen")==0){
				archivoSolicitado="screen://0,0,1280,800/25";
			}
			if(archivoSolicitado.compareTo("webcam")==0){
				archivoSolicitado="vfw://0";
			}
		}
		
		((Describe)respRTP).setContentBase(contentBase);
	}
	
	/**
	 * Método que compone una respuesta a un SETUP
	 * @param lineasPeticion Las líneas que nos envió el cliente.
	 * @throws Exception
	 */
	private void respSetup(String lineasPeticion) throws Exception{
		respRTP = new Setup(cseq);
		if(contPuertosCliente==1){
			((Setup) respRTP).crearSessionId(false);
		}
		puertosCliente[contPuertosCliente] = parser.getPuertoCliente(lineasPeticion);
		((Setup) respRTP).setPuertoCliente(puertosCliente[contPuertosCliente]);
		((Setup) respRTP).setProtocoloTransporte(parser
				.getProcoloTransporte(lineasPeticion));
		((Setup) respRTP).setTipoSession(parser
				.getTipoSession(lineasPeticion));
		((Setup) respRTP).setCliente_ip(RTSPsocket.getInetAddress().getHostAddress());
		int[] interleaved=parser.getInterleavedSetup(lineasPeticion);
		if(interleaved!=null){
			((Setup) respRTP).setIntervaled(interleaved);
		}
		contPuertosCliente++;
	}
	
	
	/**
	 * Método que se encarga de enviar el archivo por RTP. Se manda cada una de las pistas que componen
	 * el archivo, con su puerto asignado (en el SDP), por RTP.
	 */
	private void enviarArchivoUnicast(){
		String archivoEnvio = archivoSolicitado;
		if(esArchivo){
			archivoEnvio="file:"+archivoSolicitado;
		}
		InetAddress ip_destino=RTSPsocket.getInetAddress();

		/**
		 * Los números de puerto se guardan de forma inversa a la cual se envían, es decir, la pista 0
		 * es para el vídeo y el puertosCliente[0] no es el correspondiente para el vídeo. De la misma
		 * forma pasa para el audio.
		 * Por esto, se usa un indice inverso para seleccionar el puerto a la hora de enviar las pistas
		 * del archivo.
		 * Esto sólo se debe hacer si el archivo a enviar tiene más de 1 pista, es decir, cuando sea
		 * un archivo de audio/vídeo.
		 */
		int indicePuertos=1;
		if(contPuertosCliente==1){
			indicePuertos=0;
		}
		for(int i=0;i<contPuertosCliente;i++){
			if(DEBUG){
				System.out.println("\r\n"+archivoEnvio+", "+ip_destino+", "+puertoLocalEnviar+", "+puertosCliente[indicePuertos]+", "+i);
			}
			
			UnicastRtp u=null;
			if(esArchivo){
				u=new UnicastRtp(archivoEnvio, ip_destino, puertoLocalEnviar, puertosCliente[indicePuertos], i);
			}else{
				if(archivoSolicitado.compareToIgnoreCase("vfw://0")==0){
					u=new UnicastRtpWebcam(archivoEnvio, ip_destino, puertoLocalEnviar, puertosCliente[indicePuertos], i);
				}else{
					u=new UnicastRtpPantalla(archivoEnvio, ip_destino, puertoLocalEnviar, puertosCliente[indicePuertos], i);
				}
			}
			u.run();
			puertoLocalEnviar+=Servidor.unidadAumentarPuertos;
			indicePuertos--;
		}
	}
}
