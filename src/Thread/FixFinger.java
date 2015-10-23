package Thread;

import Demo.ChordNode;



public class FixFinger implements Runnable {
	ChordNode node;
	int next = 0;
    public static final int FIX=1;

	public FixFinger(ChordNode node) {
		this.node = node;
	}

	@Override
	public void run() {
		while (!this.node.exit) {
			try {
				if (next >= 4)
					next = 0;
				int id=(int) (Math.pow(2, next) + node.id)%16;
				new Thread(new Search(id, this.node,FIX)).start();
				next++;
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
