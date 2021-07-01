/*
Problem Description:
(Refer to instructions Assignment 3 pdf)
- Using POSIX threads, mutex locks, and semaphores, implement a solution that
coordinates the activities of the TA and the students

Assumptions:
- student sleep() == student working (programming)
- All students will START programming first (random 'sleep()' interval)
- ALL students must be helped by the TA before leaving/exiting (terminating their thread)

Workflow
1) Students Program/work('sleep()' their thread) for a random set time
2) After 'sleep()' time up, goes to check on chair availability 
    - if (all 3 slots empty) == TA is free, student goes straight to TA for help
    - else if (at least 1 occupied and at least 1 spot free) == student sits/waits in chair (wait() their thread)
        - Gets help from TA (Using Queue/semaphore)
    - else if (no spots free) == student goes back to programming for random period of time ('sleep()' their thread for random time)
4) Repeat

General Notes:
- Chair queue includes student with TA: 0 (Student with TA), 1 (Next up), 2 (After 1), 3 (After 2)
- If 'sleep()' or 'wait()' need try catch
*/


import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Scanner;


///////-- Setters and Getters -- ///////
class ThreadCommunicator {

    /// Priority Queue values for chairs ///
    private static LinkedList<Long> chairs = new LinkedList<Long>();
    protected LinkedList<Long> getChairs() { return chairs; } // Includes all built in functions: add, remove, peek, etc.
    protected void setChairs(LinkedList<Long> ourQueue) { chairs = ourQueue; } // In order to overwrite and have up to date version
    protected int chairSlotsTaken() { return chairs.size(); } // # of chair slots currently occupied

    /// TA availability
    private static boolean isTaBusy; // deafult value = false
    protected boolean getIsTaBusy() { return isTaBusy; }
    protected void setIsTaBusy(boolean availability) { isTaBusy = availability; }

    /// Semaphore value
    private static int semaphore = 1;
    protected int getSemaphore() { return semaphore; }
}


///////-- Ta Worker Thread -- ///////
class TaWorkerThread extends Thread 
{
    public void run()
    {
        ThreadCommunicator threadCommunicator = new ThreadCommunicator();
        while(true)
        {
            if (!threadCommunicator.getIsTaBusy() && threadCommunicator.getChairs().size() == 0)
            {
                System.out.println("TA is going to sleep");
            }
        }
    }
}



///////-- Worker Thread (Students POV) --///////
class Worker1 extends Thread { // Java Threads created with call to start() method: extend Thread class or use Runnable interface

