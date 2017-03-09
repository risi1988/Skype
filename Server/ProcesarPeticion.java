package Server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;



public class ProcesarPeticion implements Runnable {

	private Socket socketCliente = null;
	private static Map<String, Boolean> client_connected;
	private static Map<String, InetAddress> client_IP;
	private static Map<String, Vector<String>> destination_message;
	private static Map<String, Vector<File>> call_message;
	private static Boolean stopLisening = false;

	public ProcesarPeticion(Socket socketCliente, Map<String, Boolean> client_connected, Map<String, InetAddress> client_IP, Map<String, Vector<String>> destination_message, Map<String, Vector<File>> call_message){
		this.socketCliente = socketCliente;
		ProcesarPeticion.client_connected = client_connected;
		ProcesarPeticion.client_IP = client_IP;
		ProcesarPeticion.destination_message = destination_message;
		ProcesarPeticion.call_message =  call_message;

	}
	
	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(socketCliente.getInputStream());
			@SuppressWarnings("deprecation")
			String comando = dis.readLine();
			String respuesta = procesarPeticion(comando,socketCliente);
			PrintStream ps = new PrintStream(socketCliente.getOutputStream());
			ps.println(respuesta + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socketCliente != null)
				try {
					socketCliente.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	@SuppressWarnings({ "deprecation", "resource" })
	private static String procesarPeticion(String comando, Socket socketCliente) {
		StringTokenizer st = new StringTokenizer(comando);
		String respuesta = "OK. ";
		String nick = null;
		
		if (st.hasMoreTokens()) {
			String primerToken = st.nextToken();
			if (primerToken.equals("CONNECT") && st.hasMoreTokens()) {
				nick = st.nextToken();
				if (nick != null) {
					conectarCliente(nick, socketCliente);
				}
				respuesta += "CLIENT CONNECTED.";
				
				for (Iterator it = client_connected.keySet().iterator() ; it.hasNext();){
					String nom = (String)it.next();
					Boolean con = (Boolean)client_connected.get(nom);
					//A los demas solo el nombre + conectado
					if (con){
						Socket s;
						try {
							s = new Socket(client_IP.get(nom).getHostName(),4342);
							PrintStream ps = new PrintStream(s.getOutputStream());
							ps.println(nick+" ONLINE"); 
							s.close();		
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			} else if (primerToken.equals("DISCONNECT") && st.hasMoreTokens()) {
				nick = st.nextToken();
				if (nick != null)
					desconectarCliente(nick);
				respuesta += "CLIENT DISCONNECTED.";
				
				for (Iterator it = client_connected.keySet().iterator() ; it.hasNext();){
					String nom = (String)it.next();
					Boolean con = (Boolean)client_connected.get(nom);
					//Le indico a los demas que me he desconectado
					if (con){
						Socket s;
						try {
							s = new Socket(client_IP.get(nom).getHostName(),4342);
							PrintStream ps = new PrintStream(s.getOutputStream());
							ps.println(nick+" OFFLINE"); 
							s.close();		
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				
			} else if (primerToken.equals("REQUEST_IP") && st.hasMoreTokens()) {
				nick = st.nextToken();
				if (nick != null) {
					if (client_connected.containsKey(nick)) {
						respuesta += client_IP.get(nick).getHostAddress();
					} else {
						respuesta = "NOT_FOUND";
					}
				} else {
					respuesta = "NOT_FOUND";
				}
			} else if (primerToken.equals("LIST")) {
				respuesta = "";
				for (String s : client_connected.keySet()) {
					if (client_connected.get(s))
						respuesta += s + " ONLINE;";
					else
						respuesta += s + " OFFLINE;";
				}
			} else if (primerToken.equals("CONNECTED")) {
				nick = st.nextToken();
				if (nick != null) {
					if (!client_connected.containsKey(nick)) {
						respuesta = "UNKNOWN USER";
					} else {
						if (client_connected.get(nick)) {
							respuesta = "CONNECTED";
						} else {
							respuesta = "DISCONNECTED";
						}
					}
				}
			} else if (primerToken.equals("HASNEWMESSAGES")){
				nick = st.nextToken();
				if(nick!=null){
					if(hasNewMessages(nick)) respuesta = "TRUE";
					else respuesta = "FALSE";
				}
			} else if (primerToken.equals("GETNEWMESSAGES")) {
				nick = st.nextToken();
				if(nick!=null && hasNewMessages(nick)){
					respuesta = sendNewMessages(nick, socketCliente);
				} else {
					respuesta = "ERROR. UNKNOWN USER OR NO NEW MESSAGES FOUND;";
				}
				
			} else if (primerToken.equals("SEND")) {
				nick = st.nextToken();
				String nick_destino = st.nextToken();
				if (nick != null && nick_destino != null) {
					String message = "";
					while (st.hasMoreTokens()) message += st.nextToken()+" ";
					if (message != null) {
						Vector<String> vs = destination_message.get(nick_destino);
						if (vs == null) destination_message.put(nick_destino,new Vector<String>());
						vs = destination_message.get(nick_destino);
						vs.add(nick + ": " + message);
						respuesta += "SAVED.";
					}
				} else {
					respuesta = "ERROR. NOT SUPPORTED.";
				}
			}else if (primerToken.equals("CALLMESSAGES")){
				String amigo = st.nextToken();
				nick=st.nextToken();				
				File f = new File("./AudioServer/"+amigo+"_"+nick+".raw");
				
				
				
				if (call_message.containsKey(amigo)){
					if (call_message.get(amigo).contains(f)){
						call_message.get(amigo).remove(f);
					}
					call_message.get(amigo).add(f);
				}else{
					Vector<File> vf = new Vector<File>();
					vf.add(f);
					call_message.put(amigo, vf);

				}
				
				listen(f);
				try {
					DataInputStream dis = new DataInputStream(socketCliente.getInputStream());
					comando = dis.readLine();
					if (comando.equals("CALLMESSAGESSTOP")){
						stopListening();
						try {
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						stopLisening = false;
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			}else if (primerToken.equals("HASNEWMESSAGESAUDIO")){
				nick = st.nextToken();
				respuesta = "FALSE";
				if (call_message.containsKey(nick)){
					respuesta="TRUE";
				}
				
			}else if(primerToken.equals("LISTMESSAGESVOZ")){
				nick = st.nextToken();
				respuesta = "";
				for (int i=0; i<call_message.get(nick).size();i++){
					respuesta = call_message.get(nick).elementAt(i).getName()+";"+respuesta;
				}
				
			}
			else if (primerToken.equals("GETNEWMESSAGESVOZ")){
				nick=st.nextToken();
				String archivo = st.nextToken();
				try {		
					
					Socket s = new Socket(client_IP.get(nick).getHostAddress(),8899); 
					PrintStream envio = new PrintStream(s.getOutputStream());
					String path = "./AudioServer/"+archivo;
					FileInputStream origen = new FileInputStream(path);
					byte[] buffer = new byte[5120];
					int len;
					while ((len = origen.read(buffer)) > 0) {
						envio.write(buffer, 0, len); //Envio el archivo 
					}
					origen.close();
					s.close();
					//Buscar el archivo.
					@SuppressWarnings("unused")
					File faux = null;
					for (int i =0;i<call_message.get(nick).size();i++){
						if (call_message.get(nick).elementAt(i).getName().equals(archivo)){
							faux = call_message.get(nick).elementAt(i);
							call_message.get(nick).remove(i);
						}
					}
					
					if (call_message.get(nick).size()==0){
						call_message.remove(nick);
					}
					faux.delete();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else {
				respuesta = "ERROR. NOT SUPPORTED.";
				// ERROR, comando no admitido.
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss - dd/MM/yyyy");
		return respuesta;
	}

	private static String sendNewMessages(String nick, Socket socketCliente) { //Envio mensajes nuevos a nick cuando se conecta.
		Vector<String> vs = destination_message.get(nick);
		String respuesta = "";
		if (vs != null) {
			for (int i = 0; i < vs.size(); i++) {
				respuesta += vs.elementAt(i) + "; ";
			}
		}
		destination_message.remove(nick);
		return respuesta;
	}

	private static boolean hasNewMessages(String nick) {
		return destination_message.containsKey(nick);
	}

	private static void desconectarCliente(String nick) {
		if (client_connected.containsKey(nick)) {
			client_connected.remove(nick);
		}
		client_connected.put(nick, false);
	}

	private static void conectarCliente(String nick, Socket socketCliente) {
		if (client_connected.containsKey(nick)) {
			client_connected.remove(nick);
		}
		client_connected.put(nick, true);
		if (client_IP.containsKey(nick)) {
			client_IP.remove(nick);
		}
		client_IP.put(nick, socketCliente.getInetAddress());
	}
	private static void listen(final File f){
		new Thread(new Runnable() {
			public void run() {
				stopLisening = false;
		TargetDataLine line = null;
 		OutputStream target = null;
		try {
			target = new FileOutputStream(f);
			AudioFormat audioFormat = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class,
					audioFormat);
			line = (TargetDataLine) AudioSystem.getLine(info);
			if (line != null) {
				line.open(audioFormat);
				line.start();

				System.out.println("Escuchando micrófono...");

				// El tamaño del buffer suele calcularse así
				byte tempBuffer[] = new byte[line.getBufferSize() / 5];
				int nBytesRead;
				while ((nBytesRead = line.read(tempBuffer, 0, tempBuffer.length)) != -1 && !stopLisening )//Me lo he cargado no me parece normal tener esto aqui " && !stopLisening"
					{
					if (nBytesRead > 0) {
						target.write(tempBuffer, 0, nBytesRead);
						target.flush();
					}
				}
				target.close();
				line.stop();
				line.drain();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (line != null)
					line.close();
				if (target != null)
					target.close();
				System.out.println("Termina micrófono...");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		}	
			
		}).start();

	}
	
	private static AudioFormat getAudioFormat() {
		int sampleSizeInBits = 16; // Valores posibles: 8,16
		boolean bigEndian = false; // Valores: true,false
		boolean signed = true; // true,false
		float sampleRate = 8000.0F; // Valores: 8000,11025,16000,22050,44100
		int channels = 1; // Valores: 1,2
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}
	
	public static void stopListening() {
		stopLisening = true;
	}

}
