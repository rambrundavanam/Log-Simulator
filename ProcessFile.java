import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * @author Ram
 *
 */
public class ProcessFile{

	/**
	 * Searches for the last occurrence of a timestamp in the sorted arraylist using binary search
	 * @param row
	 * @param size
	 * @param to
	 * @return result
	 */
	public static int binarySearchLastIndex(List<Row> row, int size, long to){

		int low = 0;
		int result = 0;
		int high = row.size()-1;
		while(high >= low){
			int middle = (low + high)/2;
			if(row.get(middle).timeStamp == to){
				result = middle;
				low = middle+1;
			}
			if(row.get(middle).timeStamp < to){
				low = middle+1;
			}

			if(row.get(middle).timeStamp > to){
				high = middle-1;
			}
		}
		return result;
	}

	/**
	 * Searches for the first occurrence of a timestamp in the sorted arraylist using binary search
	 * @param row
	 * @param size
	 * @param from
	 * @return result
	 */
	public static int binarySearchFirstIndex(List<Row> row, int size, long from){

		int low = 0;
		int result = 0;
		int high = row.size()-1;
		while(high >= low){
			int middle = (low + high)/2;
			if(row.get(middle).timeStamp == from){
				result = middle;
				high = middle-1;
			}
			if(row.get(middle).timeStamp < from){
				low = middle+1;
			}

			if(row.get(middle).timeStamp > from){
				high = middle-1;
			}
		}
		return result;
	}

	/**
	 * Generates a linux timestamp based on a date
	 * @param dateString
	 * @return timestamp
	 */
	public static long get_timestamp(String dateString){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		long timestamp = 0;
		try{
			Date date = formatter.parse(dateString);
			formatter.format(date);
			timestamp = date.getTime();
		}catch(ParseException e) {
			//ignore, handling in main
		}
		return timestamp;
	}

	/**
	 *Used as a helper function to return error messages and help data 
	 * @param value
	 */
	public static void printusage(int value){
		String format = "%-40s%s%n";
		System.out.printf(format,"********************************","");
		System.out.printf(format,"Important Message","");
		System.out.printf(format,"********************************","");
		if(value == 1)System.out.printf(format,"Please enter a valid IP address. Example: 192.168.a.b  (a,b less than 255).","");
		else if(value == 2)System.out.printf(format,"Please enter 0 or 1 for CPUID","");
		else if(value == 3)System.out.printf(format,"Endtime cannot be before Starttime","");
		else if(value == 4){
			System.out.printf(format,"Please read the following before using the query tool","");
			System.out.printf(format,"1.Type QUERY IP CPUID STARTTIME ENDTIME","-To start querying","");
			System.out.printf(format,"2.Type EXIT","-To exit the Query tool","");
			System.out.printf(format,"3.Please query in this format:","QUERY IP CPUID STARTTIME ENDTIME");
			System.out.print(">");
		}
		else if(value==5)System.out.printf(format,"Safely exiting the Query tool","");
		else if(value==6)System.out.printf(format,"The Start Time or End Time do not exist in the log.","");
		else if(value==7)System.out.printf(format,"Not a valid file","");
		else if(value==8)System.out.printf(format,"Not a valid path","");
	}

	/**
	 * @param arr
	 * @param parsed_ip
	 * @return valid_ip
	 */
	public static String validate_IP(String [] arr,String parsed_ip){

		//validate the input ip address
		String valid_ip = null;
		try{
			if(arr.length == 4){
				int p1 = Integer.parseInt(arr[0]);
				int p2 = Integer.parseInt(arr[1]);
				int p3 = Integer.parseInt(arr[2]);
				int p4 = Integer.parseInt(arr[3]);

				if(p1 != 192 || p2 != 168 || p3 > 255 || p4 > 255){
					printusage(1);
				}else{
					valid_ip = parsed_ip;
				}
			}else if(arr.length !=4){
				printusage(1);
			}
		}catch(NumberFormatException e){
			printusage(1);
		}
		return valid_ip;
	}

	/** 
	 * A sorted list of all the log data
	 */
	static List<Row> sorted_lines = new ArrayList<Row>();

