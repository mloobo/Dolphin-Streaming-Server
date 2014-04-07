package com.dolphinss.cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class HiloRTP implements Runnable {
	public static String ADIOS = "EXIT";

	private BufferedReader teclado;
	/** Para lectura de datos del socket */
    private DataInputStream RTSPBufferedReader;

    /** Para escritura de datos en el socket */
    private PrintStream RTSPBufferedWriter;

	private Socket RTSPsocket;
	
	final static String CRLF = "\r\n";

	public HiloRTP(Socket s) {
		this.RTSPsocket = s;
		try {
			teclado = new BufferedReader(new InputStreamReader(System.in));
			RTSPBufferedReader = new DataInputStream(s.getInputStream());
			RTSPBufferedWriter = new PrintStream(s.getOutputStream(), true);
			Thread hilo = new Thread(this);
            hilo.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void describe(){
		String r="DESCRIBE rtsp://localhost:6969/Big_Buck_Bunny_small.ogv RTSP/1.0"+CRLF +
				"User-Agent: WMPlayer/11.0.6002.18005 guid/3300AD50-2C39-46C0-AE0A-E9E58AC4541C"+CRLF +
				"Accept: application/sdp"+CRLF +
				"Accept-Charset: UTF-8, *;q=0.1"+CRLF +
				"X-Accept-Authentication: Negotiate, NTLM, Digest, Basic"+CRLF +
				"Accept-Language: es-ES, *;q=0.1"+CRLF +
				"CSeq: 1"+CRLF +
				"Supported: com.microsoft.wm.srvppair, com.microsoft.wm.sswitch, com.microsoft.wm.eosmsg, com.microsoft.wm.predstrm, com.microsoft.wm.startupprofile"+CRLF;
		RTSPBufferedWriter.println(r);
	}

	public void run() {
		String orden = "";
		try {
			do {
				//cojo la orden del teclado
//				orden = teclado.readLine();
//				//orden = RTSPBufferedReader.readUTF();
//				
//
//				RTSPBufferedWriter.println(orden);
//				
//				//ejecuto la orden y obtengo la respuesta
//				String respuesta=ejecutaOrden(orden);
//				
//				//envio la respuesta al servidor
//				RTSPBufferedWriter.println(respuesta);
				
				describe();
				
				//leo la respuesta del servidor a mi respuesta (u orden, segun como se mire)
				System.out.println("[Cliente]->respuesta del servidor: "+RTSPBufferedReader.readLine());

			} while (orden.compareToIgnoreCase(ADIOS) != 0);
			RTSPsocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String ejecutaOrden(String orden){
		String respuesta="";
		
		System.out.println("[Cliente]->mando a servidor ejecutar orden de teclado: "+orden);
		
		return respuesta;
	}
}
