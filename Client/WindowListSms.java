package Client;

import java.awt.Font;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


public class WindowListSms extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Vector<String> vsms;
	/**
	 * Create the frame.
	 */
	JTextArea textMensaje = null;
	public WindowListSms(Vector<String> vsms) {
		this.vsms = vsms;
		setTitle("Mensajes de Usuarios");
		setBounds(100, 100, 382, 234);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		textMensaje = new JTextArea();
		textMensaje.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		textMensaje.setEditable(false);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(62)
					.addComponent(textMensaje, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(66, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(textMensaje, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(40, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void anadirText() {
		// TODO Auto-generated method stub
		for(int i=0;i<vsms.size();i++){
		textMensaje.append(vsms.elementAt(i)+"\n\r");
		}
	}
}
