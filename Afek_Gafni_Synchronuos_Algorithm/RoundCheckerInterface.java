import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RoundCheckerInterface extends Remote {

	public void roundCheck() throws RemoteException;

	public boolean isElectionDone() throws RemoteException;

	public void setElectionDone(boolean isElectionDone) throws RemoteException;

	public int getCurrentRound() throws RemoteException;

	public void setCurrentRound(int currentRound) throws RemoteException;

	public int getPreviousRound() throws RemoteException;

	public void setPreviousRound(int previousRound) throws RemoteException;

}
