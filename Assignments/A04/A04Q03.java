import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

// Remember to make communicator objects static, else a new instance is created each time rather than reusing a single one.
class Communicator{
    // Making sure that the read/writes are atomic w/ mutex lock
    private Semaphore semaphore = new Semaphore(1);
    protected void acquireSemaphore() throws InterruptedException{semaphore.acquire();} 
    protected void releaseSemaphore(){semaphore.release();}

    private static int writeCounter;
    protected int getWriteCounter(){return writeCounter;}
    protected void setWriteCounter(int counterVal){writeCounter = counterVal;}
    protected void decrementWriteCounter(){writeCounter--;}

    private static int readCounter;
    protected int getReadCounter(){return readCounter;}
    protected void setReadCounter(int counterVal){readCounter = counterVal;}
    protected void decrementReadCounter(){readCounter--;}

    private static LinkedList<Long> communicator = new LinkedList<Long>();
    protected LinkedList<Long> getCommunicator() {return communicator;}
    protected void setCommunicator(LinkedList<Long> update){communicator = update;}
}


class Producer extends Thread{
    Communicator communicator = new Communicator();

    public void run(){
        //System.out.println("We wil produce " + communicator.getCounter() + " catalan numbers.");
        //communicator.decrementCounter();
        //System.out.println("Are our decrements working? " + communicator.getCounter());

        while (communicator.getWriteCounter() != 0){
            try{
                communicator.acquireSemaphore();
                LinkedList<Long> update = communicator.getCommunicator();
                int writeCounter = communicator.getWriteCounter();

                long catalanNumber = calculateCatalanNumber(writeCounter);
                update.add(catalanNumber);
                communicator.setCommunicator(update);
                communicator.decrementWriteCounter();

                communicator.releaseSemaphore();
                sleep(1);
            }
            catch(InterruptedException e){}
        }
    }

    public long calculateCatalanNumber(int n){
        long numerator = 1;
        long denominator1 = 1;
        long denominator2 = 1;

        for (int i=1; i<(n*2); i++){
            numerator += numerator*i;
        }

        for (int i=1; i<(n+1); i++){
            denominator1 += denominator1*i;
        }

        for (int i=1; i<n; i++){
            denominator2 += denominator2*i;
        }

        return numerator/(denominator1*denominator2);
    }
}


class Consumer extends Thread{
    Communicator communicator = new Communicator();

    public void run(){
        
        while (communicator.getReadCounter() != 0){
            try{
                communicator.acquireSemaphore();

                LinkedList<Long> read = communicator.getCommunicator();

                if(read.peek() != null){
                    long currRead = read.pop();
                    communicator.setCommunicator(read);
                    System.out.println("Currently reading the value of: " + currRead + " from shared communicator");
                    communicator.decrementReadCounter();
                    communicator.releaseSemaphore();
                    sleep(1);
                }else{
                    communicator.releaseSemaphore();
                    sleep(1);
                }                
            }catch(InterruptedException e){}
        }
    }
}


public class A04Q03{
    public static void main(String[] args){

        // How many Catalan numbers to produce?
        Scanner scanner = new Scanner(System.in);
        System.out.println("How many communication instances?");
        int userInput = scanner.nextInt();
        scanner.close();

        if (userInput >= 7){System.out.println("Incorrect value will be displayed due to java Integer limitations");}

        // Set required amount into shared object
        Communicator communicator = new Communicator();
        communicator.setReadCounter(userInput);
        communicator.setWriteCounter(userInput);

        // Begin both threads; producer thread will have a constructor that initializes with the number of wanted Catalan numbers.
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        producer.start();
        consumer.start();
    }
}