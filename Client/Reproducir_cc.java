package Client;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Reproducir_cc  implements Runnable{

	private static boolean stopLisening = false;
	@SuppressWarnings("unused")
	private boolean talking = false;
	private ServerSocket ss = null;
	public Reproducir_cc() {
		// TODO Auto-generated constructor stub
		try {
			ss = new ServerSocket(9988);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket sc= null;
		try {
			while (true){// a ver solo puede tener un audio a la vez hay que pensar como hacerlo de momento no me preocupo
				sc = ss.accept();
				InputStream in = sc.getInputStream();
				SourceDataLine line = null;
				InputStream source = in;
				try {
					AudioFormat format = getAudioFormat();
					AudioInputStream ais = new AudioInputStream(source, format,Integer.MAX_VALUE);
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
					line = (SourceDataLine) AudioSystem.getLine(info);
					if (line != null) {
						line.open(format);
						line.start();
	
						System.out.println("Reproduciendo...");
	
						byte[] data = new byte[1024];
						int nBytesRead;
						while ((nBytesRead = ais.read(data, 0, data.length)) != -1 && !stopLisening) {
							if (nBytesRead > 0) {
								line.write(data, 0, nBytesRead);
							}
						}
						line.drain();
						line.close();
					}
					System.out.println("Termina reproduccion...");
			
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (LineUnavailableException ex) {
				ex.printStackTrace();
			}		
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static AudioFormat getAudioFormat() {
		int sampleSizeInBits = 16; // Valores posibles: 8,16
		boolean bigEndian = false; // Valores: true,false
		boolean signed = true; // true,false
		float sampleRate = 8000.0F; // Valores: 8000,11025,16000,22050,44100
		int channels = 1; // Valores: 1,2
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
	}
}