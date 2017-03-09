package Client;

import java.awt.Font;
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
import javax.swing.border.EmptyBorder;


public class WindowPendiente extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public WindowPendiente(final Socket socket_servidor) {
		setTitle("Mensajes Pendientes");
		setBounds(100, 100, 408, 159);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		JButton btnTrue = new JButton("TRUE");
		btnTrue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Client_Tac.mensajes_Recibir(socket_servidor);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dispose();
			}
		});
		
		JButton btnFalse = new JButton("FALSE");
		btnFalse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();//Para cerrar ventanas.
			}
		});
		
		JLabel lbldeseaLeerLos = new JLabel("\u00BFDesea leer los mensajes pendientes?");
		lbldeseaLeerLos.setFont(new Font("Tahoma", Font.PLAIN, 15));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(88, Short.MAX_VALUE)
					.addComponent(lbldeseaLeerLos, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
					.addGap(56))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(109)
					.addComponent(btnTrue)
					.addGap(32)
					.addComponent(btnFalse)
					.addContainerGap(132, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbldeseaLeerLos, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnTrue)
						.addComponent(btnFalse))
					.addContainerGap(89, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
