package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientHandler implements Runnable {
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
	// keep track all client
	public ArrayList<String> onlineCLients = new ArrayList<String>();
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	private String username;

	public ClientHandler(Socket socket) {
		super();
		try {
			this.socket = socket;
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			username = br.readLine();
			clientHandlers.add(this);
//			System.out.println("The number of clients connected: " + clientHandlers.size());
			
			broadcastMessage("SERVER: " + username + " is now online");

		} catch (IOException e) {
			close();
		}
	}

	@Override
	public void run() {
		String messageFromClient;
		while (socket.isConnected()) {
			try {
				messageFromClient = br.readLine();
				String message = getMessage(messageFromClient);
				if (message.equals("EXIT")) {
					close();
					return;
				} else if (messageFromClient != null && !messageFromClient.equals("")) {
					broadcastMessage(messageFromClient);
				}
			} catch (IOException e) {
				close();
				return;
			}
		}
	}

	public void broadcastMessage(String message) {
//		System.out.println("Message: "+ message);
		if(message.contains("@To@")) {
			String m = getMessage(message);
			String name = getPrivatePriendName(message);
			for (ClientHandler clientHandler : clientHandlers) {
				if(clientHandler.username.equals(name)) {
					try {
						clientHandler.bw.write("@FROM@"+this.username+":"+m);
						clientHandler.bw.newLine();
						clientHandler.bw.flush();
					} catch (IOException e) {
						e.printStackTrace();
						close();
					}
				}
			}
			return;
		}
		for (ClientHandler clientHandler : clientHandlers) {
				try {
					if (message.contains("SERVER")) {
						sendOnlineFriendList(clientHandler);
					}
					clientHandler.bw.write(message);
					clientHandler.bw.newLine();
					clientHandler.bw.flush();
				} catch (IOException e) {
					close();
				}
		}
	}
	public String getMessage(String messageFromClient) {
		String[] parts = messageFromClient.split(":");
		return parts[1];
	}
	public String getPrivatePriendName(String messageFromClient) {
		String[] parts = messageFromClient.split("@");
		return parts[2].split(":")[0];
	}
	public void sendOnlineFriendList(ClientHandler clientHandler) throws IOException {
			onlineCLients = getClientNames();
			for(int i = 0; i < onlineCLients.size(); i++) {
				clientHandler.bw.write("@FRIENDLIST@"+onlineCLients.get(i));
				clientHandler.bw.newLine();
				clientHandler.bw.flush();
			}
	}
	public ArrayList<String> getClientNames() {
		onlineCLients = new ArrayList<String>();
		for (ClientHandler clientHandler : clientHandlers) {
			onlineCLients.add(clientHandler.username);
		}
		return onlineCLients;
	}
	public void removeClientHandler() {
		clientHandlers.remove(this);
		onlineCLients.remove(this.username);
		broadcastMessage("SERVER: " + username + " has lefted the chat");
	}

	public void close() {
		removeClientHandler();
		try {
			if (br != null) {
				br.close();
			}
			if (bw != null) {
				bw.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
