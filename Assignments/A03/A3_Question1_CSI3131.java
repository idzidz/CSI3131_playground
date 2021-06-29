/*
Problem Description:
(Refer to instructions Assignment 3 pdf)
- Using POSIX threads, mutex locks, and semaphores, implement a solution that
coordinates the activities of the TA and the students

Note:
Workflow
1) Students Program/work('sleep()' their thread) for a random set time
2) After 'sleep()' time up, goes to check on TA for help
3) Get help from TA OR Wait in chair OR back to programming
4) Repeat

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
    private static boolean isTABusy; //deafult value = false
    protected boolean getIsTABusy(){return isTABusy;}
    protected void setIsTABusy(boolean availability){isTABusy = availability;}

    /// Current student with the TA ///
    private static long studentWithTA;
    protected long getStudentWithTA(){return studentWithTA;}
    protected void setStudentWithTA(long currStudentTakingTA){studentWithTA = currStudentTakingTA;}

    /// Current student with the TA ///
    private static long studentInChairOne;
    protected long getStudentInChairOne(){return studentInChairOne;}
    protected void setStudentInChairOne(long currStudentTakingTA){studentInChairOne = currStudentTakingTA;}

          /// Current student with the TA ///
    private static long StudentInChairTwo;
    protected long getStudentInChairTwo(){return StudentInChairTwo;}
    protected void setStudentInChairTwo(long currStudentTakingTA){StudentInChairTwo = currStudentTakingTA;}
}


///////////////-- worker thread () -- ////////////////////////////
class Worker1 extends Thread { // Java Threads created with call to start() method: extend Thread class or use Runnable interface

    //-- (Students)
    public void run()
    {
        try
        {
            //Students working/programming for random periods of time (Time until they check for TA for help)
            long currentStudent = Thread.currentThread().getId();   //Gets thread process number 
            //System.out.println("TA is sleeping"); // Move to main?
            System.out.println("Student " + currentStudent + " is programming for " + (randomIntGenerator()/100)  + " seconds");
            sleep(randomIntGenerator());    
            
            boolean amINext = false;

            ThreadCommunicator passingStudent = new ThreadCommunicator();

            while(true){
                //Student checking for TA availability
                if (!passingStudent.getIsTABusy()) { //If TA is NOT busy
                    //Gets help from TA for random amount of time
                    System.out.println("Student " + currentStudent + " is waking up the TA");
                    int taHelping = randomIntGenerator();
                    passingStudent.setIsTABusy(true);
                    System.out.println("Student " + currentStudent + " is currently getting help from the TA for " + taHelping + " seconds");
                    sleep(taHelping);
                    passingStudent.setIsTABusy(false);
                    break;
                }
                else if(passingStudent.areChairsNotFull()) {   //Check if chairs NOT full
                    Queue<Long> chairInfo = passingStudent.getChairs();
                    chairInfo.add(currentStudent);
                    System.out.println("Student " + currentStudent + " is taking chair " + chairInfo.size());
                    passingStudent.setChairs(chairInfo);

                    if (chairInfo.size() == 0){
                        passingStudent.setStudentInChairOne(currentStudent);
                    }else if (chairInfo.size() == 1){
                        passingStudent.setStudentInChairTwo(currentStudent);
                    }
                    
                    if (currentStudent == passingStudent.getChairs().peek()){
                        join(passingStudent.getStudentWithTA());                //put pid of studuent currently helping TA (Waits until that PID terminates, then joins up next)
                    } else if (currentStudent == passingStudent.getStudentInChairOne()){
                        join(passingStudent.getStudentInChairOne());
                    } else if (currentStudent == passingStudent.getStudentInChairTwo()){
                        join(passingStudent.getStudentInChairTwo());
                    }            
                }
                else{ //BACK to programming/working ('sleep()' their thread) 
                    if (!amINext){
                        System.out.println("-- Student " + currentStudent + " is going back to programming for " + (randomIntGenerator()/100) + " seconds");
                        sleep(randomIntGenerator());
                    }
                    
                }
            }

            System.out.println("Student [" + currentStudent + "] is done with the TA and left the building");

        }catch(InterruptedException e){
            System.out.println("Error: " + e);
        }
    }


    /////---- HELPER FUNCTION (Random Integer Generator: for random interval time) ----////////
    public int randomIntGenerator(){
        Random generate = new Random();
        int randomNumber = generate.nextInt(5000) + 1000; //nextInt(bound) + min
        return randomNumber;
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
		int numberOfStudents = randomNum.nextInt(1) + 5; //nextInt(bound) + min
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

        ThreadCommunicator TA = new ThreadCommunicator();
        //System.out.println("TA is sleeping");   


            

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