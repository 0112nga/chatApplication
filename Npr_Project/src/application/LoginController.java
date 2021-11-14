package application;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginController implements Initializable {
	@FXML
	private TextField tfUsername;
	@FXML
	private HBox hbinput;
	@FXML
	private Button btnLogin;
	private Stage stage;
	private Scene scene;
	private Parent root;
	private Socket socket;
	private Client client;

	public void login(ActionEvent e) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		root = loader.load();
		Controller controller = loader.getController();
		String username = tfUsername.getText();
		if(username!=null && !username.equals("")) {
			stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			socket = new Socket("localhost", 7777);
			client = new Client(socket, username);
			controller.setClient(client);
			stage.setOnCloseRequest(event -> {
				try {
					setlogoutBtn(stage);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		DropShadow drop = new DropShadow();
		drop.setBlurType(BlurType.GAUSSIAN);
		drop.setColor(Color.rgb(0, 0, 0, 0.5));
		drop.setHeight(100);
		drop.setWidth(150);
		drop.setOffsetY(10);
		drop.setSpread(0.2);
		drop.setRadius(20);
		btnLogin.setEffect(drop);
		hbinput.setEffect(drop);

	}

	public void setlogoutBtn(Stage stage) throws IOException {
		client.close();
		stage.close();
	}

}
