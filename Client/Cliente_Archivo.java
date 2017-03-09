package Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Cliente_Archivo implements Runnable {
	private ServerSocket ss;

	public Cliente_Archivo() {
		try {
			ss = new ServerSocket(8899);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		@SuppressWarnings({ "unused", "resource" })
		Socket s = new Socket();
		while (true) {
			if (!ss.isClosed()) {
				try {
					Socket sc2 = ss.accept();
					String path = "./AudioCliente/Prueba.raw";
					InputStream llegada;
					llegada = sc2.getInputStream();

					File f = new File(path);
					FileOutputStream destino = new FileOutputStream(f);

					byte buff[] = new byte[5120];
					int leidos = llegada.read(buff); //Tema 2 Diapositiva 26 - 27
					while (leidos != -1 ){
						destino.write(buff, 0, leidos); //lee, empieze,nºlectura
						leidos = llegada.read(buff);
					}
					destino.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
