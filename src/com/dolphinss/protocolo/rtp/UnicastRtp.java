package com.dolphinss.protocolo.rtp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.NotRealizedError;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.UnsupportedFormatException;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SendStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.event.NewSendStreamEvent;
import javax.media.rtp.event.SendStreamEvent;
import javax.media.rtp.event.StreamClosedEvent;

import com.dolphinss.HiloCliente;

public class UnicastRtp implements ControllerListener,
		SendStreamListener {
	protected Processor processor;
	protected String url;
	protected DataSource ds = null;
	protected SendStream mySendStream = null;
	protected int prepare_track;

	protected int local_rtp;
	protected InetAddress destIP;
	protected int dest_rtp;

	protected RTPManager[] mgr;

	protected boolean endofMedia = false;
	

	public UnicastRtp(String file, InetAddress d_IP, int l_rtp, int d_rtp,
			int track) {
		url = file;
		local_rtp = l_rtp;
		destIP = d_IP;
		dest_rtp = d_rtp;
		prepare_track = track;
	}

	protected void myEx(Exception ex, String f) {
		f += " :";
		f += ex.getMessage();
		System.out.println(f);
	}

	public boolean createMyProcessor() {
		final String e = "Clase Unicast_rtp::createMyProcessor()";

		try {
			processor = Manager.createProcessor(new MediaLocator(url));
			processor.addControllerListener(this);
			processor.configure();
		} catch (IOException ex) {
			myEx((Exception) ex, e);
			return false;
		} catch (NoProcessorException ex) {
			myEx((Exception) ex, e);
			return false;
		}
		return true;
	}

	protected synchronized boolean waitForState(Processor p, int state) {
		p.addControllerListener(new StateListener());
		failed = false;

		// Call the required method on the processor
		if (state == Processor.Configured) {
			p.configure();
		} else if (state == Processor.Realized) {
			p.realize();
		}

		// Wait until we get an event that confirms the
		// success of the method, or a failure event.
		// See StateListener inner class
		while (p.getState() < state && !failed) {
			synchronized (getStateLock()) {
				try {
					getStateLock().wait();
				} catch (InterruptedException ie) {
					return false;
				}
			}
		}

		if (failed)
			return false;
		else
			return true;
	}
	
	/****************************************************************
	 * Inner Classes
	 ****************************************************************/

	class StateListener implements ControllerListener {

		public void controllerUpdate(ControllerEvent ce) {

			// If there was an error during configure or
			// realize, the processor will be closed
			if (ce instanceof ControllerClosedEvent)
				setFailed();

			// All controller events, send a notification
			// to the waiting thread in waitForState method.
			if (ce instanceof ControllerEvent) {
				synchronized (getStateLock()) {
					getStateLock().notifyAll();
				}
			}
		}
	}
	
	public void controllerUpdate(ControllerEvent p0) {
		if (p0 instanceof ConfigureCompleteEvent) {
			Format format;
			boolean encodingOK = false;

			TrackControl track[] = processor.getTrackControls();
			ContentDescriptor cd = new ContentDescriptor(
					ContentDescriptor.RAW_RTP);
			processor.setContentDescriptor(cd);
			format = track[prepare_track].getFormat();

			if (format instanceof VideoFormat) {
				VideoFormat v = (VideoFormat) track[prepare_track].getFormat();
				encodingOK = setMyVideoFormat(v, track[prepare_track]);
				if(HiloCliente.DEBUG){
					System.out.println("Pista "+prepare_track+" es de Video");
				}
			}
			if (format instanceof AudioFormat) {
				AudioFormat a = (AudioFormat) track[prepare_track].getFormat();
				encodingOK = setMyAudioFormat(a, track[prepare_track]);
				if(HiloCliente.DEBUG){
					System.out.println("Pista "+prepare_track+" es de Audio");
				}
			}

			if (encodingOK) {
				for (int i = 0; i < track.length; i++) {
					if (i != prepare_track) {
						track[i].setEnabled(false);
					}
				}
				processor.realize();
			}
		}

		if (p0 instanceof RealizeCompleteEvent) {
			try {
				ds = processor.getDataOutput();
				createMyRTPManager();
			} catch (NotRealizedError ex) {
				myEx(null, ex.getMessage());
			}
		}

		if (p0 instanceof EndOfMediaEvent) {
			closeMyStream();
			endofMedia = true;
		}
	}

	public void update(SendStreamEvent p0) {
		if (p0 instanceof NewSendStreamEvent) {
			startMyStream();
		}
		if (p0 instanceof StreamClosedEvent) {
			closeMyStream();
		}
	}

	protected boolean setMyVideoFormat(VideoFormat v, TrackControl track) {
		boolean found = false;
		
		if(HiloCliente.DEBUG){
			Format[] supported = track.getSupportedFormats();
			for (int n = 0; n < supported.length; n++)
			    System.out.println("Formato de Video soportados: " + supported[n]);
		}
		
		if (v.isSameEncoding(VideoFormat.MPEG)) {
			((FormatControl) track).setFormat(new VideoFormat(
					VideoFormat.MPEG_RTP));
			found = true;
		}
		if (v.isSameEncoding(VideoFormat.JPEG_RTP)) {// para la webcam
			((FormatControl) track).setFormat(new VideoFormat(
					VideoFormat.JPEG_RTP));
			found = true;
		}
		if (v.isSameEncoding(VideoFormat.H263)) {
			((FormatControl) track).setFormat(new VideoFormat(
					VideoFormat.H263_RTP));
			found = true;
		}
		if (v.isSameEncoding(VideoFormat.JPEG)) {
			((FormatControl) track).setFormat(new VideoFormat(
					VideoFormat.JPEG_RTP));
			found = true;
		}
		if (v.isSameEncoding(VideoFormat.MJPG)) {
			((FormatControl) track).setFormat(new VideoFormat(
					VideoFormat.JPEG_RTP));
			found = true;
		}
		if (v.isSameEncoding(VideoFormat.YUV)) {// para la webcam
			((FormatControl) track).setFormat(new VideoFormat(
					VideoFormat.JPEG_RTP));
			found = true;
		}
		if (v.isSameEncoding(VideoFormat.RGB)) {// para el escritorio
			((FormatControl) track).setFormat(new VideoFormat(
					VideoFormat.JPEG_RTP));
			found = true;
		}	
		
		if(HiloCliente.DEBUG){
			String s="";
			if(found){
				s="Formato de video encontrado ("+v.toString()+"). " +
						"\nSe transforma a ("+((FormatControl) track).getFormat().toString()+").";
			}else{
				s="Formato de video ("+v.toString()+") NO encontrado.";
			}
			System.out.println(s);
		}
		
		track.setEnabled(found);
		return found;
	}

	protected boolean setMyAudioFormat(AudioFormat a, TrackControl track) {
		boolean found = false;

		if(HiloCliente.DEBUG){
			Format[] supported = track.getSupportedFormats();
			for (int n = 0; n < supported.length; n++)
			    System.out.println("Formato de Audio soportados: " + supported[n]);
		}
		
		if (a.isSameEncoding(AudioFormat.MPEG)) {
			((FormatControl) track).setFormat(new AudioFormat(
					AudioFormat.MPEG_RTP));
			found = true;
		}
		if (a.isSameEncoding(AudioFormat.MPEGLAYER3)) {
			((FormatControl) track).setFormat(new AudioFormat(
					AudioFormat.MPEG_RTP));
			found = true;
		}
		if (a.isSameEncoding(AudioFormat.LINEAR)) {
			((FormatControl) track).setFormat(new AudioFormat(
					AudioFormat.DVI_RTP));
			found = true;
		}
		if (a.isSameEncoding(AudioFormat.ULAW)) {
			((FormatControl) track).setFormat(new AudioFormat(
					AudioFormat.ULAW_RTP));
			found = true;
		}
		
		if(HiloCliente.DEBUG){
			String s="";
			if(found){
				s="Formato de audio encontrado.";
			}else{
				s="Formato de audio ("+a.toString()+") NO encontrado.";
			}
			System.out.println(s);
		}

		track.setEnabled(found);

		return found;
	}

	protected boolean createMyRTPManager() {
		if(HiloCliente.DEBUG){
			System.out.println("Inicio el createMyRTPManager...");
		}
		PushBufferDataSource pbds = (PushBufferDataSource) ds;
		PushBufferStream pbss[] = pbds.getStreams();

		mgr = new RTPManager[pbss.length];

		for (int i = 0; i < pbss.length; i++) {
			try {
				mgr[i] = RTPManager.newInstance();
				mgr[i].addSendStreamListener(this);
				SessionAddress localAddr = new SessionAddress(
						InetAddress.getLocalHost(), local_rtp);
				SessionAddress destAddr = new SessionAddress(destIP, dest_rtp);
				mgr[i].initialize(localAddr);
				mgr[i].addTarget(destAddr);
				mySendStream = mgr[i].createSendStream(ds, i);
			} catch (UnsupportedFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (InvalidSessionAddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(HiloCliente.DEBUG){
			System.out.println("Terminado el createMyRTPManager!.");
		}
		
		return true;
	}

	protected boolean startMyStream() {
		try {
			if(HiloCliente.DEBUG){
				System.out.println("\tInicio transmision del stream...");
			}
			mySendStream.start();
			processor.start();
		} catch (IOException ex) {
			myEx((Exception) ex, "RTP_Stream startMyStream");
			return false;
		} 
		return true;
	}

	protected void closeMyStream() {
		processor.close();
		processor.deallocate();
		mySendStream.close();
		for (int i = 0; i < mgr.length; i++) {
			mgr[i].dispose();
		}
	}

	public void startStreamAgain() {
		try {
			mySendStream.start();
		} catch (IOException ex) {
			myEx((Exception) ex, "RTP_Stream startStreamAgain");
		}
	}

	public void pauseStream() {
		try {
			mySendStream.stop();
		} catch (IOException ex) {
			myEx((Exception) ex, "RTP_Stream pauseStream");
		}
	}

	public void teardownStream() {
		if (!endofMedia) {
			pauseStream();
			closeMyStream();
		}
	}

	public boolean getMediaState() {
		return endofMedia;
	}

	public void run() {
		createMyProcessor();
	}
	
	
	
	protected Integer stateLock = new Integer(0);
	protected boolean failed = false;
	
	Integer getStateLock() {
		return stateLock;
	}
	void setFailed() {
		failed = true;
	}
}