    public void run() {
        // -- Student is programming OR looking for/waiting in a chair --
        try {
            long currentStudent = Thread.currentThread().getId(); // Gets current student's thread process #

            while (true) {  //(Like the if else) ALL CHAIRS TAKEN SO FULL; Students go back to programming: 'sleep()' their thread

                // -- Students programming ('sleep()' their thread) for a set random period of time --
                int randomNumber = randomIntGenerator();
                System.out.println("Student [" + currentStudent + "] is PROGRAMMING for " + (randomNumber / 1000) + " seconds");
                sleep(randomNumber);

                // Declare variables for objects to keep track (Chair & TA status, semaphore)
                ThreadCommunicator studentStatus = new ThreadCommunicator();
                LinkedList<Long> chairInfo = new LinkedList<Long>();
                Semaphore sem = new Semaphore(1);

                // There is spot available in hallways with chairs [x, x, x, x] Student with TA is in pos[0], pos[1] is chair 1, pos[2] is chair 2, pos[3] is chair 3
                if (studentStatus.chairSlotsTaken() <= 3) {

                    // ALL chair spots are free
                    if (studentStatus.chairSlotsTaken() == 0) { //Needed to split this if statement up with below or else doesnt work accordingly for some reason
                        
                        // TA is free and student goes straight to TA
                        if (!studentStatus.getIsTaBusy()) { 
                            // System.out.println("[" + currentStudent + "] is about to interrupt"); //TEST
                            System.out.println("Student [" + currentStudent + "] is waking up the TA");
                            chairInfo = studentStatus.getChairs();
                            chairInfo.addLast(currentStudent); // Add student's PID # into LinkedList
                            studentStatus.setChairs(chairInfo);
                            throw new InterruptedException(); // Goes to Catch Block (Interrupts the thread) [can put msg in parameter]
                        }

                    }
                    // System.out.println("Student [" + currentStudent + "] is currently waiting for a permit"); //TEST

                    // -- Student takes available chair <This needs to be an atomic instruction> --
                    sem.acquire();
                    // System.out.println("Student [" + currentStudent + "] gets a permit"); //TEST
                    System.out.println("Student [" + currentStudent + "] is WAITING in chair " + (studentStatus.chairSlotsTaken()));
                    chairInfo = studentStatus.getChairs();
                    chairInfo.addLast(currentStudent); // Add student's PID # into LinkedList
                    studentStatus.setChairs(chairInfo);
                    // System.out.println("Student [" + currentStudent + "] sees PQ of: " + chairInfo); //TEST
                    // System.out.println("Student [" + currentStudent + "] is releasing permit"); //TEST
                    sem.release();
                    // -- <End of atomic instruction> --

                    while (true) {
                        sem.acquire();
                        chairInfo = studentStatus.getChairs();
                        if (chairInfo.peek() == currentStudent) {
                            throw new InterruptedException();
                        }
                        sem.release();
                        sleep(1);
                    }

                }

                System.out.println("TA is busy. Student [" + currentStudent + "] will come back at a later time.");
            }


        //-- TA helping with selected student --
        } catch (InterruptedException e) { // InterruptedException == interrupt thread, throw exception
            // System.out.println("ErroR: " + e); //TEST

            //Declare variables again
            long currentStudent = Thread.currentThread().getId(); 
            ThreadCommunicator taStatus = new ThreadCommunicator();
            LinkedList<Long> chairInfo = taStatus.getChairs();
            int randomNumber = randomIntGeneratorTA();

            taStatus.setIsTaBusy(true);
            System.out.println("Student [" + currentStudent + "] is with the TA for " + (randomNumber / 1000) + " seconds.");

            try { sleep(randomNumber); } 
            catch (InterruptedException f) {}

            taStatus.setIsTaBusy(false);
            System.out.println("-- Student [" + currentStudent + "] is finished with the TA and has left the building. --");

            //All chair slots are NOT free (Current student with TA remove from Linked List)
            if (chairInfo.size() != 0) {
                chairInfo.remove();
                taStatus.setChairs(chairInfo);
                // System.out.println("In catch: " + chairInfo); //TEST
            }
            
            if (chairInfo.size() == 0 && !taStatus.getIsTaBusy()){
                System.out.println("<<<< TA is done with student and chairs are empty; TA is taking a nap. >>>>");
            }

        }
    }

    /// ---- HELPER FUNCTION (Random Integer Generator: for random interval time) ----///
    public int randomIntGenerator() {
        Random generate = new Random();
        int randomNumber = generate.nextInt(10001) + 5000; // nextInt(bound) + min || ((max - min) + 1) + min
        return randomNumber;
    }
    public int randomIntGeneratorTA() {
        Random generate = new Random();
        int randomNumber = generate.nextInt(2001) + 1000; // nextInt(bound) + min || ((max - min) + 1) + min
        return randomNumber;
    }
}


///////-- Main Thread (TA) --///////
public class A3_Question1_CSI3131 {
    public static void main(String[] args) {

        //-- Get user Input --
        Scanner input = new Scanner(System.in);
        System.out.println("How many students would you like to test with?");
        int userInput = input.nextInt();
        input.close();
        Worker1 runner;
        // Random randomNum = new Random();
        // int numberOfStudents = randomNum.nextInt(1) + 10; // nextInt(bound) + min


        // Creating Multiple Threads to run in parallel (Student threads)
        for (int i = 0; i < userInput; i++) {
            runner = new Worker1();
            runner.start();
        }    
        // System.out.println("I am the Main Thread. (TA)"); //TEST
    }
}









