package Thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Demo.ChordNode;

public class Waiting implements Runnable {
	private final int port = 9010;
	private ChordNode node;

	public Waiting(ChordNode node) {
		this.node = node;
	}

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			while (!this.node.exit) {
				Socket socket = server.accept();
				new Thread(new Exc(socket,this.node)).start();
			}
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}