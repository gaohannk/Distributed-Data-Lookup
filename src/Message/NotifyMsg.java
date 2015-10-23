package Message;
import java.io.Serializable;

import Demo.ChordNode;

public class NotifyMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotifyMsg(ChordNode node) {
		this.node = node;
	}

	public ChordNode node;

}
