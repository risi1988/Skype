package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class WindowGrabAudio extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	/**
	 * Create the frame.
	 */
	private boolean para = true;
	public WindowGrabAudio(final String amigo,final Socket socket_servidor) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//Conectar con el server de apa y pasarle el archivo .raw
		setTitle("Grabar Mensaje "+ amigo);
		setBounds(100, 100, 315, 158);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton btnGrabar = new JButton("Grabar");
		btnGrabar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Client_Tac.envio_Mensaje_Audio("CALL "+ amigo +" Nick: "+Client_Tac.nick, socket_servidor);
					new Thread(new Runnable() {
						public void run() {
					int seg =0;
					while (seg <59 && para){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						seg++;
					}
					if (para){
						Client_Tac.stop_Audio(socket_servidor);
						Client_Tac.estado_True();
						}
					}	
						
					}).start();
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client_Tac.estado_True();
				Client_Tac.stop_Audio(socket_servidor);
				para = false;
				dispose();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(57)
					.addComponent(btnGrabar)
					.addGap(34)
					.addComponent(btnStop)
					.addContainerGap(78, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(36)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStop)
						.addComponent(btnGrabar))
					.addContainerGap(51, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

}