	/**
	 * @param args
	 */
	public static void main(String args[]){

		boolean is_logSorted = false;
		boolean is_validQuery = true;
		Date logEndDate;
		Date logStartDate;
		long logStartTime = 0;
		long logEndTime = 0;
		
		Scanner args_scanner = new Scanner(System.in);//this scanner scans the query arguments
		String input = args_scanner.nextLine(); 
		//if the first command is exit, exit the tool
		if(input.equals("EXIT")){
			args_scanner.close();
			printusage(5);
			return;
		}

		//if there is no file in the path, alert the user 
		if(args.length==0){
			printusage(8);
		}

		else 
			while(true){

				String [] arGs = input.split(" ",7);

				//if the input at anytime during querying is exit, exit the tool
				if(input.equals("EXIT")){
					printusage(5);
					args_scanner.close();
					return;
				}
				
				/*if the query arguments are missing, 
				 * if there are not exactly the required number of arguments,
				 * making sure that the QUERY command is entered properly
				 * alert the user and continue*/
				if(arGs.length != 7){
					printusage(4);
					is_validQuery = false;
				}
					
				else if(!arGs[0].equals("QUERY")){
						printusage(4);
						is_validQuery = false; 
					}
				else{
					is_validQuery = true;
				}
				
				try {

					File file = new File(args[0]);

					//stop searching if the file is empty and the user has to provide new path
					if(file.length()==0){
						printusage(7);
						args_scanner.close();
						break;
					}
					Scanner scanner = new Scanner(file);

					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						try{
							@SuppressWarnings("unused")
							long timeStamp = Long.parseLong(line.split(" ")[0]);//just to check that the data in the file is not corrupt or invalid
						}catch(NumberFormatException e){
							System.out.println("Invalid data in the file, please choose a valid file");
							scanner.close();
							return;
						}
						sorted_lines.add(new Row(line));
					}
					scanner.close();
					
					//sort the log if it is not already sorted based on the timestamps
					if(is_logSorted == false){
						Collections.sort(sorted_lines, new Row());
						is_logSorted = true;
					}
					//get the log starttime and timewhen log was last written
					logEndDate = new Date(sorted_lines.get(sorted_lines.size()-1).timeStamp);
					logEndTime = logEndDate.getTime();
					logStartDate = new Date(sorted_lines.get(0).timeStamp);
					logStartTime = logStartDate.getTime();
				}catch (FileNotFoundException e) {
					System.out.println("Please Generate a log file with name log.txt");
					args_scanner.close();
					break;
				}

				String parsed_ip = null;
				String valid_ip = null;
				int parsed_cpuid = 0;
				String parsed_startTime = null;
				String parsed_endTime = null;
				
				//if the number of arguments is valid, process the arguments
				if(is_validQuery == true){
					parsed_ip = arGs[1];
					parsed_startTime = arGs[3]+" "+arGs[4]+":00";//combine the day and time form the command line
					parsed_endTime = arGs[5]+" "+arGs[6]+":00";		

					long timestampfrom = get_timestamp(parsed_startTime);
					long timestampto = get_timestamp(parsed_endTime);
					
					//if the starttime is greater than endtime on commandline
					if(timestampfrom > timestampto){
						printusage(3);
					}
					
					//validate the input ip address from the query
					String [] arr = new String[4];
					arr = parsed_ip.split("\\.",4);
					try{
						String ip = validate_IP(arr,parsed_ip);
						if(ip.equals(null)){
							break;
						}else if(!ip.equals(null)){
							valid_ip = ip;
						}
					}catch(NullPointerException e){
						//ignore
					}
					
					//validate cpuid to 0 or 1
					try{
						parsed_cpuid = Integer.parseInt(arGs[2]);
						if(parsed_cpuid > 1 || parsed_cpuid < 0){
							printusage(2);
						}
					}catch(NumberFormatException e){
						printusage(2);
					}
					
					//check if the start time or endtime do not exist in the log
					if(timestampfrom < logStartTime || timestampto > logEndTime){
						printusage(6);
						System.out.println("Log file exists from: ["+logStartDate+"] to ["+logEndDate+"]\r\n");
					}

					int startIndex = 0;
					int endIndex = 0;
					//find the indexes of the starttime and endtime in the sorted list
					startIndex = binarySearchFirstIndex(sorted_lines, sorted_lines.size()-1, timestampfrom);
					endIndex = binarySearchLastIndex(sorted_lines, sorted_lines.size()-1, timestampto);
					int count = 0;
					boolean print_flag = false;
					//if the query conditions meet, print the result
					for(int i = startIndex; i <= endIndex; i++){
						if(sorted_lines.get(i).ip.equals(valid_ip) && sorted_lines.get(i).cpuid == parsed_cpuid){
							Date date = new Date(sorted_lines.get(i).timeStamp);
							if(print_flag == false){
								System.out.print("CPU"+sorted_lines.get(i).cpuid+ " usage on "+sorted_lines.get(i).ip+":\n" );
								print_flag = true;
							}
							System.out.print("\t \t \t \t ("+date+", "+sorted_lines.get(i).usage+"%)\n");
							count++;
						}
					}
					if(count == 0){
						System.out.println("No record exists in the log for the query");
					}
					System.out.print(">");
				}
				if(args_scanner.hasNext()) {
					input = args_scanner.nextLine();//continue scanning inputs
				} else {
					break;
				}
			}	
		args_scanner.close();
	}			
}

class Row implements Comparator<Row>{
	long timeStamp;
	String ip;
	String row;
	int cpuid;
	int usage;
	public Row(String line){
		timeStamp = Long.parseLong(line.split(" ")[0]);
		this.row = line;
		this.ip = line.split(" ")[1];
		this.cpuid = Integer.parseInt(line.split(" ")[2]);
		this.usage = Integer.parseInt(line.split(" ")[3]);
	}

	public Row(){}

	@Override
	public int compare(Row o1, Row o2) {

		if(o1.timeStamp > o2.timeStamp){
			return 1;
		}

		if(o1.timeStamp < o2.timeStamp){
			return -1;
		}

		if(o1.timeStamp == o2.timeStamp){
			return 0;
		}
		return 0;
	}
}
