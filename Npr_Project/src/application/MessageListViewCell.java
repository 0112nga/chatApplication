package application;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;

public class MessageListViewCell extends ListCell<Message>{
	private Client client;
	public MessageListViewCell(Client client){
		this.client = client;
	}
	@Override
    public void updateItem(Message message, boolean empty)
    {
        super.updateItem(message,empty);
        if (!empty) { //advoid duplicate
	        if(message != null)
	        {	
	        	if(message.getSender()!=null && message.getSender().equals(client.getClientname())) {
	                 setAlignment(Pos.TOP_RIGHT);
	        	}
	            message.setMessage();
	            setGraphic(message.getBox());
	        }
	    } else {
	        setGraphic(null);
	    }
    }
}
