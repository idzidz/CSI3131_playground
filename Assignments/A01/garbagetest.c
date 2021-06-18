/* ------------------------------------------------ ------------
File: cpr.c

Last name:
Student number:

Description: This program contains the code for creation
 of a child process and attach a pipe to it.
	 The child will send messages through the pipe
	 which will then be sent to standard output.

Explanation of the zombie process
(point 5 of "To be completed" in the assignment):

	(please complete this part);

------------------------------------------------------------- */
#include <stdio.h>
#include <sys/select.h>
//#include <stdlib.h>
//#include <sys/wait.h>
#include <unistd.h>

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


/* ------------------------------------------------ -------------
Function: createChildAndRead
Arguments: 
	int prcNum - the process number
Description:
	Create the child, passing prcNum-1 to it. Use prcNum
	as the identifier of this process. Also, read the
	messages from the reading end of the pipe and sends it to 
	the standard output (df 1). Finish when no data can
	be read from the pipe.
-------------------------------------------------- ----------- */

// close(p[1]);

void createChildAndRead(int prcNum)
{
	//-- Declare Variables -- 
	int cbytes;
	int p[2];
	char* inputBuffer[1024];
	char* args[3];

	if (pipe(p) < 0)
	{
		printf("Pipe error");
		return 1;
	};

	if (fork() > 0)
	{
		close(p[0]);
		dup2(p[1], 1);

		//Second print is bugging out
		printf("Process %d is starting\n", prcNum);

		// write(p[1], "Testing P \n", sizeof(inputBuffer));
		close(p[1]);
	}else
	{		
		close(p[1]);
		char* bytesbro[4];
		//Converting Nth int parameter to a string, execvp was not working when giving an int parameter, requires string.
		sprintf(bytesbro, "%d", prcNum-1);

		//index 0 = working directory, index 1 = input parameter when running application, index 2 = end of input parameter notify
		args[0] = (char*)"./yo";
		args[1] = bytesbro;
		args[2] = NULL;

		//If we reach the 0th child, terminate
		if((prcNum - 1) == 0)
		{	
			
			while(read(p[0], inputBuffer, sizeof(inputBuffer)) > 0)
			{
				printf("yo1: %s", inputBuffer);
			}
			
			return 2;
		}else
		{
			while(read(p[0], inputBuffer, sizeof(inputBuffer)) > 0)
			{
				printf("yo2: %s", inputBuffer);
			}
			if(execvp(args[0], args) == -1)
			{
				printf("Execvp error has occured \n");
			}
		}
		close(p[0]);
	}
	return 0;
}

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// for(int i = 0; i<prcNum; i++)
	// {
	// 	fprintf(stderr, "Process %d begins \n", prcNum-i);
	// 	PID = fork();
	// 	sleep(sleeper);
	// 	if (PID > 0)
	// 	{
	// 		waitpid(PID, 0, 0);
	// 		fprintf(stderr, "Process %d ends \n", prcNum-i);
	// 		sleep(sleeper);
	// 		exit(0);
	// 	}
	// }	