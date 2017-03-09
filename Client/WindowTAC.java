package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;


public class WindowTAC extends JFrame {
	private static final long serialVersionUID = 1L;
	public JList<String> list;
	public String lista;
	public static DefaultListModel<String> dlm;
	private static String nick;
	
	public WindowTAC(final String nick, final Socket socket_servidor, final HashMap<String,WindowCHAT> window) {
		setDefaultCloseOperation(0); 
		this.nick=nick;
		
		dlm = new DefaultListModel<String>();
		list = new JList<String>(dlm);
		
		
		try {
			Socket socket_servidor_2 = new Socket(Client_Tac.Ip_server, Client_Tac.puerto); 
			PrintStream ps = new PrintStream(socket_servidor_2.getOutputStream());
			ps.println("LIST ");
			DataInputStream dis = new DataInputStream(socket_servidor_2.getInputStream()); 
			String resp = dis.readLine();
			dis.close();
			ps.close();
			socket_servidor_2.close();
			
			if (resp!=""){
				String[] lista =resp.split(";");
				for (int i =0; i<lista.length;i++){
					//StringTokenizer nombre = new StringTokenizer(lista[i]);
					if (!lista[i].equals(nick + " ONLINE"))anadir_Listab((String) lista[i]);
				}
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		setResizable(false);
		setTitle("TAC");
		setBounds(100, 100, 292, 220);
		
		JPanel panel = new JPanel();
		
		getContentPane().add(panel, BorderLayout.CENTER);
		
		list.setBounds(10, 10, 80, 20);
		
		JButton btEnviarAudio = new JButton("Enviar audio");
		btEnviarAudio.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				//Comprovamos que el usuario este conectado con el Connected
				StringTokenizer st = new StringTokenizer(list.getSelectedValue());
				String chatWith = st.nextToken();
				if (Client_Tac.usuario_Connected(chatWith,socket_servidor)){
				
					if(chatWith!=null){
						
						try {
							Client_Tac.envio_Audio("CALL "+ chatWith +" Nick: "+nick, socket_servidor);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//Tiene que esperar a que el otro usuario de la orden de abrir la ventana que tiene lo de colgar.
					}
				}else{
					WindowNoCall wnc = new WindowNoCall(chatWith,socket_servidor);
					wnc.show();
				}
			}
		});
		btEnviarAudio.setName("btEnviarAudio");
		
		JButton btEnviarTexto = new JButton("Enviar texto");
		btEnviarTexto.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				//Comprovamos que el usuario este conectado con el Connected
				StringTokenizer st = new StringTokenizer(list.getSelectedValue());
				String chatWith = st.nextToken();
				if(chatWith!=null){
					if (Client_Tac.usuario_Connected(chatWith,socket_servidor)){
						WindowCHAT wc = new WindowCHAT(chatWith, socket_servidor);
						window.put(chatWith,wc);
						System.out.println(chatWith);
						wc.show();
					}else{
						//Nueva ventana
						WindowNoCHAT wnc = new WindowNoCHAT(chatWith, socket_servidor);
						wnc.show();
					}
				}
			}
		});
		btEnviarTexto.setName("btEnviarTexto");
		
		JLabel lbBienvenida = new JLabel("Bienvenid@");
		lbBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lbNick = new JLabel(nick);
		lbNick.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client_Tac.salir(socket_servidor);
				dispose();
				System.exit(0);
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(list, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
							.addComponent(lbNick, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lbBienvenida, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btEnviarTexto, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btEnviarAudio, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(btnSalir))
					.addContainerGap(27, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(list, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(27)
							.addComponent(lbBienvenida)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbNick)
							.addPreferredGap(ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
							.addComponent(btnSalir)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btEnviarAudio)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btEnviarTexto)))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
	}
	
	private void anadir_Listab(String usu) {
		// TODO Auto-generated method stub
		dlm.addElement(usu);
	}
	
	private static void anadir_Lista(String usu, String estado) {
		// TODO Auto-generated method stub
		String aux="OFFLINE";
		if (estado.equals("OFFLINE")){
			aux="ONLINE";
		}
		if (!dlm.contains(usu+" "+aux)){
			dlm.addElement(usu+" "+estado);
		}else{
			int n = dlm.indexOf(usu+" "+aux);
			dlm.remove(n);
			dlm.addElement(usu+" "+estado);
		}
	}
	public static void anadir_Lista_SS(String mensaje) {
		// TODO Auto-generated method stub
		StringTokenizer st = new StringTokenizer(mensaje);
		String nombre = st.nextToken();
		String estado = st.nextToken();
		if (!nombre.equals(nick)){
			anadir_Lista(nombre,estado); 
		}
		
		
	}
	
}
