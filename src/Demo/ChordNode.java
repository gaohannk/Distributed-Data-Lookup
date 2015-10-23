package Demo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import Message.GetKVMsg;
import Message.InitMsg;
import Message.LeaveMsg;
import Thread.FixFinger;
import Thread.Stabilize;
import Thread.Waiting;

public class ChordNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public LinkedList<ChordNode> fingertable;
	public ChordNode successor;
	public ChordNode predecessor;
	public InetAddress ip;
	public Hashtable<Integer, String> keyvalue;
	public int port = 9010;
	public ChordNode initnode;
	public int next = 0;
	public boolean exit = false;

	public ChordNode(int id, InetAddress ip) {
		this.id = id;
		this.ip = ip;
	}

	public ChordNode findSuccessor(int identifier) {
		if ((this.id < identifier && identifier <= successor.id)
				|| (this.id < identifier && identifier >= successor.id && this.id >= successor.id)
				|| (this.id > identifier && identifier <= successor.id && this.id >= successor.id))
			return successor;
		else
			return closetPrecedingNode(identifier);

	}

	public ChordNode closetPrecedingNode(int identifier) {
		for (int i = fingertable.size() - 1; i >= 0; i--) {
			int fing = fingertable.get(i).id;
			if ((fing > this.id && fing < identifier) || (this.id < fing && fing > identifier && this.id >= identifier)
					|| (this.id > fing && fing < identifier && this.id >= identifier))
				return fingertable.get(i);
		}
		return this;
	}

	public void notify(ChordNode node) {
		if (this.predecessor == null || (node.id > this.predecessor.id && node.id < this.id)
				|| (this.predecessor.id < node.id && node.id > this.id && this.predecessor.id >= this.id)
				|| (this.predecessor.id > node.id && node.id < this.id && this.predecessor.id >= this.id))
			this.predecessor = node;
	}

	public void initialize() throws UnknownHostException {
		this.keyvalue = new Hashtable<Integer, String>();
		for (int i = 0; i < 16; i++) {
			this.keyvalue.put(i, "This is data item " + i);
		}
		this.successor = this;
		this.predecessor = this;
		this.fingertable = new LinkedList<ChordNode>();

		for (int i = 0; i < 4; i++) {
			this.fingertable.add(i, this);
		}
		new Thread(new Waiting(this)).start();
		new Thread(new Stabilize(this)).start();
		//new Thread(new CheckPredecessor(this)).start();
		new Thread(new FixFinger(this)).start();
	}

	public void join(ChordNode node) throws IOException, ClassNotFoundException {
		this.predecessor = null;
		Socket socket = new Socket(node.ip, port);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		InitMsg initmsg = new InitMsg(this.id, this.ip);
		oos.writeObject(initmsg);
		socket.close();

		new Thread(new Waiting(this)).start();
		while (this.successor == null) {
			System.out.println("successor is not get");
		}
		// get keyvalue pair
		socket = new Socket(this.successor.ip, port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		GetKVMsg getkvmsg = new GetKVMsg(this.id);
		oos.writeObject(getkvmsg);
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

		this.keyvalue = new Hashtable<Integer, String>();
		@SuppressWarnings("unchecked")
		Hashtable<Integer, String> table = (Hashtable<Integer, String>) ois.readObject();
		socket.close();
		for (Iterator<Integer> it = table.keySet().iterator(); it.hasNext();) {
			Integer key = it.next();
			String value = table.get(key);
			keyvalue.put(key, value);
		}

		new Thread(new Stabilize(this)).start();
		//new Thread(new CheckPredecessor(this)).start();
		new Thread(new FixFinger(this)).start();
	}

	public void leave() throws IOException {
		Socket socket = new Socket(successor.ip, port);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		LeaveMsg msg = new LeaveMsg(this);
		oos.writeObject(msg);
		oos.flush();
		socket.close();
		socket = new Socket(predecessor.ip, port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		msg = new LeaveMsg(this);
		oos.writeObject(msg);
		oos.flush();
		socket.close();
		this.exit = true;
		this.keyvalue.clear();
		this.fingertable.clear();

	}

	public void list() {
		System.out.println("===Finger==Table=====");
		if (this.successor != null)
			System.out.println("Successor is node " + this.successor.id);
		if (this.predecessor != null)
			System.out.println("Predecessor is node " + this.predecessor.id);
		for (int i = 0; i < fingertable.size(); i++) {
			int currentid = (int) (this.id + Math.pow(2, i))%16;
			System.out.println("N" + currentid + "----->" + "N" + fingertable.get(i).id);
		}
		System.out.println("===Finger==Table======");
		System.out.println("===Key======Value=====");
		for (Iterator<Integer> it = this.keyvalue.keySet().iterator(); it.hasNext();) {
			Integer key = it.next();
			String value = this.keyvalue.get(key);
			System.out.println(key + "---->" + value);
		}
		System.out.println("===Key======Value=====");
	}
}
