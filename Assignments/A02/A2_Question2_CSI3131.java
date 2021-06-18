/*
/////////// Assignment 2: QUESTION 2 ///////////////
AUTHOR INFORMATION:
- Marco Cen | 300129532
- Ivan Zeljkovic | 300092334

PROBLEM DESCRIPTION:
Write a multithreaded program that generates the Fibonacci series using either the Java,
Pthreads, or Win32 thread library. This program should work as follows: The user will
enter on the command line the number of Fibonacci numbers that the program is to
generate. The program will then create a separate thread that will generate the Fibonacci
numbers, placing the sequence in data that is shared by the threads (an array is probably
the most convenient data structure).When the thread finishes
execution, the parent thread will output the sequence generated by the child thread.
Because the parent thread cannot begin outputting the Fibonacci sequence until the child
thread finishes, this will require having the parent thread wait for the child thread to
finish.

Fibonacci Sequence: 
The Fibonacci sequence is the series of numbers 0, 1, 1, 2, 3, 5, 8, .... Formally, it can be
expressed as:
fib0 = 0
fib1 = 1
fibn = fibn−1 + fibn−2
*/

import java.util.Scanner;

// Will be using this object to communicate between the Main thread and Worker threads.
class FibonacciCollector{
    private static long[] FibonacciCollectorValues;

    public long[] getFibonacci(){return FibonacciCollectorValues;}
    public void setFibonacci(long[] overrideCollection){FibonacciCollectorValues = overrideCollection;}
}

//-- Class 1 (Worker Thread) --
class Worker1 extends Thread { // Java Threads created with call to start() method: extend Thread class or use Runnable interface

    public void run() {
        /* Get User Input */
        int userInput = userInputStored();

        /* Initial fibonacci values to build off of. Will continue from here until >= required number of numbers. */
        long[] FibonacciCollection = new long[userInput + 1];
        FibonacciCollection[0] = 0;
        FibonacciCollection[1] = 1;

        for (int i=2; i<=userInput; i++){
            FibonacciCollection[i] = FibonacciCollection[i-1] + FibonacciCollection[i-2];
        }

        /* Storing information computed by worker thread into our object */
        FibonacciCollector fibonacciCollector = new FibonacciCollector();
        fibonacciCollector.setFibonacci(FibonacciCollection);
    }


    //-- Helper Function to get User Input --
    public static int userInputStored() {
        Scanner input = new Scanner(System.in); // System.in is a standard input stream
        System.out.print("Enter the number of Fibonacci numbers to be generated: ");
        int userResponseInput = input.nextInt();
        input.close();
        return userResponseInput;
    }
}


//-- Class 2 (Main Thread) --
public class A2_Question2_CSI3131 {

    public static void main(String[] args) {

        //-- Creates Thread --
        Worker1 runner = new Worker1 ();
        runner.start ();
        // System.out.println ("I Am The Main Thread"); 


        //-- Waits until finished -- 
        try {
            runner.join();
        } catch (InterruptedException ie) {
        }
        System.out.println("Worker done. \n");
 
        /* Getting information computed by worker thread */
        FibonacciCollector fibonacciCollector = new FibonacciCollector();
        long[] fibonacciCollection = fibonacciCollector.getFibonacci();

        /* Main process will now print the information computed by the worker process. */
        for (int i=0; i<fibonacciCollection.length-1; i++){
            System.out.print(fibonacciCollection[i] + "\n");
        }
        System.out.println();
        System.out.println("Main thread done.");
    }
}
