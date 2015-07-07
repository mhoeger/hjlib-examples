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
                myActor.process(arraysToSum[i]);
            }
            myActor.process(myActor.STOP_MSG);
        });
        return finalSum;
    }

    private void setFinalSum(Integer sum){
        finalSum = sum;
    }

    private class ArraySumActor extends Actor<Object> {

        private final Object STOP_MSG = Integer.MAX_VALUE;
        private Integer cumulativeSum = 0;

        ArraySumActor(){
            super();
        }

        @Override
        protected void process(Object msg) {
            // Terminate an actor using special a stop message
            if (STOP_MSG.equals(msg)) {
                setFinalSum(cumulativeSum);
                exit(); // never forget to terminate an actor

            // Get sum of array in parallel and store value
            } else if (msg instanceof int[]) {
                int[] arrayMsg = (int[]) msg;
                HjDataDrivenFuture ddfA = newDataDrivenFuture();
                HjDataDrivenFuture ddfB = newDataDrivenFuture();
                final Integer half = arrayMsg.length / 2;

                System.out.println("int array begins with " + arrayMsg[0] + " and is length " + arrayMsg.length);
                System.out.println("PAUSE cumsum:" + cumulativeSum);
                pause();
                System.out.println("PAUSE cumsum:" + cumulativeSum);
                async(() -> {
                    ddfA.put(seq.sumArraySequential(Arrays.copyOfRange(arrayMsg, 0, half)));
                });
                async(() -> {
                    ddfB.put(seq.sumArraySequential(Arrays.copyOfRange(arrayMsg, half, arrayMsg.length)));
                });

                asyncAwait(ddfA, ddfB, () -> {
                    cumulativeSum += (Integer) ddfA.get() + (Integer) ddfB.get();
                    System.out.println("RESUME !:" + cumulativeSum);
                    resume();
                });

            // Output an error message for invalid input
            } else {
                System.out.println("Invalid input '" + msg +
                        "' of non-Integer type " + msg.getClass().getName());
            }
        }
    }

//    private class MasterActor extends Actor<Object> {
//
//        private TaggingActor[] workerActors;
//        private int messageCount, numWorkers, tag;
//
//        MasterActor(int numWorkers) {
//            super();
//            this.numWorkers = numWorkers;
//            workerActors = new TaggingActor[numWorkers];
//            messageCount = 0;
//            tag = 65;
//
//            // Instantiate and start worker actors
//            for (int i = 0; i < numWorkers; i++){
//                workerActors[i] = new TaggingActor();
//                workerActors[i].start();
//            }
//        }
//
//        @Override
//        protected void process(final Object msg) {
//            // Terminate an actor using special a stop message
//            if (TaggingActor.STOP_MSG.equals(msg)) {
//                if (workerActors != null) {
//                    for (TaggingActor actor : workerActors)
//                        actor.process(msg);
//                }
//                exit(); // never forget to terminate an actor
//
//            // Distribute work equally to actors
//            } else if (msg instanceof Integer) {
//                System.out.println("here" + msg);
//                workerActors[messageCount % numWorkers].process(msg);
//                messageCount++;
//
//            // Output an error message for invalid input
//            } else {
//                System.out.println("Invalid input '" + msg +
//                        "' of non-Integer type " + msg.getClass().getName());
//            }
//        }
//    }
//
//    private static class TaggingActor extends Actor<Object> {
//        static final Object STOP_MSG = Integer.MAX_VALUE;
//        static String tag = "A";
//        private String message = "";
//
//        @Override
//        protected void process(final Object msg) {
//            // Terminate if stop message
//            if (TaggingActor.STOP_MSG.equals(msg)) {
//                System.out.println("Exiting Actor: " + message);
//                exit(); // never forget to terminate an actor
//                // Process Integer message
//            } else if (msg instanceof Integer) {
//                message += tag + msg + "\t";
//                System.out.println(message);
//                // Output an error message for invalid input
//            } else {
//                System.out.println("Invalid input '" + msg +
//                        "' of non-Integer type " + msg.getClass().getName());
//            }
//        }
//
//    }


}
