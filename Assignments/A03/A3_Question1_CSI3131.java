/*
Problem Description:
(Refer to instructions Assignment 3 pdf)
- Using POSIX threads, mutex locks, and semaphores, implement a solution that
coordinates the activities of the TA and the students

Note:
- If 'sleep()' or 'wait()' need try catch
- //3 chairs  [static == when create, not blank slate] [default value of boolean array is all FALSE]
Assumptions:
- student sleep() == student working (programming)
- Assumes ALL students will be helped by the TA in the duration
*/

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;


/////////////////-- Setters and Getters -- ////////////////////////////
class ThreadCommunicator{
    
    /// Priority Queue values for chairs ///
    private static Queue<Long> chairs = new PriorityQueue<Long>();
    protected Queue<Long> getChairs(){return chairs;} //Includes all built in functions: add, remove, peek, etc. 
    protected void setChairs(Queue<Long> ourQueue){chairs = ourQueue;}   //In order to overwrite and have up to date version
    protected boolean areChairsNotFull(){ return chairs.size() < 3; }

    /// Checking if TA is available ///
    private static boolean isTAFree;
    protected boolean getIsTAFree(){return isTAFree;}
    protected void setIsTAFree(boolean availability){isTAFree = availability;}
       

///////////////-- worker thread () -- ////////////////////////////
class Worker1 extends Thread { // Java Threads created with call to start() method: extend Thread class or use Runnable interface

    //-- (Students)
    public void run()
    {
        try
        {
            System.out.println("Worker (Student) has started programming " + Thread.currentThread().getId());     //Gets thread process number  
            sleep(randomNumber);     


            ThreadCommunicator passingStudent = new ThreadCommunicator();
            Queue<Long> chairInfo = passingStudent.getChairs();

            for (int i = 0; i < 3; i++){
                if (passingStudent.areChairsNotFull())
                {
                    // System.out.println("Student [" + Thread.currentThread().getId() + "] is taking chair: " + (i+1));
                    // System.out.println("Size of LL: " + chairInfo.size());
                    chairInfo.add(currentThread().getId());
                    System.out.println("Size of PQ: " + chairInfo.size());
                    passingStudent.setChairs(chairInfo);
                    break;
                }

            }

            
            passingStudent.sleep(1000);



        }catch(InterruptedException e){
            System.out.println("Error: " + e);
        }
    }
}



//////////////////////////////////////////////////////////////////
public class A3_Question1_CSI3131
{
//-- Main Thread (TA) -- 
    public static void main(String[] args)
    {
        //Declaring Variables
        Random randomNum = new Random();
		int numberOfStudents = randomNum.nextInt(1) +5; //nextInt(bound) + min
        Worker1 runner;

        //Creating Multiple Threads to run in parallel (Students)
        for (int i = 0; i < numberOfStudents; i++){
            runner = new Worker1();
            runner.start();
        }

        System.out.println("Main Thread. (Teacher)");
         // Teacher:
         //- Help students (Thread SLEEPS FOR RANDOM PERIOD of time)
         //- When TA finishes helping student:
            //- TA check if students waiting in hall (Help students in order; else return to napping)

        ThreadCommunicator availabilityTA = new ThreadCommunicator();

            

        // Student threads will alternate between:
        //-  programming for a (RANDOM) period of time (Thread SLEEPS FOR RANDOM PERIOD of time)
        //-  seeking help from the TA  
            //- (If TA available --> will obtain help
            // else --> sit in chair   OR   if no chair resume programming and seek help at later time)
            //- If arrives and see TA sleeping --> notify TA using SEMAPHORE

        
        // try {
        //     runner.join();
        // } catch (InterruptedException ie) {
        // }

        
    }


}

