package com.dolphinss.protocolo.rtsp;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.dolphinss.HiloCliente;
import com.dolphinss.Servidor;

/**
 * Clase para el procesamiento de las peticiones realizadas por los clientes al servidor.
 * 
 * @version 0.5
 * @author mloobo@gmail.com
 *
 */
public class Parser {
	/**
	 * El nombre o recurso solicitado por el cliente.
	 */
	private String archivo = "";

	public static boolean esFile(String medio){
		if(medio.compareToIgnoreCase("sonido")==0){
			return false;
		}else{
			if(medio.compareToIgnoreCase("webcam")==0){
				return false;
			}else{
				if(medio.compareToIgnoreCase("screen")==0){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Método que lee, del bufer de lectura, la peticion (que puede estar
	 * compuesta por varias líneas) que realiza el cliente al servidor.
	 * 
	 * @return La cadena de petición del cliente.
	 * @throws IOException
	 */
	public static String leerPeticion(DataInputStream RTSPBufferedReader)
			throws IOException {
		String Request = "";
		boolean foundEnd = false;
		int c;
		Request = new String();
		while ((c = RTSPBufferedReader.read()) != -1) {
			Request += (char) c;
			if (c == '\n') {
				if (foundEnd) {
					break;
				} else {
					foundEnd = true;
				}
			} else {
				if (c != '\r') {
					foundEnd = false;
				}
			}
		}
		return Request;
	}

	/**
	 * Método que obtiene el comando RTSP está en la cadena de texto que se
	 * recibe por parámetro.
	 * 
	 * @param input
	 *            La cadena recibida, que fue leída desde el socket y enviada
	 *            por el cliente.
	 * @return Devuelve un entero que indica el comando que es.
	 */
	public static int getTipoOrden(String input) {
		StringTokenizer tokens = new StringTokenizer(input);
		String request_type_string = "";
		if (tokens.hasMoreTokens()) {
			request_type_string = tokens.nextToken();
		}

		if ((new String(request_type_string)).compareTo("OPTIONS") == 0)
			return HiloCliente.OPTIONS;
		if ((new String(request_type_string)).compareTo("DESCRIBE") == 0)
			return HiloCliente.DESCRIBE;
		if ((new String(request_type_string)).compareTo("SETUP") == 0)
			return HiloCliente.SETUP;
		else if ((new String(request_type_string)).compareTo("PLAY") == 0)
			return HiloCliente.PLAY;
		else if ((new String(request_type_string)).compareTo("PAUSE") == 0)
			return HiloCliente.PAUSE;
		else if ((new String(request_type_string)).compareTo("TEARDOWN") == 0)
			return HiloCliente.TEARDOWN;

		return 0;
	}

	/**
	 * Método que obtiene, de la línea que se recibe por parámetro, el valor
	 * para la futura cabecera "Content-base" que se envía en la respuesta a un
	 * DESCRIBE.
	 * 
	 * @param input
	 *            La línea enviada por el cliente.
	 * @return El valor para la cabecera "Content-base"
	 */
	public static String getContentBase(String input) {
		StringTokenizer tokens = new StringTokenizer(input);
		String contentBase = "";
		if (tokens.hasMoreTokens()) {
			contentBase = tokens.nextToken();
			contentBase = tokens.nextToken();
		}
		return contentBase;
	}

	/**
	 * Método que obtiene el número de secuencia de la línea recibida por
	 * parámetro.
	 * 
	 * @param input
	 *            La línea enviada por el cliente.
	 * @return El número (int) de secuencia.
	 * @throws Exception
	 */
	public int getCseq(String input) throws Exception {
		String lineaArchivo = getLineaInput(input, "\r\n", "CSeq");
		String cseq = lineaArchivo.substring(6);
		return Integer.parseInt(cseq);
	}

	/**
	 * Método que obtiene lo valores de la "propiedad" intervaled que me puede
	 * enviar el cliente en una petición SETUP (concrétamente, parámetro
	 * Transport:)
	 * 
	 * @return Array de enteros con los valores del "intreleaved"
	 * @throws Exception
	 */
	public int[] getInterleavedSetup(String input) throws Exception {
		int[] interleaved = null;
		String linea = getLineaInput(input, "\r\n", "Transport:");
		String[] trozos = linea.split("interleaved=");
		int t = trozos.length;
		if (t > 1) {
			trozos = trozos[1].split("-");
			interleaved = new int[2];
			interleaved[0] = Integer.parseInt(trozos[0]);
			interleaved[1] = Integer.parseInt(trozos[1]);
		}

		return interleaved;
	}

	/**
	 * Método que obtiene el nombre del archivo o recurso solicitado por el
	 * cliente en la línea de petición que se recibe por parámetro. Este nombre,
	 * además, se guarda en la variable de clase "archivo".
	 * 
	 * @param input
	 *            La línea enviada por el cliente.
	 * @return El nombre del archivo.
	 * @throws Exception
	 */
	public String getArchivoSolicitado(String input) throws Exception {
		String lineaArchivo = getLineaInput(input, " ", "rtsp");
		String[] trozos = lineaArchivo.split("rtsp://" + Servidor.IP_SERVIDOR
				+ "/");
		archivo = trozos[1];
		return archivo;
	}

	/**
	 * Método que se encarga de obtener la línea que empiece por el valor que se
	 * reciba en la variable "cadenaInicio"
	 * 
	 * @param input
	 *            La línea enviada por el cliente.
	 * @param separador
	 * @param cadenaInicio
	 * @return
	 * @throws Exception
	 */
	private String getLineaInput(String input, String separador,
			String cadenaInicio) throws Exception {
		StringTokenizer str = new StringTokenizer(input, separador);
		String token = null;
		boolean encontrada = false;

		while (str.hasMoreTokens()) {
			token = str.nextToken();
			if (token.startsWith(cadenaInicio)) {
				encontrada = true;
				break;
			}
		}

		if (!encontrada) {
			throw new Exception();
		}

		return token;
	}

	/**
	 * Método que obtiene el puerto que usará el cliente para leer la pista del
	 * archivo. En la línea se recibe (entre otras cosas) algo como:
	 * client_port=4000-4001 Y nos quedamos con el primer número de puerto, ya
	 * que el 2º lo podríamos calcular (sumando 1).
	 * 
	 * @param input
	 *            La línea enviada por el cliente.
	 * @return
	 * @throws Exception
	 */
	public int getPuertoCliente(String input) throws Exception {
		String lineaClient_port = getLineaInput(input, "\r\n", "Transport:");
		String[] trozos = lineaClient_port.split(";");
		trozos[2] = trozos[2].substring(12);
		String[] puertos = trozos[2].split("-");
		return Integer.parseInt(puertos[0]);
	}

	/**
	 * Se obtiene el nombre del protocolo que quiere usar el cliente en la
	 * transmisión.
	 * 
	 * @param input
	 *            La línea enviada por el cliente.
	 * @return El nombre del protocolo, por ejemplo, RTP/AVP
	 * @throws Exception
	 */
	public String getProcoloTransporte(String input) throws Exception {
		String lineaClient_port = getLineaInput(input, "\r\n", "Transport:");
		String[] trozos = lineaClient_port.split(";");
		trozos[0] = trozos[0].substring(11);
		return trozos[0];
	}

	/**
	 * Método que obtiene el valor del rango de una petición de PLAY.
	 * 
	 * @param input
	 *            La línea enviada por el cliente.
	 * @return Ej. 0.000-
	 * @throws Exception
	 */
	public String getRangoPlay(String input) throws Exception {
		String lineaRange = getLineaInput(input, "\r\n", "Range:");
		String[] trozos = lineaRange.split("=");
		return trozos[1];
	}

	/**
	 * Se obtiene el User-Agent, es decir, el nombre de la aplicación
	 * cliente que nos realiza las peticiones.
	 * @param input
	 * @return El nombre de la aplicación cliente.
	 * @throws Exception
	 */
	public String getTipoSession(String input) throws Exception {
		String lineaClient_port = getLineaInput(input, "\r\n", "Transport:");
		String[] trozos = lineaClient_port.split(";");
		return trozos[1].trim();
	}
	
	public String getUserAgent(String input) throws Exception{
		String linea = getLineaInput(input, "\r\n", "User-Agent:");
		String[] trozos = linea.split(":");
		return trozos[1];
	}

	/**
	 * Devuelve el nombre del archivo que solicitó el cliente.
	 * 
	 * @return
	 */
	protected String getArchivo() {
		return archivo;
	}

	/**
	 * Establece el nombre del archivo que solicitó el cliente.
	 * 
	 * @param archivo
	 */
	protected void setArchivo(String archivo) {
		this.archivo = archivo;
	}
}
