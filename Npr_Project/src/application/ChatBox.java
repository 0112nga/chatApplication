package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatBox {
	private String receiver;
	private String sender;
	private ArrayList<String> array = new ArrayList<String>();
	private ObservableList observableMessageList = FXCollections.observableArrayList();

	public ChatBox(String receiver,String sender) {
		super();
		this.receiver = receiver;
		this.sender = sender;
		array.add(receiver);
		array.add(sender);
		
	}
	public boolean isChatBoxExist(String receiver,String sender) {
		return this.receiver.equals(receiver)&&this.sender.equals(sender)||
				this.receiver.equals(sender)&&this.sender.contains(receiver);
	}
	public ArrayList<String> getArray() {
		return array;
	}

	public void setArray(ArrayList<String> array) {
		this.array = array;
	}
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void addNewMessage(Message message) {
			this.observableMessageList.add(message);
	}
	public ObservableList getObservableMessageList() {
		return observableMessageList;
	}
	
	
}
