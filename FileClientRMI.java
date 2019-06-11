package FilesServerRMI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.Naming;

public class FileClientRMI {

	FileServerRMIInterface fr = null;
	public String serverHostname = "localhost";

	public FileClientRMI(String args[]) throws Exception {
		if (System.getProperty("PA2_SERVER") != null) {
			try {
				String[] hostProps = System.getProperty("PA2_SERVER").split(":");
				this.serverHostname = hostProps[0];
			} catch (Exception e) {
				System.out.println("Error finding hostname and port from System Environment variable PA2_SERVER.");
			}
		}
		try {
			String rmiUrl = "rmi://" + serverHostname + "/FileServerRMI";
			fr = (FileServerRMIInterface) Naming.lookup(rmiUrl);
			System.out.println("Connection to server successful!");
			this.processCommands(args);
		} catch (Exception e) {
			System.out.println("Error connecting to Server!");
		}
	}

	public void sendFile(String pathOnClient, String remoteLocation) {
		try {
			//System.out.println("In send file");
			File f1 = new File(pathOnClient);
			FileInputStream in = new FileInputStream(f1);
			byte[] mydata = new byte[1024 * 1024];
			int mylen = in.read(mydata);
			while (mylen > 0) {
				fr.uploadFile(remoteLocation, mydata, mylen);
				mylen = in.read(mydata);
			}
			in.close();
			System.out.println("Upload successful!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveFile(String remoteLocation) {
		try {
			int slashIndex = 0;
			String clientpath = remoteLocation;
			if(remoteLocation.length() > 0) {
				slashIndex = remoteLocation.lastIndexOf('/');
				if(slashIndex == -1)
					slashIndex = remoteLocation.lastIndexOf('\\');
				slashIndex = Math.max(0, slashIndex+1);
				clientpath = remoteLocation.substring(slashIndex, remoteLocation.length());
			}
			byte [] mydata = fr.downloadFile(remoteLocation);
			System.out.println("downloading...");
			File downloadingFile = new File(clientpath);
			if(!downloadingFile.exists())
				downloadingFile.createNewFile();
			downloadingFile = new File(clientpath);
			FileOutputStream out=new FileOutputStream(clientpath);				
    		out.write(mydata);
			out.flush();
	    	out.close();
	    	System.out.println("download successful!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listdir(String path) {
		try {
			System.out.println(fr.listDir(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void mkdir(String path) {
		try {
			System.out.println(fr.mkdir(path));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rmdir(String path) {
		try {
			System.out.println(fr.rmdir(path));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rm(String path) {
		try {
			System.out.println(fr.rmdir(path));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutDown() {
		try {
			fr.shutDown();
			System.out.println("Server Shut down successful!");
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void processCommands(String[] args) {
		String command = args[1];
		try {
			if (command.equalsIgnoreCase("upload")) {
				String pathOnclient = args[2];
				String pathOnServer = args[3];
				sendFile(pathOnclient, pathOnServer);
			} else if (command.equalsIgnoreCase("download")) {
				String pathOnServer = args[2];
				receiveFile(pathOnServer);
			} else if (command.equalsIgnoreCase("dir")) {
				String remotePath = args[2];
				listdir(remotePath);
			} else if (command.equalsIgnoreCase("mkdir")) {
				String remotePath = args[2];
				mkdir(remotePath);
			} else if (command.equalsIgnoreCase("rmdir")) {
				String remotePath = args[2];
				rmdir(remotePath);
			} else if (command.equalsIgnoreCase("rm")) {
				String remotePath = args[2];
				rmdir(remotePath);
			} else if (command.equalsIgnoreCase("shutdown")) {
				shutDown();
				System.out.println("Server shutdown successful!");
			} else {
				System.out.println("Invalid arguments.");
			}
		}
		 catch (ArrayIndexOutOfBoundsException e) {
			 System.out.println("Insufficient arguments.");
		}catch (Exception e) {
			// e.printStackTrace();
		} finally {
		}

	}

	public static void main(String[] args) {
		try {
			String[] cmd = new String[4];
			cmd[0] = "client";
			cmd[1] = "upload";
			cmd[2] = "./server/upload.txt";
			cmd[3] = "./uploads/upload.txt";
			
			FileClientRMI fc = new FileClientRMI(cmd);
			//fc.sendFile("./server/testServer.txt", "./test.txt");
			//fc.receiveFile("./server/testServer.txt");
			//fc.mkdir("newdir");
			//fc.mkdir("newdir2");
			//fc.rmdir("newdir2");
			//fc.listdir(".");
			//fc.shutDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
