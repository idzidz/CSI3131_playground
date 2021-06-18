/* ------------------------------------------------ ------------
File: cpr.c

Last name: Zeljkovic  | Cen
Student number: 300092334  | 300129532

Description: This program contains the code for creation
 of a child process and attach a pipe to it.
	 The child will send messages through the pipe
	 which will then be sent to standard output.

Explanation of the zombie process
Explaination of zombie process observation:
	As seen while executing this function, a zombie process is present.
	This is known due to the fact that after each parent process, 
	it waits until their child process ends. This is confirmed from using
	and printing each process's PID id and viewing the status of each process via 
	the command 'ps -u <username>': labelled as <defunct>. Defunct processes or
	other known as zombie processes is a process that is still running and using
	resources even though it has executed all of its code. 

------------------------------------------------------------- */
#include <stdio.h>
#include <sys/select.h>

/* Prototype */
void createChildAndRead (int);

/* -------------------------------------------------------------
Function: main
Arguments: 
	int ac	- number of command arguments
	char **av - array of pointers to command arguments
Description:
	Extract the number of processes to be created from the
	Command line. If an error occurs, the process ends.
	Call createChildAndRead to create a child, and read
	the child's data.
-------------------------------------------------- ----------- */

int main (int ac, char **av)
{
 int processNumber; 

 if (ac == 2)
 {
 if (sscanf (av [1], "%d", &processNumber)== 1)
 {
    createChildAndRead(processNumber);
 }
    else fprintf(stderr, "Cannot translate argument\n");
 }
    else fprintf(stderr, "Invalid arguments\n");
    return (0);
}


/* -------------------------------------------------------------
Function: createChildAndRead
Arguments: 
	int prcNum - the process number
Description:
	Create the child, passing prcNum-1 to it. Use prcNum
	as the identifier of this process. Also, read the
	messages from the reading end of the pipe and sends it to 
	the standard output (df 1). Finish when no data can
	be read from the pipe.
------------------------------------------------------------- */

/*======= Template Ends here ======*/

void createChildAndRead(int prcNum)
{
	//-- Declare Variables --
	int fd[2]; //Allocating space in pipe (fd[0] == read end of pipe | fd[1] == write end of pipe)
	int PID; //Declaring process number as int type
	char* tmp[64]; //Had to specify char*, which is explicitly stating a sequence of chars. Just putting char caused character issues. 
	char* inputBuffer[1024]; // Specifying the memory allocated for the the inputBuffer, which is where we stored what is being read from the pipe.


	if (pipe(fd) < 0) //Create pipe AND Checking for Pipe Error
	{
		fprintf(stderr, "Pipe error\n");
		return -1;
	}

	if (prcNum == 1) //When argument is 1, will not create a child process
	{
		printf("Process %d starting \n", prcNum); 
		sleep(5); //The wait period between starting and terminating the last process
		printf("Process %d terminating \n", prcNum);
	}
	else if (prcNum != 1) //If process number is not 1 (the final process), continue to fork and create children
	{
		PID = fork();
	}

	if (PID < 0)  //Checking for Forking error
	{
		fprintf(stderr, "Fork error\n");
		return -1;
	}

	if (PID == 0) //-- CHILD Process --
	{
		close(fd[0]); //Close reading end of pipe, child will be doing purely writing

		//Converting dynamic text containing process number to a string
		sprintf(tmp, "Process %d starting \n", prcNum);  //To help form zombie process explaination: (From PID: %d)
		write(fd[1], tmp, sizeof(inputBuffer)); //Writes into the writing end of the pipe
		
		/** Notes for 'dup2(x,y)' usage:
		- Using multiple descriptors so that we do not overwrite before being read
		- With dup2, we are attaching the writing end of the pipe to our prcNum descriptor
		- Needed to find unused file descriptors in memory to work accordingly in order, therefore increased descriptor number of second argument
		**/
		dup2(fd[1], (prcNum+10));  //changes where write goes to (Where in memory you want to write to: which descriptor you want to use)

		close(fd[1]);

		//-- Declaration of "execvp" arguments --
		sprintf(inputBuffer, "%d", prcNum-1); //Convert integer to string type
		char* args[3];
		args[0] = (char*)"./a.out"; //Name of file created (Executable)
		args[1] = inputBuffer;
		args[2] = NULL;


		/** Notes for 'execvp(x,y)' usage:
		- Duplicates program with decremented argument input from line 122 
		- Similar to recursion, recursively calls program with arguements given (FirstParent --> FirstChild (SecondParent) --> SecondChild(ThirdParent) --> Third...., then Ends and traverses back up to FirstParent)
		**/
		if(execvp(args[0], args) < 0) //Also checks for execvp() error
		{
			fprintf(stderr, "Execvp error\n");
		}
	}
	else //-- PARENT Process --
	{
		close(fd[1]); //Close writing end of pipe
		
		//Reading and printing the contents from the reading end of the pipe
		while(read(fd[0], inputBuffer, sizeof(inputBuffer)) > 0)
		{
			printf("%s", inputBuffer);
		}

		sleep(10); //Delay set to observe zombie processes: after EACH process termination		
		printf("Process %d terminating \n", prcNum); //To help form zombie process explaination: (From PID: %d)
		close(fd[0]);
	}
	return 0;
}



/* General Workflow of this program function:
1. Input: cc -o <name of executable file to create> <this file name>.c   OR cc <this file name>.c
		: ./<name of executable file to create> <Argument this function takes: int #>
Example: 
	cc cpr.c
	./a.out 3

1. Line 93 --> LIne 95
2. (FirstParent Process)
	FirstChild	
	- Line 137, up to 142 (Communicates with child via pipe from read end)
	- Line 104, up to 133 (Duplicates program with decremented argument input from line 122)
	- Line 148

2. (SecondParent Process)
"(same as above but diff input/arguemnt provided)"

*/