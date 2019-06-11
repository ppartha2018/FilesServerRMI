package FilesServerRMI;

/**
 * @author Prasanna Parthasarathy
 *
 */
public class Main {
	
	public static void main(String args[]) throws Exception {
		if(args.length < 1) {
			System.out.println("Insufficient command arguments.");
			return;
		}
		if(args[0] != null && !args[0].isEmpty()) {
			if(args[0].equalsIgnoreCase("server")) {
				try {
					new FileServerRMI();
					System.out.println("File Server started!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(args[0].equalsIgnoreCase("client")) {
				new FileClientRMI(args);
			} else {
				System.out.println("Invalid arguments.");
			}
		}
	}

}
