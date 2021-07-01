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
- chair order priority: 1, 2, 3
- If 'sleep()' or 'wait()' need try catch
- 3 chairs  [static == when create, not blank slate] [default value of boolean array is all FALSE]

*/


import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

/////////////////-- Setters and Getters -- ////////////////////////////
class ThreadCommunicator{
    
    /// Priority Queue values for chairs ///
    private static LinkedList<Long> chairs = new LinkedList<Long>();
    protected LinkedList<Long> getChairs(){return chairs;} //Includes all built in functions: add, remove, peek, etc. 
    protected void setChairs(LinkedList<Long> ourQueue){chairs = ourQueue;}  //In order to overwrite and have up to date version
    protected int chairSlotsTaken(){ return chairs.size();} //# of chair slots currently occupied
    

    /// TA availability 
    private static boolean isTaBusy; //deafult value = false
    protected boolean getIsTaBusy(){return isTaBusy;}
    protected void setIsTaBusy(boolean availability){isTaBusy = availability;}

    /// Semaphore value
    private static int semaphore = 1;
    protected int getSemaphore(){return semaphore;}





    // /// -- Helper Functions -- //
    // public void signal(int s){
    //     s++;
    //     if (s <= 0){ //Threads are waiting
    //         //remove a process P from SL;
    //         //wakeup(p) //chosen thread becomes ready

    //     }
    // }

}


///////////////-- worker thread () -- ////////////////////////////
class Worker1 extends Thread { // Java Threads created with call to start() method: extend Thread class or use Runnable interface