///////////////////////////////////////////////// OLD CODE /////////////////////////////////////////////////

/// Checking if TA is available ///

// Following was in communicator object //
/// Current student with the TA ///

// private static long studentWithTA;
// protected long getStudentWithTA(){return studentWithTA;}
// protected void setStudentWithTA(long currStudentTakingTA){studentWithTA =
// currStudentTakingTA;}

// /// Current student with the TA ///
// private static long studentInChairOne;
// protected long getStudentInChairOne(){return studentInChairOne;}
// protected void setStudentInChairOne(long
// currStudentTakingTA){studentInChairOne = currStudentTakingTA;}

// /// Current student with the TA ///
// private static long StudentInChairTwo;
// protected long getStudentInChairTwo(){return StudentInChairTwo;}
// protected void setStudentInChairTwo(long
// currStudentTakingTA){StudentInChairTwo = currStudentTakingTA;}

// if (chairInfo.size() == 1){
// studentStatus.setStudentInChairOne(currentStudent);
// }else if (chairInfo.size() == 2){
// studentStatus.setStudentInChairTwo(currentStudent);
// }

// Following was on else if statement block of thread

// if (currentStudent == studentStatus.getChairs().peek()){
// join(studentStatus.getStudentWithTA()); //put pid of studuent currently
// helping TA (Waits until that PID terminates, then joins up next)
// // chairInfo.poll();
// // studentStatus.setChairs(chairInfo);
// } else if (currentStudent == studentStatus.getStudentInChairTwo()){
// join(studentStatus.getStudentInChairOne());
// } else{
// join(studentStatus.getStudentInChairTwo());
// }
/// MUTEX

// while(true){
// //Student checking for TA availability
// if (!studentStatus.getIsTaBusy()) { //If TA is NOT busy
// //Gets help from TA for random amount of time
// System.out.println("Student " + currentStudent + " is waking up the TA");
// int taHelping = randomIntGenerator();
// studentStatus.setIsTaBusy(true);
// // studentStatus.setStudentWithTA(currentStudent);
// System.out.println("Student " + currentStudent + " is currently getting help
// from the TA for " + taHelping + " seconds");
// sleep(taHelping);
// studentStatus.setIsTaBusy(false);

// break;

// }
// else if(studentStatus.areChairsNotFull()) { //Check if chairs NOT full
// Queue<Long> chairInfo = studentStatus.getChairs();
// chairInfo.add(currentStudent);
// System.out.println("Student " + currentStudent + " is taking chair " +
// chairInfo.size());
// studentStatus.setChairs(chairInfo);

// }
// else{ //BACK to programming/working ('sleep()' their thread)
// if (!amINext){
// System.out.println("-- Student " + currentStudent + " is going back to
// programming for " + (randomIntGenerator()/100) + " seconds");
// sleep(randomIntGenerator());
// }

// }
// }
// System.out.println("Student [" + currentStudent + "] is done with the TA and
// left the building");


// First student in PQ check if TA available
// while (chairInfo.peek() != currentStudent)
// {
// //System.out.println("Top of LL is " + chairInfo.peek() + " and I am " +
// currentStudent + "\nIs the TA busy: " + studentStatus.getIsTaBusy());
// if (!studentStatus.getIsTaBusy()){
// throw new InterruptedException();
// }
// };
// chairInfo = studentStatus.getChairs();

//OR

// while (chairInfo.peek() != currentStudent){}
// while (studentStatus.getIsTaBusy()){}
// throw new InterruptedException();

// if (!studentStatus.getIsTaBusy()){
// throw new InterruptedException();
// }
