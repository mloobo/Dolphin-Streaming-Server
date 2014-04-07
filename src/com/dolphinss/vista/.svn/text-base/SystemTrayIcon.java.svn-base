package com.dolphinss.vista;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SystemTrayIcon {
	public SystemTrayIcon(){
		// se declara el objeto tipo icono
		final TrayIcon iconoSystemTray;
		// se verifica que el SystemTray sea soportado
		if (SystemTray.isSupported()) {
			// se obtiene una instancia estática de la clase SystemTray
			SystemTray tray = SystemTray.getSystemTray();
			// esta es la imagen de icono
			Image imagenIcono = Toolkit.getDefaultToolkit().getImage(
					"icono.png");
			// este listener nos permite capturar cualquier tipo de evento
			// que se haga con el mouse sobre el icono
			MouseListener mouseListener = new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					System.out
							.println("Icono del System Tray - Mouse clicked!");
				}

				public void mouseEntered(MouseEvent e) {
					System.out
							.println("Icono del System Tray - Mouse entered!");
				}

				public void mouseExited(MouseEvent e) {
					System.out.println("Icono del System Tray - Mouse exited!");
				}

				public void mousePressed(MouseEvent e) {
					System.out
							.println("Icono del System Tray - Mouse pressed!");
				}

				public void mouseReleased(MouseEvent e) {
					System.out
							.println("Icono del System Tray - Mouse released!");
				}
			};
			// este listener se asociara con un item del menu contextual
			// que aparece al hacer click derecho sobre el icono
			ActionListener escuchadorSalir = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Saliendo...");
					System.exit(0);
				}
			};
			// menu que aparece al hacer click derecho
			PopupMenu popup = new PopupMenu();
			MenuItem item = new MenuItem("Exit");
			item.addActionListener(escuchadorSalir);
			popup.add(item);
			// iniciamos el objeto TrayIcon
			iconoSystemTray = new TrayIcon(imagenIcono, "Prueba System Tray",
					popup);
			// este tipo de listener captura el doble click sobre el icono
			ActionListener accionMostrarMensaje = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					iconoSystemTray.displayMessage("Titulo mensaje",
							"Este es el contenido del mensaje!",
							TrayIcon.MessageType.INFO);
				}
			};

			iconoSystemTray.setImageAutoSize(true);
			iconoSystemTray.addActionListener(accionMostrarMensaje);
			iconoSystemTray.addMouseListener(mouseListener);

			// se debe capturar una excepción en caso que falle la adicion de un
			// icono
			try {
				tray.add(iconoSystemTray);
			} catch (AWTException e) {
				System.err
						.println("No es posible agregar el icono al System Tray");
			}
		} else
			System.err.println("Tu sistema no soporta el System Tray");
	}
}
