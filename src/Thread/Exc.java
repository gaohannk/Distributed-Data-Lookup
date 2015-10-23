package Thread;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import Demo.ChordNode;
import Message.DataReplyMsg;
import Message.GetDataMsg;
import Message.GetKVMsg;
import Message.InitMsg;
import Message.JoinMsg;
import Message.LeaveMsg;
import Message.LookupMsg;
import Message.LookupReplyMsg;
import Message.NotifyMsg;

public class Exc implements Runnable {
	public Socket socket;
	private ChordNode node;
	public int port = 9010;
	public static final int FIX = 1;
	public static final int INIT = 2;
	public static final int LOOKUPDATA = 3;
	private static final int GETDATA = 4;

	public Exc(Socket socket, ChordNode node) {
		this.socket = socket;
		this.node = node;
	}

	public void lookup(Object obj) throws IOException {
		LookupMsg msg = (LookupMsg) obj;
		ChordNode resnode;
		Socket socket;
		resnode = node.findSuccessor(msg.desid);
		System.out.println("Someone want lookup key " + msg.desid);
		if (resnode.id >= msg.desid || this.node.id >= resnode.id) {
			socket = new Socket(msg.initiator, port);
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			LookupReplyMsg reply = new LookupReplyMsg(msg.desid, resnode.id,
					resnode.ip, msg.type);
			oos.writeObject(reply);
			oos.flush();
			socket.close();
		} else {
			socket = new Socket(resnode.ip, port);
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			oos.writeObject(msg);
			oos.flush();
			socket.close();
		}
	}

	public void getdata(Object obj) throws IOException, ClassNotFoundException {
		LookupReplyMsg reply = (LookupReplyMsg) obj;
		Socket socket = new Socket(reply.resip, port);
		ObjectOutputStream oos = new ObjectOutputStream(
				socket.getOutputStream());
		GetDataMsg getdata = new GetDataMsg(reply.resid);
		oos.writeObject(getdata);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		DataReplyMsg datareply = (DataReplyMsg) ois.readObject();
		System.out.println("I get the value of key is " + datareply.value);
		socket.close();
	}

	public void run() {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			Object obj = ois.readObject();
			String s = obj.getClass().getName();
			String[] ss = s.split("\\.");
			ObjectOutputStream oos;
			switch (ss[ss.length - 1]) {
			case "LookupMsg":
				lookup(obj);
				socket.close();
				break;
			case "LookupReplyMsg":
				LookupReplyMsg reply = (LookupReplyMsg) obj;
				switch (reply.type) {
				case FIX:
					if (node.next >= 4)
						node.next = 0;
					if (node.fingertable == null)
						node.fingertable = new LinkedList<ChordNode>();
					if (node.fingertable.size() >= 4)
						node.fingertable.remove(node.next);
					int num = (int) (this.node.id + Math.pow(2, node.next))%16;
					System.out.println("Node " + num + " successor is node "
							+ reply.resid);
					node.fingertable.add(node.next, new ChordNode(reply.resid,
							reply.resip));
					node.next++;
					socket.close();
					break;
				case INIT:
					socket.close();
					socket = new Socket(this.node.initnode.ip, port);
					oos = new ObjectOutputStream(socket.getOutputStream());
					JoinMsg msg = new JoinMsg(new ChordNode(reply.resid,
							reply.resip));
					oos.writeObject(msg);
					oos.flush();
					oos.close();
					socket.close();
					break;
				case LOOKUPDATA:
					System.out.println("==========================================");
					System.out.println("The key is at node " + reply.resid
							+ "ip is " + reply.resip.toString());
					System.out.println("==========================================");
					socket.close();
					break;
				case GETDATA:
					socket = new Socket(reply.resip, port);
					GetDataMsg getdatamsg = new GetDataMsg(reply.key);
					oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(getdatamsg);
					oos.flush();
					ois = new ObjectInputStream(socket.getInputStream());
					DataReplyMsg dpmsg = (DataReplyMsg) ois.readObject();
					socket.close();
					System.out.println("==========================================");
					System.out.println("I get the data item " + dpmsg.value);
					System.out.println("==========================================");
					break;
				}
				break;
			case "JoinMsg":
				JoinMsg msg = (JoinMsg) obj;
				this.node.successor = msg.succ;
				socket.close();
				break;
			case "InitMsg":
				InitMsg initmsg = (InitMsg) obj;
				this.node.initnode = new ChordNode(initmsg.initid, initmsg.ip);
				new Thread(new Search(initmsg.initid, this.node, INIT)).start();
				socket.close();
				break;
			case "GetDataMsg":
				GetDataMsg gdmsg = (GetDataMsg) obj;
				String str = node.keyvalue.get(gdmsg.keyid);
				oos = new ObjectOutputStream(socket.getOutputStream());
				DataReplyMsg datareply = new DataReplyMsg(str);
				oos.writeObject(datareply);
				oos.flush();
				socket.close();
				System.out.println("Someone want the value of key"
						+ gdmsg.keyid);
				break;
			case "NotifyMsg":
				NotifyMsg nmsg = (NotifyMsg) obj;
				this.node.notify(nmsg.node);
				socket.close();
				break;
			case "GetPreMsg":
				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(this.node.predecessor);
				oos.flush();
				socket.close();
				break;
			case "LeaveMsg":
				LeaveMsg leavemsg = (LeaveMsg) obj;
				ChordNode leavenode = leavemsg.leavenode;
				if (leavenode.id == this.node.predecessor.id) {
					for (Iterator<Integer> it = leavenode.keyvalue.keySet()
							.iterator(); it.hasNext();) {
						Integer key = it.next();
						String value = leavenode.keyvalue.get(key);
						this.node.keyvalue.put(key, value);
					}
					this.node.predecessor=leavenode.predecessor;
				}else if(leavenode.id==this.node.successor.id){
					this.node.successor=leavenode.successor;
				}
				socket.close();
				break;
			case "GetKVMsg":
				GetKVMsg getkvmsg = (GetKVMsg) obj;
				oos = new ObjectOutputStream(socket.getOutputStream());
				Hashtable<Integer, String> table = new Hashtable<Integer, String>();
				for (Iterator<Integer> it = this.node.keyvalue.keySet()
						.iterator(); it.hasNext();) {
					Integer key = it.next();
					if (key > this.node.predecessor.id && key <= getkvmsg.keyid) {
						String value = this.node.keyvalue.get(key);
						table.put(key, value);
					}
				}
				for (Iterator<Integer> it = table.keySet().iterator(); it
						.hasNext();) {
					Integer key = it.next();
					this.node.keyvalue.remove(key);
				}
				oos.writeObject(table);
				oos.flush();
				socket.close();
				break;
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
