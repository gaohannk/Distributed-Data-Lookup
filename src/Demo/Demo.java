package Demo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import Thread.*;


public class Demo {

	private static final int LOOKUPDATA = 3;
	private static final int GETDATA = 4;

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException,
			InterruptedException, ExecutionException {

		System.out.println("Please assign id for this node");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		int id = Integer.parseInt(scanner.nextLine());
		ChordNode node = new ChordNode(id, InetAddress.getByName("54.68.175.196")); // initialize
		// initialize node for a new chord ring. // node
		System.out.println("Initialize Node?(yes/no)");
		scanner = new Scanner(System.in);
		if (scanner.nextLine().equals("yes")){
			node.initialize();
			System.out.println("This is first node, so Join operation is invalid");
		}
		while (true) {
			System.out
					.println("please input the index\n1.Join   2.Leave   3.List finger table   4.Search a data item    "
							+ "5.Get data item");
			scanner = new Scanner(System.in);
			String func = scanner.nextLine();
			switch (func) {
			case "1":
				System.out.println("Please Input node number for join");
				scanner = new Scanner(System.in);
				id = Integer.parseInt(scanner.nextLine());
				System.out.println("Please Input node IP");
				scanner = new Scanner(System.in);
				ChordNode n = new ChordNode(id, InetAddress.getByName(scanner.nextLine()));
				node.join(n);
				System.out.println("Join Successful");
				break;
			case "2":
				node.leave();
				System.out.println("Leave Successful");
				break;
			case "3":
				node.list();
				break;
			case "4":
				System.out.println("Please Input data item you want");
				scanner = new Scanner(System.in);
				int key = Integer.parseInt(scanner.nextLine());
				if ((node.predecessor.id < key && key <= node.id)
						|| (node.predecessor.id < key && key >= node.id && node.predecessor.id >= node.id)
						|| (node.predecessor.id > key && key <= node.id && node.predecessor.id >= node.id)) {
					System.out.println("==========================================");
					System.out.println("The key is at node " + node.id + "ip is " + node.ip.toString());
					System.out.println("==========================================");
				} else {
					new Thread(new Search(key, node, LOOKUPDATA)).start();
				}
				break;
			case "5":
				System.out.println("Please Input data item you want");
				scanner = new Scanner(System.in);
				key = Integer.parseInt(scanner.nextLine());
				if ((node.predecessor.id < key && key <= node.id)
						|| (node.predecessor.id < key && key >= node.id && node.predecessor.id >= node.id)
						|| (node.predecessor.id > key && key <= node.id && node.predecessor.id >= node.id)) {
					System.out.println("==========================================");
					System.out.println("I get the data item " + node.keyvalue.get(key));
					System.out.println("==========================================");
				} else {
					new Thread(new Search(key, node, GETDATA)).start();
				}
				break;
			default:
				System.out.println("Input wrong, please input again");
			}
		}
	}
}
