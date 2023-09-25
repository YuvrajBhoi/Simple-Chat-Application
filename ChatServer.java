import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.awt.event.*;

public class ChatServer {
	String msg = null;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private Scanner sc = new Scanner(System.in);
	JFrame frame = new JFrame("Server");
	JTextField textField = new JTextField(50);
	JTextArea messageArea = new JTextArea(16, 50);

	public ChatServer () throws Exception{
		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
		frame.pack();

		serverSocket = new ServerSocket(5000);
		clientSocket = serverSocket.accept();
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

		Thread receive = new Thread(new Runnable(){
			String msg = null;
			@Override
			public void run(){
				try{
					msg = in.readLine();
					while(!msg.equals("/quit")){
						messageArea.append("[Client] : " + msg + "\n");
						msg = in.readLine();
					}
					System.out.println("Client Disconnected");
					
					out.close();
					clientSocket.close();
					serverSocket.close();
				}
				catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
		});
		receive.start();
	}
	
	public static void main(String[] args) throws Exception {
		var server = new ChatServer();
	}
}