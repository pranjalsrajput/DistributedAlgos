import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainClass {

	public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException {
		List<Integer> linkList;
		try {
			LocateRegistry.createRegistry(ComponentClass.port);

			System.out.println("Created registry");

			// binding the objects in the registry
			for (int i = 0; i < ComponentClass.listOfProcessIds.length; i++) {
				linkList = new ArrayList<>();
				for (int j = 0; j < ComponentClass.listOfProcessIds.length; j++) {
					if (ComponentClass.listOfProcessIds[j] != ComponentClass.listOfProcessIds[i]) {
						linkList.add(ComponentClass.listOfProcessIds[j]);
						// myLinkList.add(ComponentClass.listOfProcessIds[i]);
					}
				}
				// unique key + stub
				Naming.rebind(ComponentClass.repositoryName + ComponentClass.listOfProcessIds[i],
						new ProcessImplementation(new CountDownLatch(0),ComponentClass.listOfProcessIds[i],
								ComponentClass.listOfProcessIds[i], -1, linkList, false, false, new ArrayList<>(),new ArrayList<>(),new HashMap<>()));
			}

			List<Integer> ordinaryProcessList = new ArrayList<>();
			for (int i = 0; i < ComponentClass.listOfProcessIds.length; i++) {
				boolean match=false;
				for (int j = 0; j < ComponentClass.listOfCandidateProcesses.length; j++) {
					if (ComponentClass.listOfProcessIds[i] == ComponentClass.listOfCandidateProcesses[j]) {
						match=true;
						// myLinkList.add(ComponentClass.listOfProcessIds[i]);
					}
				}
				if(!match) {
					ordinaryProcessList.add(ComponentClass.listOfProcessIds[i]);
				}
			}
			
			Naming.rebind(ComponentClass.repositoryName + "roundChecker",new RoundCheckerImplementation(false,0,0,ordinaryProcessList));
			System.err.println("RoundChecker Ready");
			// setRegistryList();
			start();

			System.err.println("All processess ready");

		} catch (Exception e) {

			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();

		}

	}

	public static void start() throws RemoteException, NotBoundException {
		try {
			Registry registry = LocateRegistry.getRegistry("145.94.210.95", ComponentClass.port);

			System.out.println(registry.list().length);
			int numberOfRegistries = registry.list().length-1;

			// processesArray used for storing the registries of the objects
			ProcessInterface[] processesArray = new ProcessInterface[numberOfRegistries];
			System.out.println("Number of registries: " + numberOfRegistries);

			for (int i = 0; i < numberOfRegistries; i++) {
				System.out.println("i = " + i);
				Object o = registry.lookup(registry.list()[i]);

				// Looking up the registries of all objects
				for (int j = 0; j < numberOfRegistries; j++) {
					// processesArray[j] = (ProcessInterface) registry.lookup(registry.list()[j]);
					processesArray[j] = (ProcessInterface) Naming
							.lookup(ComponentClass.repositoryName + ComponentClass.listOfProcessIds[j]);
					// System.out.println("processesArray[j] = " + processesArray[j]);
				}

				// Setting the list of all processes for every process,
				// so that every process knows the 'address' of every process
				for (int j = 0; j < numberOfRegistries; j++) {
					processesArray[j].setListOfAllProcesses(processesArray);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
