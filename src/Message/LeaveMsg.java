package Message;
import java.io.Serializable;

import Demo.ChordNode;

public class LeaveMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public ChordNode leavenode;
	public LeaveMsg( ChordNode leavenode) {
		this.leavenode = leavenode;
	}
}
