import java.io.Serializable;

public class NotificationMessage extends Message implements Serializable {
    public NotificationMessage(int round, int value) {
        super(round, value);
    }
}
