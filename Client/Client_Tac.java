package Client;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;


public class Client_Tac {
	static String Ip_server;
	static int puerto;
	static int puerto_cliente =9996;
	static int puerto_cliente_audio =9997; //apa--------------------------------------------------------------------------------------------------- 
	static String nick;  
	public static ExecutorService pool;
	public static boolean estado = true;
	//--------------------------------------------------------------Cosas del audio 
	private static boolean stopLisening = false;
	static Socket Saudio; //Cambiar
	
	//-------------------------------------------------------------------------Se cambiaran.
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		File f = new File("./AudioCliente");
		if(!f.isDirectory()){
			f.mkdir();
		}
		HashMap<String,WindowCHAT> window = new HashMap<String, WindowCHAT>();
		pool = Executors.newCachedThreadPool();
		
		//Se han creado tantos hilos porque nos es mas sencillo a la hora de manejar el programa. Ya que asi distingimos las funciones
		// de mensajes aundio archivo y reproduccion
		Cliente_Server cs = new Cliente_Server(window);
		Cliente_Server_Audio csa = new Cliente_Server_Audio();
		Reproducir_cc rc = new Reproducir_cc();
		Cliente_Archivo ca = new Cliente_Archivo();
		Lista_Usuario lu = new Lista_Usuario();
			
			pool.submit(lu);
			pool.submit(cs);
			pool.submit(csa);
			pool.submit(rc);
			pool.submit(ca);
			
			WindowBienvenida wb = new WindowBienvenida(window);
			wb.show();
			
