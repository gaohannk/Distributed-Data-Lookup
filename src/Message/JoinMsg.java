package Message;
import java.io.Serializable;

import Demo.ChordNode;

public class JoinMsg implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ChordNode succ;

	public JoinMsg(ChordNode succ) {
		this.succ = succ;
	}
}
