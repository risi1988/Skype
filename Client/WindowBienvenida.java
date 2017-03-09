package Client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class WindowBienvenida extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textNick;
	private JTextField textIP;
	private JTextField textServer;

	public WindowBienvenida(final HashMap<String,WindowCHAT> window) {
		setTitle("TAC_jon_alb");
		setBounds(100, 100, 378, 273);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblBienvenidoALa = new JLabel("Bienvenido a la aplicaci\u00F3n ");
		lblBienvenidoALa.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JLabel lblNick = new JLabel("Nick:");
		lblNick.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		textNick = new JTextField();
		textNick.setColumns(10);
		
		JButton btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				Client_Tac.nick=textNick.getText();
				Client_Tac.Ip_server=textIP.getText();
				Client_Tac.puerto=Integer.parseInt(textServer.getText());
				new Thread(new Runnable() {
					public void run() {
				Socket socket_servidor = null;
				Client_Tac.connect_Cliente(socket_servidor);
			}	
			
		}).start();
		
				Socket socket_servidor = null;
				WindowTAC w = new WindowTAC(textNick.getText(), socket_servidor,window);
				w.show();
				dispose();
			}
		});
		
		JLabel lblIpserver = new JLabel("IP_SERVER:");
		lblIpserver.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		
		textIP = new JTextField();
		textIP.setColumns(10);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		
		textServer = new JTextField();
		textServer.setColumns(10);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(67)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblBienvenidoALa)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblNick)
									.addGap(31)
									.addComponent(textNick, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(142)
							.addComponent(btnEnviar))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(41)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(13)
									.addComponent(lblPuerto, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(textServer, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblIpserver, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(textIP, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)))))
					.addGap(77))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(30)
					.addComponent(lblBienvenidoALa)
					.addGap(35)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNick)
						.addComponent(textNick, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblIpserver)
						.addComponent(textIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(textServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPuerto, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
					.addComponent(btnEnviar)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
