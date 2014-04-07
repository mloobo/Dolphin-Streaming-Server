package com.dolphinss.protocolo.screen;



import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

public class StreamPantalla implements PushBufferStream, Runnable {

	protected ContentDescriptor cd = new ContentDescriptor(
			ContentDescriptor.RAW);
	protected int maxDataLength;
	protected int[] data;
	protected Dimension size;
	protected RGBFormat rgbFormat;
	protected boolean started;
	protected Thread thread;
	protected float frameRate = 1f;
	protected BufferTransferHandler transferHandler;
	protected Control[] controls = new Control[0];
	protected int x, y, width, height;

	protected Robot robot = null;

	int seqNo = 0;
	boolean rateChanged = false, rateChanged2 = false, everChanged = false;
	long timeStamp = 0;
	long OldStampCons = 0;
	public boolean pauseScreen = false;
	int pauseCounter = 0;

	public void setDimension(int w, int h) {
		width = w;
		height = h;
		size = new Dimension(width, height);
		maxDataLength = size.width * size.height * 3;
		rgbFormat = new RGBFormat(size, maxDataLength, Format.intArray,
				frameRate, 32, 0xFF0000, 0xFF00, 0xFF, 1, size.width,
				VideoFormat.FALSE, Format.NOT_SPECIFIED);
	}

	public void setPoint(int xx, int yy) {
		x = xx;
		y = yy;
	}

	public void setFrameRate(float ffps) {
		frameRate = ffps;
		seqNo = 0;
		rateChanged = true;
		rateChanged2 = true;
		everChanged = true;
		maxDataLength = size.width * size.height * 3;
		rgbFormat = new RGBFormat(size, maxDataLength, Format.intArray,
				frameRate, 32, 0xFF0000, 0xFF00, 0xFF, 1, size.width,
				VideoFormat.FALSE, Format.NOT_SPECIFIED);
	}

	public StreamPantalla(MediaLocator locator) {
		try {
			parseLocator(locator);
		} catch (Exception e) {
			System.err.println(e);
		}
		size = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			robot = new Robot();
		} catch (AWTException awe) {
			throw new RuntimeException("");
		}
		maxDataLength = size.width * size.height * 3;
		rgbFormat = new RGBFormat(size, maxDataLength, Format.intArray,
				frameRate, 32, 0xFF0000, 0xFF00, 0xFF, 1, size.width,
				VideoFormat.FALSE, Format.NOT_SPECIFIED);

//		VistaFPS frame1 = new VistaFPS("Capture Frame");
//		frame1.pack();
//		frame1.setSizeMarcoCaptura(new Dimension(width, height));
//		frame1.setVisible(true);
//		frame1.FrameTransmit(StreamPantalla.this);

