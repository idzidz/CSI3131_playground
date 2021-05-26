#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>

int value = 5;

int main()
{
    int pid = fork();

    if (pid == 0)
    {
        printf("Child PID is %ld \n", (long)getpid());
        execlp("/bin/ls","ls",NULL);
        printf("Child PID is %ld \n", (long)getpid());
        value += 15;
        printf("Child value is %d \n", value);
        execlp("/bin/ls","ls",NULL);
        return 0;
    }else if (pid > 0)
    {
        printf("Parent PID is %d \n" , getpid());
        printf("Child PID is %d \n", pid);
        wait(NULL);
        printf("Parent value is %d \n", value);
    }
}