#include <stdio.h>

int main()
{
    int a = 4;

    //Here we just have the parent. Expected output is one print.
    printf("First print: I am the process %d a = %d \n", getpid(), a);

    fork();

    //Here we have one child and two parents, expected output is two prints for this
    printf("Second print: I am process %d a = %d \n", getpid(), a);

    fork();

    //Here we have two parents and two children, which produces four prints altogether. 
    printf("Third print: I am process %d a = %d \n", getpid(), a);

    fork();

    //We have four parents, four children, 8 output.
    printf("Fourth print: I am process %d a = %d \n", getpid(), a);



    // Total print counter:
    // Print 1 : 1 (2^0)
    // Print 2 : 2 (2^1)
    // Print 3 : 4 (2^2)
    // Print 4 : 8 (2^3)

    // Sum : 15

}