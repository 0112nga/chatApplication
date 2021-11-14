package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class Controller implements Initializable {
	@FXML
	private ImageView btsendmessage;
	@FXML
	private TextField tfinputmessage;
	@FXML
	private Button btnsend;
	@FXML
	private ListView<Message> messageHolder;
	@FXML
	private ListView<ChatBox> lvfriendsHolder;
	@FXML
	private Label lbusername;
	@FXML
	private BorderPane mainBorder;
	@FXML
	private Button btngroupchat;
	private Client client;
	private String currentFriend;
	private ChatBox privateChatBox;
	ObservableList observableListFriend = FXCollections.observableArrayList();

	public void setUsername(String name) {
		lbusername.setText(name);
		currentFriend = name;
	}

	public void setClient(Client client) {
		this.client = client;
		createMessageListView(client.observableMessageList);
		createFriendListView();
		client.listenForMessage();
		setUsername(client.getClientname());
	}
	//action of sendbtn
	public void sendMessage() {
		String inputMessage = tfinputmessage.getText();
		tfinputmessage.clear();
		if (!currentFriend.equals(client.getClientname())) {
			client.sendMessageTo(currentFriend, inputMessage);
			return;
		}
		client.sendMessage(inputMessage);

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	public void groupChat() {
		currentFriend = client.getClientname();
		setUsername(currentFriend);
		lvfriendsHolder.getSelectionModel().clearSelection();
		createMessageListView(client.observableMessageList);
	}
	public void privateChat() {
		createFriendListView();
	}
	public void createFriendListView() {
		lvfriendsHolder.setItems(client.observableListFriend);
		lvfriendsHolder.setCellFactory(new Callback<ListView<ChatBox>, ListCell<ChatBox>>() {

			@Override
			public ListCell<ChatBox> call(ListView<ChatBox> arg0) {
				return new FriendListViewCell();
			}
		});
		lvfriendsHolder.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ChatBox>() {
			@Override
			public void changed(ObservableValue<? extends ChatBox> arg0, ChatBox arg1, ChatBox arg2) {
				if(lvfriendsHolder.getSelectionModel().getSelectedItem() == null) {
					return;
				}
				String name = lvfriendsHolder.getSelectionModel().getSelectedItem().getReceiver();
				if (name != null && !name.equals(currentFriend)) {
					currentFriend = name;
				}
				for (int i = 0; i < client.getChatlist().size(); i++) {
					if (client.getChatlist().get(i).isChatBoxExist(currentFriend, client.getClientname())) {
						privateChatBox = client.getChatlist().get(i);
					}

				}
				createMessageListView(privateChatBox.getObservableMessageList());
				setUsername(currentFriend);
			}
		});
	}

	public void createMessageListView(ObservableList observableList) {
		messageHolder.setItems(observableList);

		messageHolder.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {

			@Override
			public ListCell<Message> call(ListView<Message> arg0) {
				return new MessageListViewCell(client);
			}
		});
		client.observableMessageList.addListener(new ListChangeListener<Message>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Message> c) {
				Platform.runLater(() -> messageHolder.scrollTo(c.getList().size() - 1));

			}

		});

	}
}
//#417A8A
