import java.util.Random;
import java.util.LinkedList;
import java.util.*;

/* NOTE:
- Write program that implements FIFO and LRU 
- Generate random page-reference string

*/
public class A04Q02 {

    public static void main(String args[]) {

        Random rn = new Random();
        LinkedList<Integer> pageRefListFIFO = new LinkedList<Integer>();

        // -- Page Frames (1-7 randomly) --
        int frameSize = rn.nextInt((7 - 1)) + 1; // 1-7

        // -- Page-Reference String (Generate random int from 0-9 (Inclusive)) --
        for (int i = 0; i < 15; i++) {
            // int answer2 = rn.nextInt(10);
            pageRefListFIFO.add(rn.nextInt(10));
        }

        LinkedList<Integer> pageRefListLRU = (LinkedList) pageRefListFIFO.clone();

        // -- FIFO --
        System.out.println("The FIFO Algorithm in Action: ");
        LinkedList<Integer> fifoResult = fifoAlg(pageRefListFIFO, frameSize);

        System.out.println("");
        System.out.println("");

        // -- LRU --
        System.out.println("The LRU Algorithm in Action: ");
        LinkedList<Integer> lruResult = lruAlg(pageRefListLRU, frameSize);

    }

    // -- FIFO ALGO --
    public static LinkedList<Integer> fifoAlg(LinkedList<Integer> input, int PAGE_FRAMES) {

        System.out.println("Our frame size is: " + PAGE_FRAMES);
        System.out.print("Our given reference string is: [");
        for (Integer val : input) {
            System.out.print(val + " ");
        }
        System.out.println("]");

        LinkedList<Integer> frame = new LinkedList<Integer>();
        int faultCount = 0;

        // -- Intially putting faults in page slots/frame --
        while (PAGE_FRAMES != 0) {

            if (!frame.contains(input.peek())) {
                //System.out.println("Adding " + input.peek() + " to the frame");
                frame.add(input.pop());
                PAGE_FRAMES--;
                faultCount++;

                // for (int i = 0; i < frame.size(); i++) {
                //     System.out.println(i + " = " + frame.get(i));
                // }
            } else {
                input.pop();
            }
        }

        // -- (After have page slots/frames set, pop original list (Input) until empty,
        // modifying page slots/frames according to fifo) --
        while (input.size() != 0) {
            // as long as not already in there
            if (!frame.contains(input.peek())) {
                //System.out.println("Removing " + frame.pop() + " from the frame.");
                //System.out.println("Adding " + input.peek() + " to the frame.");
                frame.add(input.pop());
                faultCount++;

                // for (int i = 0; i < frame.size(); i++) {
                //     System.out.println(i + " = " + frame.get(i));
                // }
            } else {
                input.pop();
            }
        }

        System.out.println("Total Fault Count for FIFO Algorithm is: [" + faultCount + "]");
        return frame;
    }

    // -- LRU Algorithm --
    public static LinkedList<Integer> lruAlg(LinkedList<Integer> input, int PAGE_FRAMES) {

        //TESTING DATA 
        // PAGE_FRAMES = 2;
        // input = new LinkedList<Integer>(Arrays.asList(9, 9, 2, 4, 6, 3, 4, 5, 0, 8, 3, 6, 3, 8, 3));

        System.out.println("Our frame size is: " + PAGE_FRAMES);
        System.out.print("Our given reference string is: [");
        for (Integer val : input) {
            System.out.print(val + " ");
        }
        System.out.println("]");

        LinkedList<Integer> frame = new LinkedList<Integer>();

        int count = 0;
        int pageFaults = 0;
        for (int pageNumber : input) {
            // Insert it into set if not present
            // already which represents page fault
            if (!frame.contains(pageNumber)) {

                // Check if the set can hold equal pages
                if (frame.size() == PAGE_FRAMES) {
                    frame.remove(0);
                    frame.add(PAGE_FRAMES - 1, pageNumber);
                } else
                    frame.add(count, pageNumber);
                    
                // Increment page faults
                pageFaults++;
                ++count;

            } else {
                // Remove the indexes page
                frame.remove((Object) pageNumber);
                // insert the current page
                frame.add(frame.size(), pageNumber);
            }

        }

        System.out.println("Total Fault Count for LRU Algorithm is: [" + pageFaults + "]");

        return input;
    }

}
