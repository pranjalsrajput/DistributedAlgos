import java.io.Serializable;

public class Message implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int level;
    private int id;
    private boolean isAck=false;
    private int originalProcessId;

    public Message(int level, int id) {
        this.level = level;
        this.id = id;
    }

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAck() {
		return isAck;
	}

	public void setAck(boolean isAck) {
		this.isAck = isAck;
	}

	public int getOriginalProcessId() {
		return originalProcessId;
	}

	public void setOriginalProcessId(int originalProcessId) {
		this.originalProcessId = originalProcessId;
	}

}