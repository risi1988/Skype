package Client;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;


public class Cliente_Server_Audio implements Runnable{
	String mensaje = null;
	public ServerSocket ss = null;
	private String amigo;
	public Cliente_Server_Audio(){
			try {
				ss = new ServerSocket(9997);
				
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
		WindowAudio wa = null;
		try {	
			while (true){
				if (!ss.isClosed()){
					Socket sc2 = ss.accept();
					dis2 = new DataInputStream(sc2.getInputStream());
					String mensaje = dis2.readLine();
					if (mensaje.startsWith("CALL ")){
						tratar_mensaje(mensaje);
						@SuppressWarnings("unused")
						DataInputStream dis = new DataInputStream(sc2.getInputStream());
						PrintStream ps = new PrintStream(sc2.getOutputStream());						
						if (Client_Tac.estado()){
							wa = new WindowAudio(amigo,sc2);
							wa.show();	
							ps.println("OK.");			
						}else{
							ps.println("NO.");
						}
						ps.close();
					}else if(mensaje.startsWith("OK.")){
						//aqui hay que iniciar la conversacion.
						tratar_mensaje2(mensaje);
						
						WindowConv wac = new WindowConv(amigo);
						wac.show();
						
						Client_Tac.listen(amigo);
						
					}else if (mensaje.startsWith("DES.")){
						Client_Tac.stopListening();
						try {
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Client_Tac.startListening();
					}else if (mensaje.startsWith("NO")){
						tratar_mensaje2(mensaje);
						Socket socket_servidor = null;
						WindowNoCall wnc = new WindowNoCall(amigo, socket_servidor);
						wnc.show();
					}else{
						wa.dispose();
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
	
	private void tratar_mensaje2(String resp) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = null;
		tokens = new StringTokenizer(resp);
		if(tokens.hasMoreElements()){
			tokens.nextToken();
			amigo = tokens.nextToken();
			
		}
		
	}
	
	private void tratar_mensaje(String resp) {
		// TODO Auto-generated method stub
		StringTokenizer tokens = null;
		tokens = new StringTokenizer(resp);
		if(tokens.hasMoreElements()){
			tokens.nextToken();
			tokens.nextToken();
			tokens.nextToken();
			amigo = tokens.nextToken();			
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
