package UnitTests;

import edu.rice.hj.api.HjDataDrivenFuture;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.actors.Actor;
import java.util.Arrays;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.newDataDrivenFuture;
import static edu.rice.hj.Module1.async;
import static edu.rice.hj.Module1.asyncAwait;

/**
 * Created by Marie on 7/3/15.
 *
 * This is a simple example of an actor that takes the sum of an array and stores a cumulative
 * sum. This actor's process method uses parallel constructs, and therefore uses the pause and
 * resume methods. These methods are used instead of a finish, because they are non-blocking
 * operations that improve performance.
 *
 */
public class ActorExample {

    private SequentialCheck seq = new SequentialCheck();
    private Integer finalSum = null;

    public Integer arraySumWithActor(final int[][] arraysToSum) throws SuspendableException {
        finish (() -> {
            ArraySumActor myActor = new ArraySumActor();
            myActor.start();

            for (int i = 0; i < arraysToSum.length; i++) {
                myActor.send(arraysToSum[i]);
            }
            myActor.send(myActor.STOP_MSG);
        });
        return finalSum;
    }

    private void setFinalSum(Integer sum){
        finalSum = sum;
    }

    private class ArraySumActor extends Actor<Object> {

        private final Object STOP_MSG = Integer.MAX_VALUE;
        private Integer cumulativeSum = 0;
        private volatile int count = 0;

        ArraySumActor(){
            //The constructor accepts a boolean flag, pAlwaysSequentialBody, that indicates whether
            //or not the code inside of the body of process() is sequential or not. The default value
            //is true.
            super(false);
        }

        @Override
        protected void process(Object msg) {
            // Terminate an actor using special a stop message
            if (STOP_MSG.equals(msg)) {
                tryProcessMessage();
                setFinalSum(cumulativeSum);
                exit(); // never forget to terminate an actor

            // Get sum of array in parallel and store value
            } else if (msg instanceof int[]) {
                int[] arrayMsg = (int[]) msg;
                HjDataDrivenFuture ddfA = newDataDrivenFuture();
                HjDataDrivenFuture ddfB = newDataDrivenFuture();
                final Integer half = arrayMsg.length / 2;
                System.out.println("processing int array " + count + ": begins with " + arrayMsg[0] + " and is length " + arrayMsg.length);

                pause();

                System.out.println("PAUSED:" + count);

                asyncAwait(ddfA, ddfB, () -> {
                    cumulativeSum += (Integer) ddfA.get() + (Integer) ddfB.get();
                    System.out.println("RESUME:" + count++);
                    resume();
                });

                async(() -> {
                    ddfA.put(seq.sumArraySequential(Arrays.copyOfRange(arrayMsg, 0, half)));
                });
                async(() -> {
                    ddfB.put(seq.sumArraySequential(Arrays.copyOfRange(arrayMsg, half, arrayMsg.length)));
                });

            // Output an error message for invalid input
            } else {
                System.out.println("Invalid input '" + msg +
                        "' of non-Integer type " + msg.getClass().getName());
            }
        }
    }
}
