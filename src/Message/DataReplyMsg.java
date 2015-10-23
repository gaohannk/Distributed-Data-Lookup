package Message;
import java.io.Serializable;

public class DataReplyMsg  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String value;

	public DataReplyMsg(String str) {
		this.value = str;
	}

}
