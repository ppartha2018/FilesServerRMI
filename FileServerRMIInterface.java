package FilesServerRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServerRMIInterface extends Remote {
	
	public String uploadFile(String filename, byte[] data, int len) throws RemoteException;
	public byte[] downloadFile(String location) throws RemoteException;
	public String listDir(String path) throws RemoteException;
	public String mkdir(String path) throws RemoteException;
	public String rmdir(String path) throws RemoteException;
	public void shutDown() throws RemoteException;
}