		// generate the data
		data = new int[maxDataLength];
		thread = new Thread(this, "Hilo Screen Grabber RTP");
	}

	protected void parseLocator(MediaLocator locator) {
		String rem = locator.getRemainder();
		// Strip off starting slashes
		while (rem.startsWith("/") && rem.length() > 1)
			rem = rem.substring(1);
		StringTokenizer st = new StringTokenizer(rem, "/");
		if (st.hasMoreTokens()) {
			// Parse the position
			String position = st.nextToken();
			StringTokenizer nums = new StringTokenizer(position, ",");
			String stX = nums.nextToken();
			String stY = nums.nextToken();
			String stW = nums.nextToken();
			String stH = nums.nextToken();
			x = Integer.parseInt(stX);
			y = Integer.parseInt(stY);
			width = Integer.parseInt(stW);
			height = Integer.parseInt(stH);
		}
		if (st.hasMoreTokens()) {
			// Parse the frame rate
			String stFPS = st.nextToken();
			frameRate = (Double.valueOf(stFPS)).floatValue();
		}
	}

	/***************************************************************************
	 * SourceStream
	 ***************************************************************************/

	public ContentDescriptor getContentDescriptor() {
		return cd;
	}

	public long getContentLength() {
		return LENGTH_UNKNOWN;
	}

	public boolean endOfStream() {
		return false;
	}

	/***************************************************************************
	 * PushBufferStream
	 ***************************************************************************/

	// seqNo = 0;

	public Format getFormat() {
		return rgbFormat;
	}

	public void read(Buffer buffer) throws IOException {
		synchronized (this) {
			// {
			Object outdata = buffer.getData();
			if (rateChanged || outdata == null
					|| !(outdata.getClass() == Format.intArray)
					|| ((int[]) outdata).length < maxDataLength) {
				outdata = new int[maxDataLength];
				buffer.setData(outdata);
				// System.out.println("rateChanged is: "+rateChanged+" and framerate is: "
				// + frameRate);
				rateChanged = false;
			}
			buffer.setFormat(rgbFormat);
			if (!everChanged) {
				timeStamp = (long) (seqNo * (1000 / frameRate) * 1000000);
			} else {
				if (rateChanged2) {
					OldStampCons = timeStamp;
					rateChanged2 = false;
				}
				timeStamp = OldStampCons
						+ (long) (seqNo * (1000 / frameRate) * 1000000);
				// System.out.println("timeStamp is: " + timeStamp);
			}

			/*
			 * if (rateChanged2 && everChanged) { timeStamp = timeStamp + (long)
			 * (seqNo * (1000 / frameRate) * 1000000);
			 * System.out.println("timeStamp is: " + timeStamp); rateChanged2
			 * ++; } else { timeStamp = (long) (seqNo * (1000 / frameRate) *
			 * 1000000); System.out.println("timeStamp is: " + timeStamp); }
			 */
			// buffer.setTimeStamp( (long) (seqNo * (1000 / frameRate) *
			// 1000000) );
			buffer.setTimeStamp(timeStamp);
			// System.out.println("timeStamp is: " + timeStamp);
			// System.out.println("framerate is: " + frameRate);
			int ww = width;
			// ww = ww+1;
			// x = x+1;
			// y = y+1;
			// height = height+1;
			// System.out.println("width is: "+width);
			BufferedImage bi = robot.createScreenCapture(new Rectangle(x, y,
					width, height));
			// bi.getRGB(0, 0, width, height,
			// (int[])outdata, 0, width);
			bi.getRGB(0, 0, width, height, (int[]) outdata, 0, width);
			buffer.setSequenceNumber(seqNo);
			buffer.setLength(maxDataLength);
			buffer.setFlags(Buffer.FLAG_KEY_FRAME);
			buffer.setHeader(null);
			seqNo++;
		}
	}

	public void setTransferHandler(BufferTransferHandler transferHandler) {
		synchronized (this) {
			this.transferHandler = transferHandler;
			notifyAll();
		}
	}

	void start(boolean started) {
		synchronized (this) {
			this.started = started;
			if (started && !thread.isAlive()) {
				thread = new Thread(this);
				thread.start();
			}
			notifyAll();
		}
	}

	/***************************************************************************
	 * Runnable
	 ***************************************************************************/

	public void run() {
		while (started) {
			synchronized (this) {
				while (transferHandler == null && started) {
					try {
						wait(1000);
					} catch (InterruptedException ie) {
					}
				} // while
			}

			synchronized (this) {
				if (pauseScreen && pauseCounter == 0) {
					// pauseCounter = 1;
					pauseCounter = seqNo;
					/*
					 * try { wait(3000); } catch (InterruptedException iee2) {}
					 */
				} else if (pauseScreen && pauseCounter >= seqNo - 20) {

				} else {
					pauseCounter = 0;
					while (pauseScreen) {
						try {
							wait(1000);
						} catch (InterruptedException iee) {
						}
					} // endof while
				}
			}

			if (started && transferHandler != null) {
				transferHandler.transferData(this);
				try {
					Thread.currentThread().sleep(10);
				} catch (InterruptedException ise) {
				}
			}
		} // while (started)
	} // run

	// Controls

	public Object[] getControls() {
		return controls;
	}

	public Object getControl(String controlType) {
		try {
			Class cls = Class.forName(controlType);
			Object cs[] = getControls();
			for (int i = 0; i < cs.length; i++) {
				if (cls.isInstance(cs[i]))
					return cs[i];
			}
			return null;

		} catch (Exception e) { // no such controlType or such control
			return null;
		}
	}
}