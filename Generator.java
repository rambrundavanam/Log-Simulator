import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author Ram Brundavanam
 *
 */
public class Generator {

	static int p1 = 192;
	static int p2 = 168;
	static int p3 = 0;
	static int p4 = 0;
	final static Semaphore mutex = new Semaphore(1);

	/**
	 * @author Ram
	 * @param server_id
	 * This method is used to generate ip addresses ranging from
	 * 192.168.0.0 to 192.68.255.255
	 *
	 */
	public static String generate_ipAddress(){

		if(p4 > 255){
			p3++;
			p4=0;
		}
		String ipAddress = Integer.toString(p1)+"."+Integer.toString(p2)+"."+Integer.toString(p3)+"."+Integer.toString(p4);
		p4++;
		return ipAddress;
	}

	public static void main(String args[]){

		List<Server> servers = new ArrayList<Server>();
		int num_servers = 1000;
		int server_id = 0;
		boolean flag = true;
		for(server_id = 0; server_id < num_servers; server_id++){
			Server s = new Server(server_id,generate_ipAddress(), mutex);
			servers.add(s);
			Thread t = new Thread(s,Integer.toString(server_id));	//Create a new thread to write to the file each time till the server_id is 999
			if(flag){
				System.out.println("Writing to log to log.txt....\n");
				flag = false;
			}
			t.start();
		}
	}
}
