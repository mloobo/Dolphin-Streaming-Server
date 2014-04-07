package com.dolphinss.protocolo.sdp;

import javax.media.MediaLocator;
import javax.media.Format;
import javax.media.RealizeCompleteEvent;
import javax.media.Manager;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.format.AudioFormat;

import java.io.IOException;

public final class InfoArchivo implements ControllerListener {
	private MediaLocator ml = null;
	private Processor p = null;
	private int track_amount = 0;
	private String[] trackInfos;
	private boolean audioTrack = false;
	private boolean videoTrack = false;
	private double trackTime = 0.0;
	private boolean read = false;

	public InfoArchivo(String url) {
		ml = new MediaLocator(url);
	}

	public boolean makeInfos() {
		return createProcessor();
	}

	public boolean canRead() {
		return read;
	}

	public double getTrackTime() {
		return trackTime;
	}

	public boolean getAudio() {
		return audioTrack;
	}

	public boolean getVideo() {
		return videoTrack;
	}

	public int getTrackCount() {
		return track_amount;
	}

	public String[] getTrackInfo() {
		return trackInfos;
	}

	private boolean createProcessor() {
		final String m = "MediaInfo createProcessor";

		try {
			p = Manager.createProcessor(ml);
			p.addControllerListener(this);
			p.configure();
		} catch (NoProcessorException ex) {
			ex.printStackTrace();
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	private void getMyTracks() {
		Format format;
		TrackControl[] track = p.getTrackControls();
		track_amount = track.length;
		trackInfos = new String[track_amount];
		Time t = p.getDuration();
		trackTime = t.getSeconds();

		for (int i = 0; i < track_amount; i++) {
			format = track[i].getFormat();
			trackInfos[i] = format.toString();
			if (format instanceof VideoFormat) {
				videoTrack = true;
			}
			if (format instanceof AudioFormat) {
				audioTrack = true;
			}
		}
		read = true;
	}

	public void controllerUpdate(ControllerEvent p0) {
		if (p0 instanceof ConfigureCompleteEvent) {
			p.realize();
		}
		if (p0 instanceof RealizeCompleteEvent) {
			getMyTracks();
			p.close();
			p.deallocate();
		}
	}
	
	public Processor getProcessor(){
		return p;
	}
}