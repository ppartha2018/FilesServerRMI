package FilesServerRMI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class FileServerRMI extends UnicastRemoteObject implements FileServerRMIInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//clientId:filename, offset
	Map<String, Integer> partialUploadsHelper = new HashMap<String, Integer>();
	Map<String, Integer> partialDownloadsHelper = new HashMap<String, Integer>();

	public FileServerRMI() throws RemoteException, MalformedURLException {
		Naming.rebind("rmi://localhost/FileServerRMI", this);
		System.out.println("The File Server is ready!");
	}

	@Override
	public String uploadFile(String filename, byte[] data, int len) throws RemoteException {
		System.out.println("Server: uploadFile: Server Locaiton: " + filename);
		try {
			File uploadingFile = new File(filename);
			if(!uploadingFile.exists())
				uploadingFile.getParentFile().mkdirs();
			uploadingFile.createNewFile();
			uploadingFile = new File(filename);
			FileOutputStream out = new FileOutputStream(uploadingFile, true);
			out.write(data, 0, len);
			out.flush();
			out.close();
			System.out.println("Server: Done writing file...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int partialUploadsHelper(String clientId, String filename) {
		try {
			if(partialDownloadsHelper != null) {
				if(partialDownloadsHelper.get(clientId+":"+filename) > 0) {
					return partialDownloadsHelper.get(clientId+":"+filename);
				}
			}
		} 
		catch(Exception e) {
		}
		return 0;
	}

	@Override
	public byte[] downloadFile(String serverpath) throws RemoteException {
		byte[] mydata;
		System.out.println("Server: downloadFile: Server Locaiton: " + serverpath);
		File serverpathfile = new File(serverpath);
		mydata = new byte[(int) serverpathfile.length()];
		FileInputStream in;
		try {
			in = new FileInputStream(serverpathfile);
			in.read(mydata, 0, mydata.length);
			in.close();
			System.out.println("Server: File Sent");
		} catch (Exception e) {

			e.printStackTrace();
		}

		return mydata;
	}
	
	public int partialDownloadsHelper(String clientId, String filename) {
		try {
			if(partialDownloadsHelper != null) {
				if(partialUploadsHelper.get(clientId+":"+filename) > 0) {
					return partialUploadsHelper.get(clientId+":"+filename);
				}
			}
		} 
		catch(Exception e) {
		}
		return 0;
	}


	@Override
	public String listDir(String path) throws RemoteException {
		System.out.println("Server: processing listdir request.");
		File serverpathdir = new File(path);
		StringBuilder sbr = new StringBuilder();
		if(serverpathdir.list().length > 0) {
			for(String s : serverpathdir.list()) {
				sbr.append(s);
				sbr.append("\t");
			}
		}
		return sbr.toString();
	}

	@Override
	public String mkdir(String path) throws RemoteException {
		System.out.println("Server: processing mkdir request.");
		File serverpathdir = new File(path);
		 
		if(serverpathdir.mkdir())
			return "Directory "+path+" created in server.";
		else
			return "ERR501: Given directory '"+path+"' already exist in server.";
			
	}

	@Override
	public String rmdir(String path) throws RemoteException {
		System.out.println("Server: processing rmdir request.");
		File serverpathdir = new File(path);
		if(serverpathdir.delete())
			return "Directory/File '"+path+"' deleted in server.";
		else
			return "ERR502: Given directory/file does not exist in server.";
	}

	@Override
	public void shutDown() throws RemoteException {
		try {
			Naming.unbind("rmi://localhost/FileServerRMI");
		} catch (MalformedURLException | NotBoundException e) {
			//e.printStackTrace();
		}
		System.out.println("Server: Bye!");
		System.exit(1);
	}

	public static void main(String[] args) {
		try {
			FileServerRMI fr = new FileServerRMI();
			System.out.println("The File Server is ready!");
		} catch (Exception e) {
			System.out.println("File Server Failed.");
			e.printStackTrace();
		}
	}

}
