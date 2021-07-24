import java.util.Random;
import java.util.LinkedList;
import java.util.*;

/* NOTE:
- Write program that implements FIFO and LRU 
- Generate random page-reference string

*/
public class A04Q02 {

    private static int PAGE_FRAMES = 3; // 1-7 randomly

    public static void main(String args[]) {

        // //Generate random int from 0-9 (Inclusive)
        // Random rn = new Random();
        // int answer = rn.nextInt(10);
        
        LinkedList<Integer> pageRefList = new LinkedList<Integer>(Arrays.asList(8, 2, 2, 3, 1, 2, 0, 5, 6, 7)); // 0-9

        //FIFO
        LinkedList<Integer> x = fifoAlg(pageRefList);

    }

    public static LinkedList<Integer> fifoAlg(LinkedList<Integer> input) {

        LinkedList<Integer> frame = new LinkedList<Integer>();

        // Not EMPTY list && not putting in duplicate lists (Intially putting faults in
        // page slots/frame)
        if (PAGE_FRAMES != 0 && !frame.contains(input.peek())) {
            frame.add(input.pop());

            for (int i = 0; i < frame.size(); i++) {
                System.out.println(i + " = " + frame.get(i));
            }

            PAGE_FRAMES--;
        }

        while (input.size() != 0) {
            if (!frame.contains(input.peek())) { // as long as not already in there

                frame.add(input.pop());

                for (int i = 0; i < frame.size(); i++) {
                    System.out.println(i + " = " + frame.get(i));
                }

            }
        }

        return frame;
    }

}
