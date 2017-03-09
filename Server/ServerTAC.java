package Server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTAC {
	static ServerSocket ss = null;
	final static int POOL_SIZE = 20;
	static Map<String, Boolean> client_connected = null;
	static Map<String, InetAddress> client_IP = null;
	static Map<String, Vector<String>> destination_message = null;
	static Map<String, Vector<File>> call_message = null;
	static int puerto;
	private static ExecutorService poolDeConexiones = Executors
			.newFixedThreadPool(POOL_SIZE);

	public static void main(String[] args) {
		try {
			File f = new File("./AudioServer");
			if(!f.exists()){
				f.mkdir();
			}
			client_connected = new HashMap<String, Boolean>();
			client_IP = new HashMap<String, InetAddress>();
			destination_message = new HashMap<String, Vector<String>>();
			call_message = new HashMap<String, Vector<File>>();
			if(args.length==1) puerto = Integer.parseInt(args[0]);
			else puerto = 6669;
			ss = new ServerSocket(puerto);

			System.out.println("LISTENING FROM PORT "+puerto+"...");
			while (true) {
			if (ss != null) {
				final Socket socketCliente = ss.accept();
				poolDeConexiones.submit(new ProcesarPeticion(socketCliente, client_connected, client_IP, destination_message,call_message));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}