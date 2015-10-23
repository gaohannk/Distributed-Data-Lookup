package Message;
import java.io.Serializable;
import java.net.InetAddress;

public class LookupMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int type;
	public int desid;
	public InetAddress initiator;

	public LookupMsg(int id, InetAddress ip, int type) {
		this.desid = id;
		this.initiator = ip;
		this.type = type;
	}

}
