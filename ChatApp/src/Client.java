import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {
	
	private static JPanel panel;
	private static JLabel label;
	private static  JTextArea textArea;
	private static JTextField textField;
	private static JButton button;
	private static JScrollPane scroll;
	
	Socket socket;
	
	DataInputStream din;
	DataOutputStream dout;
	
	public Client() {
		
		panel = new JPanel();
		panel.setLayout(null);
		add(panel);
		
		label = new JLabel("Iron Man", JLabel.CENTER);
		label.setBounds(0, 0, 450, 50);
		panel.add(label);
		
		textArea = new JTextArea();
		//textArea.setBounds(0, 50, 450, 350);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		//panel.add(textArea);
		
		scroll = new JScrollPane(textArea);
		scroll.setBounds(0, 50, 450, 350);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scroll);
		
		textField = new JTextField();
		textField.setBounds(0, 400, 370, 40);
		panel.add(textField);
		
		button = new JButton("send");
		button.setBounds(370, 400, 80, 40);
		button.addActionListener(this);
		panel.add(button);
		
		setSize(450,480);
		setTitle("Client");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		textArea.setEditable(false);
		
		try {
			System.out.println("sending request to server...");
			socket = new Socket("192.168.238.99", 2024);
			System.out.println("connected!");
			
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());

			startReading();
			startWriting();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void startReading() {
		Runnable r1 = () -> {
			try {
				while (true) {
					String message = din.readUTF();
					if (message.equals("exit")) {
						System.out.println("server terminated the chat!");
						JOptionPane.showMessageDialog(null, "server terminated the chat!");
						textField.setEditable(false);
						socket.close();
						setVisible(false);
						break;
					}
					textArea.append("\t\t\t"+ message +"\n");
				}
			}catch(Exception e) {
				System.out.println("connection closed!");
			}
		};
		new Thread(r1).start();
	}
	
	public void startWriting() {
		Runnable r2 = () -> {
			try {
				while (!socket.isClosed()) {
					DataInputStream in = new DataInputStream(System.in);
					String content = in.readUTF();
					dout.writeUTF(content);
					if (content.equals("exit")) {
						socket.close();
					}
				}
			}catch(Exception e) {
				System.out.println("connection closed!");
			}
		};
		new Thread(r2).start();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Client();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		try {
			if (ae.getSource() == button) {
				String contentToSend = textField.getText();
				if (contentToSend.equals("exit")) {
					setVisible(false);
				}else {
					textArea.append(contentToSend + "\n");
				}
				dout.writeUTF(contentToSend);
				textField.setText("");
				textField.requestFocus();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
