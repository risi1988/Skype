package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Lista_Usuario implements Runnable {
	private ServerSocket ss; 

	public Lista_Usuario(){
		try {
			ss = new ServerSocket(4342);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run() {
		// TODO Auto-generated method stub
		DataInputStream dis2 = null;
		try {	
			while (true){
				Socket sc2 = ss.accept();
				if (!ss.isClosed()){
					dis2 = new DataInputStream(sc2.getInputStream());
					String mensaje = dis2.readLine();
					WindowTAC.anadir_Lista_SS(mensaje);
					sc2.close();
				}
				
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				dis2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
