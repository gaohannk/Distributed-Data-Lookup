package Message;
import java.io.Serializable;
public class GetDataMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int keyid;

	public GetDataMsg(int keyid) {
		this.keyid = keyid;
	}
}
