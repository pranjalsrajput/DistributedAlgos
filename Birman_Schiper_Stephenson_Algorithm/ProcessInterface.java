import java.rmi.Remote;
import java.rmi.RemoteException;

// Creating Remote interface for our application
public interface ProcessInterface extends Remote {
//    void printMsg() throws RemoteException;
  // public void broadcastMsg(Message message) throws RemoteException;

  // public void deliverMsg(Message message) throws RemoteException;
   public int[] receiveMsg(Message message) throws RemoteException;
}