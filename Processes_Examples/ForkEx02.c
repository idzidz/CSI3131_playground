#include <sys/wait.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>

int main(void)
{
    int pids[2];
    int i;

    //This will loop twice
    for (i = 1; i >= 0; --i)
    {
        //Will fork and set array position 1 as first child PID
        //Will then sleep for 60 seconds

        //Loop 2, will fork once more and in position 0 keep second and third child PIDs
        //Will then sleep for 30 seconds

        //Wrong above. There will not be a second fork since we are exiting this loop.
        pids[i] = fork();
        if (pids[i] == 0)
        {
            printf("Child PID is %ld \n", (long)getpid());
            sleep((i+1)*30);
            exit(0);
        }
    }

    //This will also loop twice
    //At this point, we have 2 parent processes.
    //Hence we will print 4 Parent PIDs.
    for (i = 0; i <= 1; ++i)
    {
        printf("Parent PID is %ld \n", (long)getpid());
        waitpid(pids[i],0,0);
    }
    return 0;
}