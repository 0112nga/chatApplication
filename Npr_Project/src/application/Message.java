package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Message implements Initializable{
	@FXML
	private Label lbmessage;
	@FXML
    private HBox hBox;
	private String message;
	private String sender;
	public Message(String message,String sender)
    {	
		this.message = message;
		this.sender = sender;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Message.fxml"));
        fxmlLoader.setController(this);
        try
        {
        	hBox = fxmlLoader.load();
           
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

	public void setMessage() {
		this.lbmessage.setText(message);
	}
	public String getMessage() {
		return message;
	}
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	public HBox getBox()
    {
        return hBox;
    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lbmessage.setMaxWidth(300);
		lbmessage.setWrapText(true);
		
	}
}
