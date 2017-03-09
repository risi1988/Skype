package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class WindowNoCall extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Esta ventana saldra tanto si el usuario contrario no acepta la llamada echa , si el usuario ya esta hablando con otra pesona y si el usuario esta desconectado.

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public WindowNoCall(final String amigo, final Socket socket_servidor) {
		setTitle("Dejar mensaje a: "+ amigo);
		setBounds(100, 100, 428, 173);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblElUsuario = new JLabel("EL usuario : "+amigo+" no puede hablar con usted que desea hacer:");
		
		JButton btnMensajeDeVoz = new JButton("Mensaje de voz");
		btnMensajeDeVoz.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				WindowGrabAudio wga = new WindowGrabAudio(amigo,socket_servidor);
				wga.show();
				dispose();
			}
		});
		
		JButton btnSalirAMenu = new JButton("Salir a Menu");
		btnSalirAMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(42)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnMensajeDeVoz)
							.addGap(76)
							.addComponent(btnSalirAMenu))
						.addComponent(lblElUsuario, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(65, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblElUsuario, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(27)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSalirAMenu)
						.addComponent(btnMensajeDeVoz))
					.addContainerGap(27, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
