package Message;
import java.io.Serializable;

public class GetKVMsg implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int keyid;
	public GetKVMsg(int id) {
		this.keyid = id;
	}


}
