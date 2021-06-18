/*
/////////// Assignment 2: QUESTION 1 ///////////////
AUTHOR INFORMATION:
- Marco Cen | 300129532
- Ivan Zeljkovic | 300092334

PROBLEM DESCRIPTION:
Write a multithreaded Java, Pthreads, or Win32 program that outputs prime numbers.
This program should work as follows: The user will run the program and will enter a
number on the command line. The program will then create a separate thread that outputs
all the prime numbers less than or equal to the number entered by the user.

Prime Numbers Definition: 
= whole number that is only divisible by itself and one
- Except for 0 and 1
- Special Cases: 2 is prime number
*/

import java.util.Scanner;

//-- Class 1 --
class JoinableWorker implements Runnable { // Java Threads created with call to start() method: extend Thread class or
                                           // use Runnable interface
    public void run() {        
        // Calls Helper function to get user input
        int userInput = userInputStored();

        // Decremenets from user input and check all numbers up to 0
        for (int i = userInput; i > 0; i--) {
            boolean isPrime = true;

            /* Checking every value above 2, up to our current number being evaluated. If our current number is divisible by any of these, then it is not a prime number. Using a boolean flag to keep track. */
            int currentCount = 2;
            while (currentCount<userInput){
                if (userInput % currentCount == 0){
                    isPrime = false;
                }
                currentCount++;
            }

            /* Assuming we have gone through our while loop and our current number is only divisible by itself (and is not 1), then it is a prime number.*/
            if (isPrime && userInput > 1){
                System.out.println(userInput);
            }

            /* Decrement user input to check the next number in our collection. */
            userInput = userInput -1; 

        }
        System.out.println("This is the END of the separate WORKER thread that outputs all the prime numbers <= the number entered by the user");
    }

    public static int userInputStored() {
        // -- Get user input --
        Scanner input = new Scanner(System.in); // System.in is a standard input stream
        System.out.print("Enter a number: ");
        int userResponseInput = input.nextInt();
        input.close();
        return userResponseInput;
    }
}


//-- Class 2 --
public class A2_Question1_CSI3131 {
    public static void main(String[] args) {
        
        //-- Start and creation of threads --
        Thread task = new Thread(new JoinableWorker());
        task.start(); //Automatically runs JoinableWorker class

        //-- Wait for thread to finish --
        try {
            task.join();
        } catch (InterruptedException ie) {
        }
        System.out.println("The Main Thread has finished");
    }
}