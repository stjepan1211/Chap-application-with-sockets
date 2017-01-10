package socketChatServer;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
	
	//ui components
	private JTextField userText;
	private JTextArea chatWindow;
	//streams
	private ObjectOutputStream output;
	private ObjectInputStream input;
	//server and socket
	private ServerSocket server;
	private Socket connection;

	//constructor
	public Server() {
		//create GUI
		super("Server side.");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(500, 300);
		setVisible(true);
	}
	
	//set up and run server
	public void startRunning() {
		try {
			//instantiate ServerSocket object
			//aka instantiate server with port number and how many people can wait on server
			server = new ServerSocket(5555, 5);
			//infinite loop on server side
			while(true) {
				try {
					/*
					 * listening is there any clients to connect on the server
					 * display connection information
					 */
					waitForConnection();
					/*
					 * setting up input and output streams
					 */
					setupStreams();
					/*
					 * allows to send messages between client and sever
					 */
					whileChatting();
				} catch (Exception exception) {
					showMessage("\nServer ended the connection!");
				} finally {
					//close streams and sockets when chatting is done
					closeAll();
				}
			}
		}catch(IOException exception) {
			exception.printStackTrace();
		}
	}
	
	private void waitForConnection() throws IOException {
		showMessage(" Waiting for client... \n");
		//when client connects socket is made between client and server
		//every client has own socket
		connection = server.accept();
		showMessage("Connected with: " + connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		//flush() push the rest of data from stream
		//like water stays in water pipe
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are setted up.");
	}
	
	private void whileChatting() throws IOException{
		String message = "You are now connected";
		sendMessage(message);
		ableToType(true);
		do {
			//conversation
			try {
				//send message to client
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException exception) {
				showMessage("\nCan't get message from client.");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	private void closeAll() {
		showMessage("\nClosing connections...\n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	//send message to client
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch (IOException exception) {
			chatWindow.append("\n Can't send message");
		}
	}
	//updates chat window
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					chatWindow.append(text);
				}
			});
	}
	//allow or forbid user to write in text field 
	private void ableToType(final boolean able) {
		SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						userText.setEditable(able);
					}
				});
	}
}