			//------------------------------------------------------------------------------------------------------------
	}
	
	public static void salir(Socket socket_servidor){
		disconnect_servidor(socket_servidor);
		File f = new File("./AudioCliente");
		if(f.isDirectory()){
			f.deleteOnExit();
		}
		pool.shutdownNow();
	}
	
	@SuppressWarnings("deprecation")
	public static void envio_SEND(String comando2, Socket socket_servidor) throws IOException{
		String resp = null;
		PrintStream ps = null;
		DataInputStream dis = null;
		if (comando2.startsWith("SEND")){
			StringTokenizer st = new StringTokenizer(partir_cadena_send(comando2));
			String amigo = st.nextToken();
			String mensaje = "";
			while(st.hasMoreTokens()){
				mensaje = mensaje + " " + st.nextToken();
			}

			socket_servidor = new Socket(Ip_server, puerto);
			ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("CONNECTED "+amigo);
			dis = new DataInputStream(socket_servidor.getInputStream()); 
			resp = dis.readLine();
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);
			
			
			if (resp.equals("CONNECTED")){
				
				socket_servidor = new Socket(Ip_server, puerto); 
				ps = new PrintStream(socket_servidor.getOutputStream());
				ps.println("REQUEST_IP "+amigo);
				dis = new DataInputStream(socket_servidor.getInputStream()); 
				resp=dis.readLine();
				ps.close();
				dis.close();
				cierre_socket_servidor(socket_servidor);
				
				
				Socket socket_cliente = creacion_socket_cliente(tratamiento_resp_ip(resp));
				PrintStream psc = new PrintStream(socket_cliente.getOutputStream());
				psc.println(amigo+" "+"Nick: "+ nick +" Mensaje: "+mensaje);
				psc.close();
				cierre_socket_cliente(socket_cliente);
				
			}else if (resp.equals("DISCONNECTED")){
				
				socket_servidor = new Socket(Ip_server, puerto); 
				ps = new PrintStream(socket_servidor.getOutputStream()); 
				ps.println("SEND "+nick+ " "+amigo+" "+mensaje);
				ps.close();
				cierre_socket_servidor(socket_servidor);
			
			}
			
		}
	}
	
	public static void cierre_socket_cliente(Socket socket_cliente) {
		if (socket_cliente!=null){
			try {
				socket_cliente.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static Socket creacion_socket_cliente(String ip_cliente) {
		// TODO Auto-generated method stub
		Socket socket_cliente = null;
		try {
			socket_cliente = new Socket(ip_cliente, puerto_cliente); 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket_cliente;
	}


	private static String partir_cadena_send(String resp) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = null;
		tokens = new StringTokenizer(resp);
		String amigo = null, mensaje = null;
		if(tokens.hasMoreElements()){
			String palabra = tokens.nextToken();
			palabra = tokens.nextToken();
			amigo = palabra;
			mensaje = "";
			while (tokens.hasMoreElements()){
				String aux = tokens.nextToken();
				if (!aux.equals("null")) mensaje+=aux + " "; 				
			}
		}
		return amigo+" "+mensaje;
	}

	@SuppressWarnings("deprecation")
	private static void disconnect_servidor(Socket socket_servidor) {
		// TODO Auto-generated method stub
		String comando = "DISCONNECT";
		comando=comando+ " "+nick;
		@SuppressWarnings("unused")
		String resp;
		PrintStream ps = null;
		try {
			socket_servidor = new Socket(Ip_server, puerto);
			ps = new PrintStream(socket_servidor.getOutputStream()); 
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
			ps.println(comando);
			resp=dis.readLine();
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("deprecation")
	static void connect_Cliente(Socket socket_servidor) {
		// TODO Auto-generated method stub
		//Creacion del socket
		String comando = "CONNECT";
		comando=comando+ " "+nick;
		@SuppressWarnings("unused")
		String resp;
		try {
			socket_servidor = new Socket(Ip_server,puerto);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream());
			PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println(comando);
			resp=dis.readLine();
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);
			
			if (mensajes_Pendientes(socket_servidor)){
				//Primero mostrar una ventana de hay mensajes nuevos
				WindowPendiente wp = new WindowPendiente(socket_servidor);
				wp.show();
			}
			//------------------------------------------audio
			if(mensajes_Pendientes_Voz(socket_servidor)){
				WidowPendienteA wpa = new WidowPendienteA(socket_servidor);
				wpa.show();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void mensajes_Recibir(Socket socket_servidor) throws IOException {
		// TODO Auto-generated method stub
		String resp;
		socket_servidor = new Socket(Ip_server,puerto);
		DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
		PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
		
		ps.println("GETNEWMESSAGES "+nick);
		resp=dis.readLine();
		dis.close();
		ps.close();
		cierre_socket_servidor(socket_servidor);
		
		String[] lista =resp.split("; ");	
		Vector<String> vsms = new Vector<String>();
		for (int i =0; i<lista.length;i++){
			vsms.add(lista[i]);
		}
		
		WindowListSms wls = new WindowListSms(vsms);
		wls.anadirText();
		wls.show();
		
	}
	
	
	@SuppressWarnings("deprecation")
	public static void mensajes_Recibir_Voz(Socket socket_servidor, String audio) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		String resp;
		try {
			socket_servidor = new Socket(Ip_server,puerto);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
			PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("GETNEWMESSAGESVOZ "+nick+" "+audio);
			resp=dis.readLine();
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);		
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void mensajes_Talk(FileInputStream fi){
				
		SourceDataLine line = null;
		InputStream source = null;
		try {
			source = fi;
			AudioFormat format = getAudioFormat();
			AudioInputStream ais = new AudioInputStream(source, format,
					Integer.MAX_VALUE);

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			if (line != null) {
				line.open(format);
				line.start();

				System.out.println("Reproduciendo...");

				byte[] data = new byte[1024];
				int nBytesRead;
				while ((nBytesRead = ais.read(data, 0, data.length)) != -1
						&& !stopLisening) {
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
		}finally{
			stopLisening = false;
		}
		
	}

	@SuppressWarnings("deprecation")
	private static boolean mensajes_Pendientes(Socket socket_servidor) throws IOException {
		// TODO Auto-generated method stub
		String resp;
		boolean hay = false;
		socket_servidor = new Socket(Ip_server,puerto); 
		DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
		PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
		ps.println("HASNEWMESSAGES "+nick);
		resp=dis.readLine();
		ps.close();
		dis.close();
		cierre_socket_servidor(socket_servidor);
		
		if (resp.equals("TRUE")){
			hay=true;
		}
		return hay;
	}
	
	
	@SuppressWarnings({ "deprecation" })
	private static boolean mensajes_Pendientes_Voz(Socket socket_servidor) throws IOException {
		// TODO Auto-generated method stub
		String resp;
		boolean hay = false;
		
		socket_servidor = new Socket(Ip_server,puerto); 
		DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
		PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
		ps.println("HASNEWMESSAGESAUDIO "+nick);
		resp=dis.readLine();
		ps.close();
		dis.close();
		cierre_socket_servidor(socket_servidor);
		if (resp.equals("TRUE")){
			hay=true;
		}
		return hay;
	}

	public static String tratamiento_resp_ip(String resp) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = null;
		String ip_cliente = null;
		tokens = new StringTokenizer(resp);
		if(tokens.hasMoreElements()){
			String palabra = tokens.nextToken();
			if (palabra.equals("OK.")){
				ip_cliente = tokens.nextToken();	
			}else{
			}
			
		}	
		return ip_cliente;
	}

	@SuppressWarnings("unused")
	private static void tratamiento_resp_list(String resp) {
		// TODO Auto-generated method stub
		String[] lista =resp.split(";");	
		for (int i =0; i<lista.length;i++){
		}
		
	}
	
	private static void cierre_socket_servidor(Socket socket_servidor) {
		// TODO Auto-generated method stub
		if (socket_servidor!=null){
			try {
				socket_servidor.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void peticion_Nick(){
		DataInputStream ruta= null;
		ruta = new DataInputStream(System.in);
		try {
			nick= ruta.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				ruta.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
		
		public static void stop_Audio(Socket socket_servidor){
			try {
				PrintStream ps = new PrintStream(Saudio.getOutputStream());
				ps.println("CALLMESSAGESSTOP"); 
				ps.close();
				cierre_socket_servidor(Saudio);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

		
	public static void envio_Mensaje_Audio(String comando2, Socket socket_servidor) throws IOException {
		String amigo = partir_cadena_call_audio(comando2);
		Saudio = new Socket(Ip_server, puerto); 
		PrintStream ps = new PrintStream(Saudio.getOutputStream());
		ps = new PrintStream(Saudio.getOutputStream());
		ps.println("CALLMESSAGES "+amigo+" "+nick);
	
	
	}		
	@SuppressWarnings("deprecation")
	public static void envio_Audio(String comando2, Socket socket_servidor) throws IOException {
		// TODO Auto-generated method stub
		String resp = null;
		if (comando2.startsWith("CALL")){
			String amigo = partir_cadena_call_audio(comando2);
			socket_servidor = new Socket(Ip_server, puerto); 
			PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("CONNECTED "+amigo);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
			resp = dis.readLine();
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);
			
			if (resp.equals("CONNECTED")){
				socket_servidor = new Socket(Ip_server, puerto); 
				ps = new PrintStream(socket_servidor.getOutputStream());
				ps.println("REQUEST_IP "+amigo);
				dis = new DataInputStream(socket_servidor.getInputStream()); 
				resp=dis.readLine();
				ps.close();
				dis.close();
				cierre_socket_servidor(socket_servidor);
				
				Socket socket_cliente_audio = creacion_socket_cliente_audio(tratamiento_resp_ip(resp));
				PrintStream psca = new PrintStream(socket_cliente_audio.getOutputStream());
				dis = new DataInputStream(socket_cliente_audio.getInputStream());
				psca.println("CALL "+ amigo+" "+"Nick: "+ nick);
				resp=dis.readLine();
				psca.close();
				dis.close();
				cierre_socket_cliente_audio(socket_cliente_audio);
				
				if (resp.equals("NO.")){
					WindowNoCall wnc = new WindowNoCall(amigo, socket_servidor);
					wnc.show();
				}
			}
			
		}
	}

	private static void cierre_socket_cliente_audio(Socket socket_cliente_audio) {
		// TODO Auto-generated method stub
		if (socket_cliente_audio!=null){
			try {
				socket_cliente_audio.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

	public static Socket creacion_socket_cliente_audio(String ip_cliente) {
		// TODO Auto-generated method stub
		Socket socket_cliente_audio = null;
		try {
			socket_cliente_audio = new Socket(ip_cliente, puerto_cliente_audio);
//			psca = new PrintStream(socket_cliente_audio.getOutputStream());//Mandamos la informacion al servidor Socket 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket_cliente_audio;
	}

	private static String partir_cadena_call_audio(String comando2) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = null;
		tokens = new StringTokenizer(comando2);
		String amigo = null;
		if(tokens.hasMoreElements()){
			tokens.nextToken();
			amigo = tokens.nextToken();
		}
		return amigo;
	}
	
	public static void colgar(String amigo) {
		PrintStream ps = null;
		Socket socket_servidor = null;
		
		try{
			socket_servidor = new Socket(Ip_server, puerto);
			ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("REQUEST_IP "+amigo);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
			@SuppressWarnings("deprecation")
			String resp=dis.readLine();
			dis.close();
			ps.close();
			cierre_socket_servidor(socket_servidor);
			
			Socket socket_cliente_audio = creacion_socket_cliente_audio(tratamiento_resp_ip(resp));
			PrintStream psca = new PrintStream(socket_cliente_audio.getOutputStream());
			psca.println("DES. ");
			psca.close();
			cierre_socket_cliente_audio(socket_cliente_audio);	
			
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	public static void llamda(String mensaje, Socket socket_servidor) {
		// TODO Auto-generated method stub
		//Crear una conexion con el ss_ de audio y abrir el windowConv Mensaje: OK. Nick
		StringTokenizer tokens = null;
		String amigo = null;
		PrintStream ps = null;
		try {
			tokens = new StringTokenizer(mensaje);
			tokens.nextToken();
			if(tokens.hasMoreElements()){
				amigo = tokens.nextToken();
			}
			socket_servidor = new Socket(Ip_server, puerto);
			ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("REQUEST_IP "+amigo);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
			@SuppressWarnings("deprecation")
			String resp=dis.readLine();
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);
			
			Socket socket_cliente_audio = creacion_socket_cliente_audio(tratamiento_resp_ip(resp));
			PrintStream psca = new PrintStream(socket_cliente_audio.getOutputStream());
			psca.println("OK. "+nick);
			psca.close();
			cierre_socket_cliente_audio(socket_cliente_audio);	
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void rechazar(String mensaje, Socket socket_servidor) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = null;
		String amigo = null;
		PrintStream ps = null;
		try {
			tokens = new StringTokenizer(mensaje);
			tokens.nextToken();
			if(tokens.hasMoreElements()){
				amigo = tokens.nextToken();
			}
			socket_servidor = new Socket(Ip_server, puerto);
			ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("REQUEST_IP "+amigo);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
			@SuppressWarnings("deprecation")
			String resp=dis.readLine();
			ps.close();
			dis.close();
			
			cierre_socket_servidor(socket_servidor);
			Socket socket_cliente_audio = creacion_socket_cliente_audio(tratamiento_resp_ip(resp));
			PrintStream psca = new PrintStream(socket_cliente_audio.getOutputStream());
			psca.println("NO. "+nick);
			psca.close();
			cierre_socket_cliente_audio(socket_cliente_audio);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean usuario_Connected(String usu, Socket socket_servidor){
		boolean esta = false;
		try {
			socket_servidor = new Socket(Ip_server, puerto); 
			PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("CONNECTED "+usu);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream());
			@SuppressWarnings("deprecation")
			String resp=dis.readLine();
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);
			
			if (resp.equals("CONNECTED")){
				esta =true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return esta;
	}
	
	public static File crear_Fichero(){
		File f = new File("D:/temp.raw");
		f.deleteOnExit();
		return f;
	}
	
	
	//--------------------------------------------------------------------audio
	
	public static void lista_Mensajes_Voz(Socket socket_servidor) {
		// TODO Auto-generated method stub
		
		try {
			socket_servidor = new Socket(Ip_server, puerto);
			PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
			ps.println("LISTMESSAGESVOZ "+nick);
			DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
			@SuppressWarnings("deprecation")
			String resp = dis.readLine(); // no me devulve bien la ip
			ps.close();
			dis.close();
			cierre_socket_servidor(socket_servidor);
			
			//Cortar la lista que me manda el servidor
			if (resp!=null){
				String[] lista =resp.split(";");
				for (int i =0; i<lista.length;i++){
					StringTokenizer nombre = new StringTokenizer(lista[i]);
					WindowListAudio.anadirLista((String) nombre.nextElement());
				}
			}
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	public static void listen(final String amigo) throws UnknownHostException, IOException {
		
		
		new Thread(new Runnable() {
				public void run() {
			TargetDataLine line = null;
			OutputStream target = null;
			try {
				
				Socket socket_servidor = new Socket(Ip_server, puerto); 
				PrintStream ps = new PrintStream(socket_servidor.getOutputStream());
				ps.println("REQUEST_IP "+amigo);
				DataInputStream dis = new DataInputStream(socket_servidor.getInputStream()); 
				@SuppressWarnings("deprecation")
				String resp = dis.readLine(); // no me devulve bien la ip
				cierre_socket_servidor(socket_servidor);
				
				StringTokenizer tokens = new StringTokenizer(resp);
				tokens.nextToken();
				@SuppressWarnings("resource")
				Socket s = new Socket(tokens.nextToken(),9988);
				
				target = s.getOutputStream();
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
					while ((nBytesRead = line.read(tempBuffer, 0, tempBuffer.length)) != -1 && !stopLisening) {
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
	public static void startListening() {
		stopLisening = false;
	}

	public static void estado_True(){
		estado=true;
	}
	
	public static boolean estado() {
		// TODO Auto-generated method stub
		return estado;
	}

	
}
