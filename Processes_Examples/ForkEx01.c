#include <stdio.h>
static int aa = 10;

int main()
{
    int a = 4;
    int p;

    printf("First print: I am process %d a = %d \n", getpid(), a);
    p = fork();

    //P will be 0 for the child here. It has nothing assigned to it; guess C doesn't have nulls?
    if (p == 0)
    {
        aa++;
        a++;
    }

    //This will print that aa = 11 and a = 5 because we have one child with an empty PID variable.
    //!!! NOTE, however, that only one line will be like that. The parent line will have a second print of aa = 10 and a = 4.

    //Child will have a PID at this point because it is essentially a running parent.
    //We are not assigning a non-existing value to it is how I understand it
    printf("Second print: I am process: aa = %d, a = %d, pid = %d \n", aa, a, getpid());

    fork();

    //The variable p will not have changed in the children.
    //We have one parent with p != 0 and one new parent/child with p == 0
    //Now we create two more forks from each.
    //Therefore we will print aa = 10 a = 4 twice and aa = 11 and a = 5 twice. (Four total prints)
    printf("Third print: I am process: aa = %d, a = %d, pid = %d \n", aa, a, getpid());
}