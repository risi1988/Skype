package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;

public class WindowCHAT extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfTextoAEnviar;
	public JTextArea textArea;
	JPanel panel = new JPanel();
	String nick;
	
	public WindowCHAT(final String nick, final Socket socket_servidor) {
		this.nick = nick;
		setResizable(false);
		setTitle("Chat con "+nick); //AÑADIR AQUI NICK
		setBounds(100, 100, 288, 187);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		dlm.addElement("Pipas");
		
		tfTextoAEnviar = new JTextField();
		tfTextoAEnviar.setColumns(10);
		textArea = new JTextArea(4,22);
		textArea.setEditable(false);
		JButton btEnviar = new JButton("Enviar");
		btEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.append(Client_Tac.nick+": "+tfTextoAEnviar.getText()+"\r\n"); // HAY QUE PASAR EL TEXT AL SERVERSOCKET
				try {
					Client_Tac.envio_SEND("SEND "+nick+" "+tfTextoAEnviar.getText(), socket_servidor);
					//clt.envio_SEND("SEND "+nick+" "+tfTextoAEnviar.getText());					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JScrollPane scroll = new JScrollPane ( textArea );
	    scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	    panel.add ( scroll );
	    GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(textArea, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(tfTextoAEnviar, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btEnviar, GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btEnviar)
						.addComponent(tfTextoAEnviar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
	}
}
