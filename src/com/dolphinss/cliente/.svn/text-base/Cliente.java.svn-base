package com.dolphinss.cliente;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.dolphinss.Servidor;

public class Cliente {
	private Socket socket;
	
	public Cliente(){
		try {
			socket = new Socket("localhost", Servidor.PUERTO);
			
			new HiloRTP(socket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Cliente();
	}
}
