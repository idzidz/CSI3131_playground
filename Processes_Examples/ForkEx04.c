#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>

int value = 5;

int main()
{
    pid_t pid;
    int a = 2;
    int b = 4;

    pid = fork();

    if (pid == 0)
    {
        ++value;
        a = 3;
        printf("PID = %d, Child a+b = %d, value = %d, getpid = %d \n", pid, a+b, value, getpid());
        pause();
    }else if (pid > 0)
    {
        b = 1;
        wait(0);
        printf("PID = %d, Parent a+b = %d, value = %d, getpid = %d \n", pid, a+b, value, getpid());
        printf("Parent PID is %d \n", getpid());
        wait(0);
        return 0;
    }
}