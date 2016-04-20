/***********************************************************README*****************************************************************/
/*Author Ram Brundavanam
*MS Computer Science
*Indiana University Bloomington
*+1(202)(602)(8940)
*/

Please reading the following before using the tool. 

This simulator is developed in JAVA. I used java 8 SDK to develop it on Eclipse Luna IDE.

It has the following files.

1.Generator.java	- Generates the log from the current system time. I though of generating it for a past date but, since the threads 
			  have to write for every minute, it doesnt make much difference in the actual funcitonality of the tool

2.Server.java 		- This file contains the code that the threads run to write to the file, contains the server class with all the 
			  properties that a server object should have. Each server has 2 CPUs. CPU 0 and CPU 1

3.ProcessFile.java	- This file is the core functional part for Querying the generated log. I used the log file that I generated using 
			  the Generator and did basic testing on that file. The search algorithm used is binary search. Once we generate the
			  log file we stop the generator. Then when we start querying, we sort all the lines in the log using comparator.
			  We sort only once. So the time complexity is O(nlogn) for sorting and O(log n) for the searching.

4.generate.sh 		- Use this file to compile all the java files in the folder and start generating the log

5.process.sh		- Use this file to start querying the log. This file requires an argument for the path of the log. If the argument 
			  doesnt have a valid path, the tool will show an error message and exit.

How to use the tool?

1.Type and press enter

./generate.sh 

This will start writing a log.txt file in the same directory. CTRL+c will stop the generation. Generate log for 30 minutes to check the 
performance of the tool.

2.Type and press enter
 
./process.sh log.txt 

This will take you into the query tool. Start writing the Query.
Example: QUERY 192.168.0.1 0 2016-03-29 13:13 2016-03-29 13:13

This is interpreted as QUERY IP CPUID STATDATE TIME ENDDATE TIME

IP can be anything like 192.168.A.B (A,B < 255) A is practically less than 4 here since we increment A for every 256 incrementations of B like
192.168.0.255, 192.168.1.255. Since we have 1000 servers, we have 4 255 incremenatations of B.

CPUID is either 0,1

Startdate and time is either a time between start time and endtime in the log or any starttime and an end time less than the log end time.
Any other start time or end times will result in errors.

3.Type and Enter
EXIT to come out of the tool. 

The tool might come out automatically for some logical errors like loading invalid log file or giving invalid path as argument for the 
./process.sh ($1).

Thank you for reading.
