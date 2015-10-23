package Message;
import java.io.Serializable;
import java.net.InetAddress;

public class InitMsg  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int initid;
	public InetAddress ip;

	public InitMsg(int id, InetAddress ip) {
		this.initid = id;
		this.ip=ip;
	}

}
