import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

public class ChatClient {
	String msg = null;
	private Socket clientSocket = null;
	private BufferedReader in;
	private PrintWriter out;
	final Scanner sc = new Scanner(System.in);
	JFrame frame = new JFrame("Client");
	JTextField textField = new JTextField(50);
	JTextArea messageArea = new JTextArea(16, 50);

	public ChatClient () throws Exception{
		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
		frame.pack();
		
		clientSocket = new Socket("localhost", 5000);
		textField.setEditable(true);
		messageArea.setEditable(true);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		
		
		Thread sender = new Thread(new Runnable(){	
			@Override
			public void run(){
				textField.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						msg = textField.getText();
						textField.setText("");
						out.println(msg);
						messageArea.append("[You] : " + msg + "\n");
					}
				});
			}
		});
		sender.start();
		
		Thread receiver = new Thread(new Runnable() {
			String msg = null;
			@Override
			public void run(){
				try{
					msg = in.readLine();
					while(!msg.equals("/quit")){
						messageArea.append("[Server] : " + msg + "\n");
						msg = in.readLine();
					}
					System.out.println("Server out of service");
					
					out.close();
					clientSocket.close();
				}
				catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
			
		});
		receiver.start();
	}
	
	public static void main(String[] args) throws Exception {
		var server = new ChatClient();
	}
}