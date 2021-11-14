package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket serverSocket;

	public Server(ServerSocket serverSocket) {
		super();
		this.serverSocket = serverSocket;
	}
	public void startServer() {
		try {
			while(!serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				System.out.println("A new client has connected");
				ClientHandler clientHandler = new ClientHandler(socket); //each object of this class will be responsible for communicating with client 
				Thread thread = new Thread(clientHandler); //when a class implements runnable interface => the instances of class will be executed by a separate Thread
				thread.start();
			}
		}catch (IOException e) {
			e.printStackTrace();
			
		}
	}
	public void closeServerSocket() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		}catch (IOException e) {
			// TODO: handle exception
			e.getStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(7777);
		Server server = new Server(serverSocket);
		server.startServer();
	}

}

