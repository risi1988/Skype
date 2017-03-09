package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;


public class WindowAudio extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */

	public WindowAudio(final String nick, final Socket socket_servidor) {
		setResizable(false);
		setTitle("Audio con"+nick);
		setBounds(100, 100, 346, 158);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				
				//Client_Tac.llamda("OK. "+nick,socket_servidor);
				//Conexion con el socket reproducir_cc
				try {
					Client_Tac.startListening();
					Client_Tac.estado=false;
					WindowConv wc = new WindowConv(nick);//Ahora mismo no se si es de nick o de que.
					wc.show();
					dispose();
					Client_Tac.llamda("OK. "+nick,socket_servidor);
					Client_Tac.listen(nick);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		JButton btnRechazar = new JButton("Rechazar");
		btnRechazar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client_Tac.rechazar("NO. "+nick,socket_servidor);
				Client_Tac.estado=true;
				dispose();
				
			}
		});
		
		JLabel lblLlamadaEntranteDe = new JLabel("Llamada entrante de: "+nick);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(43)
					.addComponent(btnAceptar)
					.addPreferredGap(ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
					.addComponent(btnRechazar)
					.addGap(63))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(81)
					.addComponent(lblLlamadaEntranteDe)
					.addContainerGap(81, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblLlamadaEntranteDe)
					.addPreferredGap(ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAceptar)
						.addComponent(btnRechazar))
					.addGap(28))
		);
		contentPane.setLayout(gl_contentPane);
	}


}
