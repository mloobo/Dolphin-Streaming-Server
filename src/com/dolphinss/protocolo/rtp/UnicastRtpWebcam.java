package com.dolphinss.protocolo.rtp;

import java.net.InetAddress;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.NotRealizedError;
import javax.media.RealizeCompleteEvent;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;

public class UnicastRtpWebcam extends UnicastRtp {

	public UnicastRtpWebcam(String file, InetAddress d_IP, int l_rtp,
			int d_rtp, int track) {
		super(file, d_IP, l_rtp, d_rtp, track);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void controllerUpdate(ControllerEvent p0) {
		if (p0 instanceof ConfigureCompleteEvent) {
			ContentDescriptor cd = new ContentDescriptor(
					ContentDescriptor.RAW_RTP);
			processor.setContentDescriptor(cd);
			Format format;
			TrackControl track[] = processor.getTrackControls();
			int numPistas=track.length;
			for(int i=0;i<numPistas;i++){
				format = track[i].getFormat();

				if (format instanceof VideoFormat) {
					VideoFormat v = (VideoFormat) track[i].getFormat();
					setMyVideoFormat(v, track[i]);
				}
				if (format instanceof AudioFormat) {
					AudioFormat a = (AudioFormat) track[i].getFormat();
					setMyAudioFormat(a, track[i]);
				}
			}
			processor.realize();
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
}
