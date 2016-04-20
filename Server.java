import java.util.Calendar;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.Semaphore;


/**
 * @author Ram
 *
 */
public class Server extends Thread{ 

	// TODO Auto-generated method stub
	private int server_id;
	private String ipAddress;
	Semaphore mutex;
	public int getServer_id() {
		return server_id;
	}
	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	//constructor
	public Server(int server_id) {
		this.server_id = server_id;
	}
	//constructor
	public Server(int server_id, String ipAddress,Semaphore mutex) {
		this.server_id = server_id;
		this.ipAddress = ipAddress;
		this.mutex = mutex;
	}

	public String toString(){
		return Integer.toString(server_id);
	}

	/**
	 * This method is used to generate an array of random usages for a server.
	 * The array is used for the 2 cpu's each server may contain.
	 */
	public int[] generate_usage(){
		Random r = new Random();
		int randomUsage1 = r.nextInt(100);
		int randomUsage2 = r.nextInt(100);
		int [] usages = {randomUsage1,randomUsage2};
		return usages;
	}

	/**
	 * @param date
	 *This method is used to get the number of minutes
	 */
	public long getMinutes(Date date)
	{
		return (date.getTime()/1000)/(long)60;
	}

	@Override
	public void run() {

		long start_time = getMinutes(new Date());
		Writer writer = null;
		while(true)
		{
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.SECOND, 0);//ignoring seconds and milliseconds from system date
			cal.set(Calendar.MILLISECOND, 0);
			boolean isBreak = false;
			long min = getMinutes(date);
			Date ignoreSeconds = cal.getTime();
			long timestamp = ignoreSeconds.getTime();

			if(min - start_time > 24*60)//verifying to break if the log runs for 24 hours
			{
				break;
			}

			int[] usages = generate_usage();

			try
			{
				mutex.acquire();
				writer = new BufferedWriter(new FileWriter("log.txt", true));
				writer.write(timestamp+" "+ipAddress+" 0 "+usages[0]+"\r\n");
				writer.write(timestamp+" "+ipAddress+" 1 "+usages[1]+"\r\n");
				writer.flush();
			} catch (IOException e) {
				System.out.println("Error writing the log to the file");
			} catch (InterruptedException e) {
				isBreak = true;
				System.out.println("Interrupted Exception");
			}

			finally{
				try {
					writer.close();
				} catch (Exception ex) {/*ignore*/}
				mutex.release();
				try
				{
					Thread.sleep(60000);// To generate log for every minute, sleep for 60000 milli seconds
				}catch (InterruptedException e) {
					System.out.println("Interrupted Exception");
					isBreak = true;
				}
			}
			if(isBreak)
			{
				break;
			}
		}
	}
}


