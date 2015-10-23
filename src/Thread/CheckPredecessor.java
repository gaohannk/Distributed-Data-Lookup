package Thread;
import java.io.IOException;

import Demo.ChordNode;

class CheckPredecessor implements Runnable {
	public ChordNode node;
	int timeout = 100;

	public CheckPredecessor(ChordNode node) {
		this.node = node;
	}

	@Override
	public void run() {
		try {
			while (!this.node.exit) {
				if (node.predecessor != null) {
					boolean reach = node.predecessor.ip.isReachable(timeout);
					if (reach == false)
						node.predecessor = null;
				}
				Thread.sleep(10000);
			}
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
