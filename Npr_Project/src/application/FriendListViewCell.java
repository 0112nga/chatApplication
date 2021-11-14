package application;

import javafx.scene.control.ListCell;

public class FriendListViewCell extends ListCell<ChatBox>{
		public FriendListViewCell(){
		}
		@Override
	    public void updateItem(ChatBox c, boolean empty)
	    {
	        super.updateItem(c,empty);
	        if (!empty) {
		        if(c != null)
		        {	
		        	Client friend = new Client();
		            friend.setUsername(c.getReceiver());
		            if(c.getObservableMessageList().size()>1) {
		            	Message message = (Message) c.getObservableMessageList().get(c.getObservableMessageList().size()-1);
		            	friend.setRecentMessage(message);
		            }
		            setGraphic(friend.getBox());
		        }
	        } else {
	            setGraphic(null);
	        }
	    }
	}

