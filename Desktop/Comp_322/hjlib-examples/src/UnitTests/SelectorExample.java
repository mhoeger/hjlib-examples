package UnitTests;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.experimental.actors.Selector;

import java.util.ArrayList;

import static edu.rice.hj.Module0.finish;

/**
 * Created by Marie on 7/4/15.
 */
public class SelectorExample {

    public final Integer STOP_MSG = Integer.MAX_VALUE;
    private ArrayList<int[]> pairs = null;

    public ArrayList<int[]> evenPairsWithSelector (final int[] myArray) throws SuspendableException {
        finish (() -> {
            AddsToEven selector = new AddsToEven();
            selector.start();

            for (int i = 0; i < myArray.length; i++){
                selector.send(i % 2, i);
            }

            // Send to both mailboxes to make sure one is not blocked
            selector.send(0, STOP_MSG);
            selector.send(1, STOP_MSG);
        });

        return pairs;

    }

    private void setPairList(ArrayList<int[]> myPairs){
        pairs = myPairs;
    }

    private final class AddsToEven extends Selector<Integer> {

        private Integer previous = null;
        private ArrayList<int[]> evenPairs = new ArrayList<>();

        protected AddsToEven() {
            super(2);
        }

        @Override
        protected void process(Integer msg) {

            // Terminate an actor using special a stop message
            if (STOP_MSG.equals(msg)) {
                setPairList(evenPairs);
                exit(); // never forget to terminate an actor

            } else {
                // Enable correct mailbox to get an even sum
                if (previous == null){
                    previous = msg;
                    enableCorrectMailbox(previous);

                // Add matched pair and continue enable correct mailboxes
                } else {
                    int[] temp = new int[2];
                    temp[0] = previous;
                    temp[1] = msg;
                    evenPairs.add(temp);
                    previous = null;
                    enableAll();
                }
            }
        }

        private void enableCorrectMailbox(Integer myInt){
            if (isEven(myInt)){
                enable(0);
                disable(1);
            } else {
                enable(1);
                disable(0);
            }
        }

        private boolean isEven(Integer myInt){
            if (myInt % 2 == 0)
                return true;
            return false;
        }
    }
}
