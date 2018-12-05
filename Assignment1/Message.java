import java.io.Serializable;

public class Message implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int processId;
    private String message;
    private int[] vectorClock;

    public Message(int processId, String message, int[] vectorClock) {
        this.processId = processId;
        this.message = message;
        this.vectorClock = vectorClock;
    }

    public int getProcessId() {
        return this.processId;
    }

    public String getMessage() {
        return this.message;
    }

    public int[] getVectorClock() {
        return this.vectorClock;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setVectorClock(int[] vectorClock) {
        this.vectorClock = vectorClock;
    }
}