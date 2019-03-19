
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;

public interface Interface extends Remote {
    void broadcast(Message message) throws RemoteException;
    void receive(Message message) throws RemoteException;
    void setListOfAllProcesses(Interface[] processesArray) throws RemoteException;
    void run() throws RemoteException, InterruptedException;
    Semaphore getSemaphore() throws RemoteException;
    int getId() throws RemoteException;
    int getValue() throws RemoteException;
    int getRound() throws RemoteException;
    boolean isDecided() throws RemoteException;
    String getString() throws RemoteException;
}
