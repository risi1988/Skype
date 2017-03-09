package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class WindowListAudio extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	/**
	 * Create the frame.
	 */
	public JList<String> list;
	public static DefaultListModel<String> dlm;
	
	public WindowListAudio(final Socket socket_servidor) {
		setTitle("Lista de mensajes");
		setBounds(100, 100, 428, 274);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		
		dlm = new DefaultListModel<String>();
		list = new JList<String>(dlm);
		
		JButton btnEscuchar = new JButton("Escuchar");
		btnEscuchar.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				String chatWith = list.getSelectedValue();
				if (chatWith!=null){
					Client_Tac.mensajes_Recibir_Voz(socket_servidor,chatWith);//Mas el nombre del archivo a reproducir
					WindowAudioMensaje wam = new WindowAudioMensaje(chatWith);
					wam.show();
					eliminarLista(chatWith);
				}
				
			}
		});
		
		JButton btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(56)
					.addComponent(list, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnEscuchar)
						.addComponent(btnSalir))
					.addGap(55))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(42)
							.addComponent(list, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(79)
							.addComponent(btnEscuchar)
							.addGap(18)
							.addComponent(btnSalir)))
					.addContainerGap(61, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

	public static void anadirLista(String men) {
		// TODO Auto-generated method stub
		dlm.addElement(men);
	}
	
	public void eliminarLista(String ar){
		int posicion = dlm.indexOf(ar);
		dlm.remove(posicion);
		if (dlm.size()==0){
			dispose();
		}
	}
}
