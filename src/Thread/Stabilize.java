package Thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Demo.ChordNode;
import Message.GetPreMsg;
import Message.NotifyMsg;

public class Stabilize implements Runnable {
	ChordNode node;
	public int port = 9010;

	public Stabilize(ChordNode node) {
		this.node = node;
	}

	public void run() {
		while (!this.node.exit) {
			try {
				stablize();
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void stablize() throws IOException, ClassNotFoundException {
		Socket socket = new Socket(node.successor.ip, port);
		ObjectOutputStream oos = new ObjectOutputStream(
				socket.getOutputStream());
		GetPreMsg msg = new GetPreMsg();
		oos.writeObject(msg);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		ChordNode pre = (ChordNode) ois.readObject();
		socket.close();
		ChordNode x = pre;
		if (x != null) {
			if ((x.id > node.id && x.id < node.successor.id)
					|| (node.id < x.id && x.id > node.successor.id && node.id >= node.successor.id)
					|| (node.id > x.id && x.id < node.successor.id && node.id >= node.successor.id))
				node.successor = x;
		}
		socket = new Socket(node.successor.ip, port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		NotifyMsg notimsg = new NotifyMsg(this.node);
		oos.writeObject(notimsg);
		oos.flush();
		socket.close();
	}
}
