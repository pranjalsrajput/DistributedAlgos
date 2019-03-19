import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    static Interface[] processesArray;
    static Thread[] threads;
    static boolean done;

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException {
        main2(6, 1);
    }

    public static void main2(int totalProcesses, int faultyProcesses) throws AlreadyBoundException, RemoteException, NotBoundException {
        // Creates registry to which stubs can be bound by servers and discovered by clients
        // Creates a registry local to the server
        Registry registry = LocateRegistry.createRegistry(1099);
        System.out.println("Created registry");

        int countFaultyProcesses = faultyProcesses;

        // binding the objects in the registry
        for (int i = 0; i < totalProcesses; i++) {
            // unique key + stub
            boolean faultyProcess = false;
            if (countFaultyProcesses > 0) {
                faultyProcess = true;
                countFaultyProcesses --;
            } else {
                faultyProcess = false;
            }
            registry.bind(String.valueOf(i), new Proces(i, faultyProcess));
        }

        //setRegistryList();
        start();
    }

    //Sets the list of all registered objects in the object.
//    public static void setRegistryList() throws RemoteException, NotBoundException {
//        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
//        int numberOfRegistries = registry.list().length;
//        for(int i = 0; i < registry.list().length; i++) {
//            System.out.println(registry.list()[i]);
//        }
//
//        // processesArray used for storing the registries of the objects
//        ProcessInterface[] processesArray = new ProcessInterface[numberOfRegistries];
//        System.out.println("Number of registries: " + numberOfRegistries);
//
//        // Looking up the registries of all objects
//        for(int i = 0; i < numberOfRegistries; i++) {
//            processesArray[i] = (ProcessInterface) registry.lookup(registry.list()[i]);
//            System.out.println("processesArray[i] = " + processesArray[i]);
//        }
//
//        // Setting the list of all processes for every process,
//        // so that every process knows the 'address' of every process is
//        for(int i = 0; i < numberOfRegistries; i++) {
//            processesArray[i].setListOfAllProcesses(processesArray);
//            System.out.println("ProcessInterface[] = " + processesArray[i].getListOfAllProcesses());
//            for(int j = 0; j < processesArray[i].getListOfAllProcesses().length; j++) {
//                System.out.println("processesArray[i].getListOfAllProcesses()[j] = " + processesArray[i].getListOfAllProcesses()[j]);
//            }
//        }
//    }

    public static void start() throws RemoteException, NotBoundException
    {
        done = false;
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);

        System.out.println(registry.list().length);
        int numberOfRegistries = registry.list().length;

        // processesArray used for storing the registries of the objects
        Interface[] processesArray = new Interface[numberOfRegistries];
        System.out.println("Number of registries: " + numberOfRegistries);


        for (int i = 0; i < numberOfRegistries; i++) {
            processesArray[i] = (Interface) registry.lookup(registry.list()[i]);

        }

        for(int j = 0; j < numberOfRegistries; j++) {
            processesArray[j].setListOfAllProcesses(processesArray);
        }

        Main.processesArray = processesArray;
        threads = new Thread[processesArray.length];

        for(int i = 0; i < processesArray.length; i++) {
            Interface process = processesArray[i];

            Thread thread = new Thread(() -> {
                try {
                    process.run();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i] = thread;
            thread.start();
        }

        System.out.println("DONE!");
        done = true;
    }

}