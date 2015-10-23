package Message;
import java.io.Serializable;
import java.net.InetAddress;

public class LookupReplyMsg  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int key;
	public int resid;
	public InetAddress resip;
	public int type;

	public LookupReplyMsg(int key,int id, InetAddress ip,int type) {
		this.key=key;
		this.resid = id;
		this.resip = ip;
		this.type=type;
	}
}
