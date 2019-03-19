import java.io.Serializable;

public class ProposalMessage extends Message implements Serializable {
    public ProposalMessage(int round, int value) {
        super(round, value);
    }

}