    //-- (Students POV) --
    public void run()
    {
        //-- Student is programming OR looking for/waiting in a chair --
        try
        {
            //System.out.println("TA is sleeping"); // Move to main?


            //[Loop START]            
            //-- Students programming ('sleep()' their thread) for a set random period of time (Once time is up, check chair slot availability) --
            long currentStudent = Thread.currentThread().getId();   //Gets thread process number 
            int randomNumber = randomIntGenerator();
            System.out.println("Student [" + currentStudent + "] is PROGRAMMING for " + (randomNumber/100)  + " seconds");
            sleep(randomNumber);    
            ThreadCommunicator studentStatus = new ThreadCommunicator(); 
            LinkedList<Long> chairInfo = new LinkedList<Long>();

            Semaphore sem = new Semaphore(1);  

            //if any chair spot is free == wait() their thread [Includes student with TA in PQ so 4 spots in PQ where 1-3 is chair slo]
            if (studentStatus.chairSlotsTaken() <= 3){
                
                //if ALL 3 chair spots == ta is free and goes straight to TA
                if(studentStatus.chairSlotsTaken() == 0){
                    //System.out.println("Student " + currentStudent + " is waking up the TA");    
                    if (!studentStatus.getIsTaBusy()){
                        System.out.println("[" + currentStudent + "] is about to interrupt");
                        chairInfo = studentStatus.getChairs();
                        chairInfo.addLast(currentStudent); //Add into PQ, student's PID #                
                        studentStatus.setChairs(chairInfo);
                        throw new InterruptedException(); //Goes to Catch Block (Interrupts the thread) [can put msg in parameter]
                    }
                }

                
                //Takes available chair
                
                // System.out.println("Student [" + currentStudent + "] is currently waiting for a permit");  //TEST

                //-- This needs to be an atomic instruction --
                sem.acquire(); 
                // System.out.println("Student [" + currentStudent + "] gets a permit"); //TEST
                System.out.println("Student [" + currentStudent + "] is WAITING in chair " + (studentStatus.chairSlotsTaken()) );
                chairInfo = studentStatus.getChairs();
                chairInfo.addLast(currentStudent); //Add into PQ, student's PID #                
                studentStatus.setChairs(chairInfo);
                System.out.println("Student [" + currentStudent + "] sees PQ of: " +  chairInfo);
                // System.out.println("Student [" + currentStudent + "] is releasing permit");  //TEST
                sem.release();
                //-- End of atomic instruction --
                

                //First student in PQ check if TA available
                // while (chairInfo.peek() != currentStudent)
                // {
                //     //System.out.println("Top of LL is " + chairInfo.peek() + " and I am " + currentStudent + "\nIs the TA busy: " + studentStatus.getIsTaBusy());
                //     if (!studentStatus.getIsTaBusy()){
                //         throw new InterruptedException();
                //     }
                // };
                //chairInfo = studentStatus.getChairs();

                // while (chairInfo.peek() != currentStudent){}
                // while (studentStatus.getIsTaBusy()){}
                // throw new InterruptedException();

                // if (!studentStatus.getIsTaBusy()){
                //     throw new InterruptedException();
                // }

                while(true)
                {
                    sem.acquire();
                    chairInfo = studentStatus.getChairs();
                    if(chairInfo.peek() == currentStudent){throw new InterruptedException();}
                    sem.release();
                    sleep(1);
                }


            }


            // ALL CHAIRS TAKEN SO FULL; else == (loop) back to programming/working == sleep() their thread
            else{
                System.out.println("Else block hit");
            }


            //[Loop END]            


        
        // TA helping with selected student
        }catch(InterruptedException e){ //InterruptedException == interrupt thread, throw exception
            //System.out.println("ErroR: " + e);


            long currentStudent = Thread.currentThread().getId();   //Gets thread process number 
            ThreadCommunicator taStatus = new ThreadCommunicator(); 
            LinkedList<Long> chairInfo = taStatus.getChairs();   
            int randomNumber = randomIntGenerator();
            //int randomNumber = 5;
            
            // if(taStatus.chairSlotsTaken() == 0){ //No student waiting in hallway for TA
            //     System.out.println("TA is sleeping"); // move to TA
            // }

            taStatus.setIsTaBusy(true);

            System.out.println("Student [" + currentStudent + "] is with the TA for " + randomNumber/1000 + " seconds.");
            //System.out.println("Student [" + currentStudent + "] is with the TA for " + randomNumber + " seconds.");
            try{
                sleep(randomNumber);
            }catch(InterruptedException f){}


            System.out.println("Student [" + currentStudent + "] is finished with the TA and has left the building");


            //System.out.println(chairInfo + " [3] This is wahts ianscdas ");

            if(chairInfo.size() != 0){

                chairInfo.remove();
                taStatus.setChairs(chairInfo);
                System.out.println("In catch: " + chairInfo);
            }
            System.out.println(chairInfo);
            
        
            taStatus.setIsTaBusy(false);
            // taStatus.setChairs(chairInfo);
            // Current student with TA pop from PQ
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

        System.out.println("Main Thread. (TA)");
         // TA:
         //- Help students (Thread SLEEPS FOR RANDOM PERIOD of time)
         //- When TA finishes helping student:
            //- TA check if students waiting in hall (Help students in order; else return to napping)

        // ThreadCommunicator TA = new ThreadCommunicator();
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




///////////////////////////////////////////////// OLD CODE /////////////////////////////////////////////////

    /// Checking if TA is available ///

    // Following was in communicator object //
    /// Current student with the TA ///
    
    // private static long studentWithTA;
    // protected long getStudentWithTA(){return studentWithTA;}
    // protected void setStudentWithTA(long currStudentTakingTA){studentWithTA = currStudentTakingTA;}

    // /// Current student with the TA ///
    // private static long studentInChairOne;
    // protected long getStudentInChairOne(){return studentInChairOne;}
    // protected void setStudentInChairOne(long currStudentTakingTA){studentInChairOne = currStudentTakingTA;}

    //       /// Current student with the TA ///
    // private static long StudentInChairTwo;
    // protected long getStudentInChairTwo(){return StudentInChairTwo;}
    // protected void setStudentInChairTwo(long currStudentTakingTA){StudentInChairTwo = currStudentTakingTA;}

    // if (chairInfo.size() == 1){
    //     studentStatus.setStudentInChairOne(currentStudent);
    // }else if (chairInfo.size() == 2){
    //     studentStatus.setStudentInChairTwo(currentStudent);
    // }

    // Following was on else if statement block of thread
    
    // if (currentStudent == studentStatus.getChairs().peek()){
    //     join(studentStatus.getStudentWithTA());                //put pid of studuent currently helping TA (Waits until that PID terminates, then joins up next)
    //     // chairInfo.poll();
    //     // studentStatus.setChairs(chairInfo);
    // } else if (currentStudent == studentStatus.getStudentInChairTwo()){
    //     join(studentStatus.getStudentInChairOne());
    // } else{
    //     join(studentStatus.getStudentInChairTwo());
    // }
    /// MUTEX

        // while(true){
            //     //Student checking for TA availability
            //     if (!studentStatus.getIsTaBusy()) { //If TA is NOT busy
            //         //Gets help from TA for random amount of time
            //         System.out.println("Student " + currentStudent + " is waking up the TA");
            //         int taHelping = randomIntGenerator();
            //         studentStatus.setIsTaBusy(true);
            //         // studentStatus.setStudentWithTA(currentStudent);
            //         System.out.println("Student " + currentStudent + " is currently getting help from the TA for " + taHelping + " seconds");
            //         sleep(taHelping);
            //         studentStatus.setIsTaBusy(false);

            //         break;

            //     }
            //     else if(studentStatus.areChairsNotFull()) {   //Check if chairs NOT full
            //         Queue<Long> chairInfo = studentStatus.getChairs();
            //         chairInfo.add(currentStudent);
            //         System.out.println("Student " + currentStudent + " is taking chair " + chairInfo.size());
            //         studentStatus.setChairs(chairInfo);

                    
            //     }
            //     else{ //BACK to programming/working ('sleep()' their thread) 
            //         if (!amINext){
            //             System.out.println("-- Student " + currentStudent + " is going back to programming for " + (randomIntGenerator()/100) + " seconds");
            //             sleep(randomIntGenerator());
            //         }
                    
            //     }
            // }
            // System.out.println("Student [" + currentStudent + "] is done with the TA and left the building");