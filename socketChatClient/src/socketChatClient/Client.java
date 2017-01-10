package socketChatClient;

import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;

public class Client extends JFrame{
	
	//ui components
	private JTextField userText;
	private JTextArea chatWindow;
	//streams
	private ObjectOutputStream output;
	private ObjectInputStream input;
	//socket
	private Socket connection;
	private String message = "";
	private String serverIP;
	
	//constructor
	public Client(String host) {
		//create GUI
		super("Client side.");
		serverIP = host;
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
	
	//connect to server
	public void startRunning() {
		try {
			/*
			 * connect to server
			 */
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException exception) {
			showMessage("\nClient terminated exception.");
		}catch(IOException exception) {
			exception.printStackTrace();
		}finally {
			closeAll();
		}
	}
	
	private void connectToServer() throws IOException {
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 5555);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nReady to send message.");
	}
	
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException exception) {
				showMessage("\nNot supported object type.");
			}
		}while(!message.equals("SERVER - END"));
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
	
	//send messages to server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException exception) {
			chatWindow.append("\n Can't send message.");
		}
	}
	
	//update window
	private void showMessage(final String m) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					chatWindow.append(m);
				}
			});
	}
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
