package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;

public class Client {
	@FXML
	private Label username;
	@FXML
	private Label recentMessage;
	@FXML
	private HBox friendBox;

	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	private String clientname;
	private ArrayList<String> onlineFriends;
	ObservableList observableMessageList = FXCollections.observableArrayList();
	ObservableList observableListFriend = FXCollections.observableArrayList();
	ObservableList observablePrivateChatBox = FXCollections.observableArrayList();
	private ArrayList<ChatBox> chatlist = new ArrayList<ChatBox>();

	public Client() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Friend.fxml"));
		fxmlLoader.setController(this);
		try {
			friendBox = fxmlLoader.load();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Client(Socket socket, String username) {
		super();
		this.socket = socket;
		this.clientname = username;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			onlineFriends = new ArrayList<String>();
			setClientToClientHandler(username);
		} catch (IOException e) {
			close();
		}

	}

	public void setUsername(String username) {
		this.username.setText(username);
		this.clientname = username;
	}
	public void setRecentMessage(Message message) {
		this.recentMessage.setText(message.getMessage());
		recentMessage.setWrapText(true);
		recentMessage.setTextOverrun(OverrunStyle.ELLIPSIS);
	}

	public String getClientname() {
		return clientname;
	}

	public HBox getBox() {
		return friendBox;
	}

	public void setClientToClientHandler(String name) {
		try {
			bw.write(name);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
	}

	public void sendMessage(String message) {
		try {
			while (socket.isConnected() && !socket.isClosed() && message != null && !message.equals("")) {
				bw.write(clientname + ":" + message);
				bw.newLine();
				bw.flush();
				message = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listenForMessage() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String messageFromChat;
				while (socket.isConnected() && !socket.isClosed()) {
					try {
						messageFromChat = br.readLine();
						// load onlinefriends List
						while (messageFromChat!= null && messageFromChat.contains("@FRIENDLIST@")) {
							String name = getFriendName(messageFromChat);
							if (onlineFriends.contains(name)) {
								messageFromChat = br.readLine();
							} else {
								if (!name.equals(clientname)) {
									onlineFriends.add(name);
									chatlist.add(new ChatBox(name, clientname));
									// create private chatbox
								}
								messageFromChat = br.readLine();
							}
						}
						Platform.runLater(() -> {
							ObservableList temp = observableListFriend;
							observableListFriend.clear();
							temp.addAll(chatlist);
							observableListFriend = temp;
						});
						if (messageFromChat!= null && messageFromChat.contains("@FROM@")) {
							receiveMessage(messageFromChat);
							listenForMessage();
						} else {
							final String mess = messageFromChat;
							Platform.runLater(() -> {
								observableMessageList.add(new Message(mess, null));
							});
						}
					} catch (IOException e) {
						close();
					}
				}

			}
		}).start();
	}

	public String getFriendName(String message) {
		return message.split("@")[2];
	}

	public void receiveMessage(String messageFromChat) {
		String[] parts = messageFromChat.split(":");
		String message = parts[1];
		String sender = parts[0].split("@")[2];
		for (int i = 0; i < chatlist.size(); i++) {
			final int j = i;
			if (chatlist.get(i).isChatBoxExist(sender, getClientname())) {
				Platform.runLater(() -> {
					chatlist.get(j).addNewMessage(new Message(message, sender));
				});
			}
		}

	}

	public void sendMessageTo(String friendName, String message) {
		try {
			while (socket.isConnected() && !socket.isClosed() && message != null && !message.equals("")) {
				final String m = message;
				for (int i = 0; i < chatlist.size(); i++) {
					if (chatlist.get(i).isChatBoxExist(friendName, getClientname())) {
						chatlist.get(i).addNewMessage(new Message(message, clientname));
					}
				}
				bw.write(clientname + "@To@" + friendName + ":" + message);
				bw.newLine();
				bw.flush();
				message = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isPrivateChatBoxExist(String reciever, String sender) {
		for (int i = 0; i < chatlist.size(); i++) {
			if (chatlist.get(i).getArray().contains(reciever) && chatlist.get(i).getArray().contains(sender)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<ChatBox> getChatlist() {
		return chatlist;
	}

	public BufferedReader getBufferedReader() {
		return br;
	}

	public BufferedWriter getBufferedWriter() {
		return bw;
	}

	public Socket getSocket() {
		return socket;
	}

	public void close() {
		sendMessage("EXIT");
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
			System.out.println("Client logout");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
