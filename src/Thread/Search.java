package Thread;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import Demo.ChordNode;
import Message.LookupMsg;
import Message.LookupReplyMsg;

public class Search implements Runnable {
	public int identifier;
	public ChordNode node;
	public int port = 9010;
	public int type;

	public Search(int identifier, ChordNode node, int type) {
		this.identifier = identifier;
		this.node = node;
		this.type = type;
	}

	@Override
	public void run() {
		ChordNode resnode;
		resnode = node.findSuccessor(identifier);
		if ((node.id < identifier && identifier <= resnode.id)
				|| (node.id < identifier && identifier >= resnode.id && resnode.id <= node.id)
				|| (node.id > identifier && identifier <= resnode.id && resnode.id <= node.id)) {
			try {
				Socket socket = new Socket(this.node.ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				LookupReplyMsg msg = new LookupReplyMsg(identifier, resnode.id, resnode.ip, this.type);
				oos.writeObject(msg);
				oos.flush();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Socket socket;
			try {
				socket = new Socket(resnode.ip, port);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				LookupMsg msg = new LookupMsg(identifier, this.node.ip, this.type);
				oos.writeObject(msg);
				oos.flush();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean isReachable(InetAddress ip) {
		try {
			Socket socket = new Socket(ip, port);
			socket.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
