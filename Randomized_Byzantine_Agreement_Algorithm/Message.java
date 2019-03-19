import java.io.Serializable;

public abstract class Message implements Serializable {
    private int round;
    private int value;

    public Message(int round, int value) {
        this.round = round;
        this.value = value;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
