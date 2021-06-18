#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int main()
{
    char* msg1 = "One\n";
    char* msg2 = "Two\n";
    char* msg3 = "Three\n";
    int fd[2];
    char inputBuffer[1024];
    pipe(fd);
    int looper = 5;

    if (fork() == 0)
    {
        close(fd[0]);
        //write(fd[1], "Child writing test", sizeof(inputBuffer));

        //Ignores the buffer, immediately displayed 
        //dup2(fd[1], 1);

        dup2(fd[1], 1);
        //printf("Testing dup2 pt.1 \n");
        printf("Testing dup2 pt.2\n");
        printf("Testing dup2 pt.3\n");
        printf("%c", '\0');

        close(fd[1]);
    }
    else
    {
        close(fd[1]);
        while(read(fd[0], inputBuffer, sizeof(inputBuffer)) > 0)
        {
            printf("Found:\n%s", inputBuffer);
        }        
        close(fd[0]);
    }    
}