package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Cliente_Server implements Runnable {

	String mensaje = null;
	public static ServerSocket ss = null;
	private String amigo; 
	private String sms;
	Client_Tac ct= new Client_Tac();
	private HashMap<String, WindowCHAT> window;
	
	public Cliente_Server (HashMap<String,WindowCHAT> window){
		this.window = window;
			try {
				ss = new ServerSocket(9996);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		DataInputStream dis2 = null;
		try {	
			while (true){
				if (!ss.isClosed()){
					Socket sc2 = ss.accept();
					dis2 = new DataInputStream(sc2.getInputStream());
					String mensaje = dis2.readLine();
					tratar_mensaje(mensaje);
					WindowCHAT wc = new WindowCHAT(amigo,sc2);
					if (window.containsKey(amigo)){
						//Si ya hay un windowschat
						System.out.println(amigo);
						window.get(amigo).textArea.append(amigo+":"+sms+"\r\n");
						wc.textArea.append(sms);
					}else{
						//Si no hay un windowschat
						wc.show();
						window.put(amigo,wc);
						window.get(amigo).textArea.append(amigo+":"+sms+"\r\n");
					}
					
					 
					sc2.close();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace(); //Para que cuando cierre el ss no me muestre el mensaje de error
		}finally{
			try {
				dis2.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (ss!=null){
				try {
					ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void tratar_mensaje(String resp) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = null;
		tokens = new StringTokenizer(resp);
		if(tokens.hasMoreElements()){
			tokens.nextToken();
			tokens.nextToken();
			amigo = tokens.nextToken();
			tokens.nextToken();
			sms ="";
			while (tokens.hasMoreElements()){
				String aux = tokens.nextToken();
				if (!aux.equals("null")) sms+=aux + " "; 				
			}
		}
		
	}
	public void cerrarSS(){
		if (ss != null){
			try {
				ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
}