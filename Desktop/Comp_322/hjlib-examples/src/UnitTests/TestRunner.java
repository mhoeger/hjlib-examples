package UnitTests;

/**
 * Created by Marie on 6/30/15.
 */

import edu.rice.hj.api.HjPhaser;
import edu.rice.hj.api.HjPhaserMode;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.experimental.ModuleZ;
import edu.rice.hj.experimental.api.*;
import edu.rice.hj.runtime.actors.Actor;
import edu.rice.hj.runtime.config.HjSystemProperty;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;
import static edu.rice.hj.experimental.ModuleZ.*;

/**
 * Created by Marie on 6/29/15.
 */
public class TestRunner extends TestCase {

    private final int numChunks = 2;
    private final char[] multipleHitEurekaArray = "Madam Marsley, must Mixer make movies more mellow?".toCharArray();
    private final char[] searchHit = "m".toCharArray();

    /*
     * Test driving methods
     */

    // ASYNC TESTS
    public void testAsyncFinish() {
        final int start = 0;
        final int end = 100;
        System.out.println("Starting async/finish test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final int[] results = new int[2];

        launchHabaneroApp(() -> {
            results[0] = sumSequential(start, end);
        });

        launchHabaneroApp(() -> {
            results[1] = sumWithAsyncFinish(start, end);
        });

        assertTrue("Sum: Expected = " + results[0] + ", Actual = " + results[1], results[1]== results[0]);

        System.out.println("Async/Finish test ends.");

    }

    public void testAsyncPhased(){

        System.out.println("Starting asyncPhased test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final int[][] resultsPar = new int[1][];
        final int[] resultsSeq = new int[3];

        launchHabaneroApp(() -> {
            resultsSeq[0] = sumSequential(1, 5);
            resultsSeq[1] = sumSequential(1, 10);
            resultsSeq[2] = sumSequential(1, 15);
        });

        launchHabaneroApp(() -> {
            resultsPar[0] = sumWithAsyncPhased();
        });

        for (int i = 0; i < 3; i ++) {
            assertTrue("Sum: Expected = " + resultsSeq[i] + ", Actual = " + resultsPar[0][i], resultsPar[0][i] == resultsSeq[i]);
        }

        System.out.println("asyncPhased test ends.");
    }

    // EUREKA TESTS
    public void testSearchEureka() {
        System.out.println("Starting search eureka test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final Integer[] resultPar = new Integer[1];
        final List[] resultSeq = new List[1];

        launchHabaneroApp(() -> {
            resultPar[0] = searchEureka(multipleHitEurekaArray,  searchHit);
        });

        launchHabaneroApp(() -> {
            resultSeq[0] = searchSequential(multipleHitEurekaArray,  searchHit);
        });

        assertTrue("Search Eureka: " + resultPar[0] + " not found in sequential search.", resultSeq[0].contains(resultPar[0]));

        System.out.println("Search eureka test ends.");
    }

    public void testMaximumEureka() {
        System.out.println("Starting maximum eureka test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final Integer[] results = new Integer[2];

        launchHabaneroApp(() -> {
            results[0] = maximumSearchEureka(multipleHitEurekaArray, searchHit);
        });

        launchHabaneroApp(() -> {
            results[1] = maximumSearchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Search Index: Expected = " + results[0] + ", Actual = " + results[1], results[1].equals(results[0]));

        System.out.println("Maximum eureka test ends.");
    }

    public void testMinimumEureka() {

        System.out.println("Starting minimum eureka test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final Integer[] results = new Integer[2];

        launchHabaneroApp(() -> {
            results[0] = minimumSearchEureka(multipleHitEurekaArray, searchHit);
        });

        launchHabaneroApp(() -> {
            results[1] = minimumSearchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Search Index: Expected = " + results[0] + ", Actual = " + results[1], results[1].equals(results[0]));

        System.out.println("Minimum eureka test ends.");
    }

    public void testCountEureka() {
        final int count = 4;
        System.out.println("Starting count eureka test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final List[] results = new List[2];

        launchHabaneroApp(() -> {
            results[0] = countEureka(multipleHitEurekaArray, count, searchHit);
        });

        launchHabaneroApp(() -> {
            results[1] = searchSequential(multipleHitEurekaArray, searchHit);
        });

        List<Integer> parResults = results[0];

        for (Integer result : parResults){
            assertTrue("Count Eureka: " + result + " not found in sequential search.", results[1].contains(result));
        }

        System.out.println("Count eureka test ends.");
    }

    public void testTimerEureka() {
        System.out.println("Starting timer eureka test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final Integer[] resultPar = new Integer[2];
        final List[] resultSeq = new List[1];

        launchHabaneroApp(() -> {
            resultPar[0] = timerEureka(multipleHitEurekaArray, searchHit, 1000);
        });

        launchHabaneroApp(() -> {
            resultPar[1] = timerEureka(multipleHitEurekaArray, searchHit, 0);
        });

        launchHabaneroApp(() -> {
            resultSeq[0] = searchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Timer Eureka (1,000 ms): " + resultPar[0] + " not found in sequential search.", resultSeq[0].contains(resultPar[0]));
        assertTrue("Timer Eureka (0 ms): " + resultPar[1] + " should be null.", resultPar[1] == null);

        System.out.println("Count eureka test ends.");
    }

    public void testEngineEureka() {
        System.out.println("Starting engine eureka test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final Integer[] resultPar = new Integer[2];
        final List[] resultSeq = new List[1];

        launchHabaneroApp(() -> {
            resultPar[0] = engineEureka(multipleHitEurekaArray, searchHit, 1000);
        });

        launchHabaneroApp(() -> {
            resultPar[1] = engineEureka(multipleHitEurekaArray, searchHit, 0);
        });

        launchHabaneroApp(() -> {
            resultSeq[0] = searchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Engine Eureka (1,000 time units): " + resultPar[0] + " not found in sequential search.", resultSeq[0].contains(resultPar[0]));
        assertTrue("Timer Eureka (0 time units): " + resultPar[1] + " should be null.", resultPar[1] == null);

        System.out.println("Engine eureka test ends.");
    }

    public void testActors() {
        System.out.println("Starting actors");
        launchHabaneroApp(() -> startActors());
    }

    /*
     * HjLib construct unit tests
     */

    // Uses: async, finish
    public int sumWithAsyncFinish(int start, int end) throws SuspendableException {
        final int[] sum = new int[2];
        finish(() -> {
            final int mid = (end - start) / 2;
            async(() -> {
                sum[0] = sumSequential(start, mid);
            });
            if (mid > start) {
                sum[1] = sumSequential(mid + 1, end);
            } else {
                sum[1] = 0;
            }
        });
        return sum[0] + sum[1];
    }

    // Uses finish, asyncPhased, next, HjPhaserMode, HjPhaser
    public int[] sumWithAsyncPhased() throws SuspendableException {
        final int[] chunkSums = new int[3];
        final int[] resultSums = new int [3];

        finish(() -> {
            HjPhaser ph = newPhaser(HjPhaserMode.SIG_WAIT);
            asyncPhased(ph.inMode(HjPhaserMode.SIG), () -> {
                chunkSums[0] = sumSequential(1,5);
                next();     // Signal that self is ready, continue to phase 2 without waiting
                resultSums[0] = chunkSums[0];
            });
            asyncPhased(ph.inMode(HjPhaserMode.SIG_WAIT), () -> {
                chunkSums[1] = sumSequential(6,10);
                next();        // Signal that self is ready, waits on A1 to signal
                resultSums[1] = chunkSums[1] + chunkSums[0];
            });
            asyncPhased(ph.inMode(HjPhaserMode.WAIT), () -> {
                chunkSums[2] = sumSequential(11,15);
                next();     // Waits on signal from A1 and A2
                resultSums[2] = chunkSums[2] + chunkSums[1] + chunkSums[0];
            });
        });
        return resultSums;
    }

    // Find the index that contains searchHit in dataArray. If searchHit is present multiple times, the answer may between runs.
    // Uses: newSearchEureka, HjSearchEureka
    public Integer searchEureka(final char[] dataArray, final char[] searchHit) throws SuspendableException {
        final HjSearchEureka<Integer> resultEureka = newSearchEureka(null);
        boolean checkAgainstNull = true;
        return (Integer) runSearchWithEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    // Find index of last occurrence of searchHit
    // Uses: newExtremaEureka, HjExtremaEureka
    public Integer maximumSearchEureka(final char[] dataArray, final char[] searchHit) throws SuspendableException {
        final HjExtremaEureka<Integer> resultEureka = newExtremaEureka(Integer.MIN_VALUE, (Integer i, Integer j) -> i.compareTo(j), false);
        boolean checkAgainstNull = false;
        return (Integer) runSearchWithEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    // Uses: newExtremaEureka, HjExtremaEureka
    public Integer minimumSearchEureka(final char[] dataArray, final char[] searchHit) throws SuspendableException {
        final HjExtremaEureka<Integer> resultEureka = newExtremaEureka(Integer.MAX_VALUE, (Integer i, Integer j) -> j.compareTo(i));
        boolean checkAgainstNull = false;
        return (Integer) runSearchWithEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    // Uses: newCountEureka, HjCountEureka
    public List<Integer> countEureka(final char[] dataArray, final int count, final char [] searchHit) throws SuspendableException {
        final HjCountEureka<Integer> resultEureka = newCountEureka(count, count);
        boolean checkAgainstNull = true;
        return (List<Integer>) runSearchWithEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    // Uses: newTimerEureka, HjTimerEureka
    public Integer timerEureka(final char[] dataArray, final char[] searchHit, final int time) throws SuspendableException {
        final HjTimerEureka<Integer> resultEureka = newTimerEureka(time);
        boolean checkAgainstNull = true;
        return (Integer) runSearchWithEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    public Integer engineEureka(final char[] dataArray, final char[] searchHit, final int time) throws SuspendableException {
        final HjEngineEureka<Integer> resultEureka = newEngineEureka(time);
        boolean checkAgainstNull = true;
        return (Integer) runSearchWithEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    // Uses: HjEUreka, finish(eureka), async
    public Object runSearchWithEureka(final char[] dataArray, final char[] searchHit, final HjEureka resultEureka, boolean checkAgainstNull) throws SuspendableException {
        final int chunkSize = dataArray.length / numChunks;
        ModuleZ.finish(resultEureka, () -> {
            for (int c = 0; c < numChunks; c++) {
                // perform chunking to increase amount of work done in the loop
                final int startI = c * chunkSize;
                final int endI = Math.min(dataArray.length, startI + chunkSize);
                async(() -> {
                    for (int i = startI; i < endI; i++) {
                        // Eureka check depends on the specific type of eureka (extrema - must check object. others - check null)
                        if (checkAgainstNull)
                            resultEureka.check(null);
                        else
                            resultEureka.check(i);

                        if (dataArray[i] == searchHit[0]) {
                            resultEureka.offer(i);
                        }
                    }
                });
            }
        });
        return resultEureka.get();
    }

    public void startActors() throws SuspendableException {
        finish (() -> {
            MasterActor driverActor = new MasterActor(5);
            driverActor.start();
            for (int i = 0; i < 50; i++) {
                driverActor.process(Integer.valueOf(i));
                if (i % 10 == 9)
                    driverActor.changeTag();
            }
            driverActor.process(TaggingActor.STOP_MSG);
        });
    }

    private class MasterActor extends Actor<Object> {

        private TaggingActor[] workerActors;
        private int messageCount, numWorkers, tag;

        MasterActor(int numWorkers) {
            super();
            this.numWorkers = numWorkers;
            workerActors = new TaggingActor[numWorkers];
            messageCount = 0;
            tag = 65;

            // Instantiate and start worker actors
            for (int i = 0; i < numWorkers; i++){
                workerActors[i] = new TaggingActor();
                workerActors[i].start();
            }
        }

        @Override
        protected void process(final Object msg) {
            // Terminate an actor using special a stop message
            if (TaggingActor.STOP_MSG.equals(msg)) {
                if (workerActors != null) {
                    for (TaggingActor actor : workerActors)
                        actor.process(msg);
                }
                exit(); // never forget to terminate an actor
                // Distribute work equally to actors
            } else if (msg instanceof Integer) {
                System.out.println("here" + msg);
                workerActors[messageCount % numWorkers].process(msg);
                messageCount++;

                // Output an error message for invalid input
            } else {
                System.out.println("Invalid input '" + msg +
                        "' of non-Integer type " + msg.getClass().getName());
            }
        }

        protected void changeTag(){
            for (TaggingActor actor : workerActors){
                actor.pause();
            }
            TaggingActor.tag = (char) ++tag + "";
            if (tag > 90)
                tag = 65;
            System.out.println("resuming...");

//            for (int i = 0; i < numWorkers; i++){
//                System.out.println("starting" + i);
//                workerActors[i].resume();
//            }
            System.out.println("done resuming");
        }
    }

    private static class TaggingActor extends Actor<Object> {
        static final Object STOP_MSG = Integer.MAX_VALUE;
        static String tag = "A";
        private String message = "";

        @Override
        protected void process(final Object msg) {
            // Terminate if stop message
            if (TaggingActor.STOP_MSG.equals(msg)) {
                System.out.println("Exiting Actor: " + message);
                exit(); // never forget to terminate an actor
                // Process Integer message
            } else if (msg instanceof Integer) {
                message += tag + msg + "\t";
                System.out.println(message);
                // Output an error message for invalid input
            } else {
                System.out.println("Invalid input '" + msg +
                        "' of non-Integer type " + msg.getClass().getName());
            }
        }

    }

    /*
     * Sequential methods for unit test checking
     */
    public int sumSequential(int start, int end){
        int result = 0;
        for (int i = start; i <= end; i++){
            result += i;
        }
        return result;
    }

    public Integer maximumSearchSequential(final char[] dataArray, final char[] searchHit){
        Integer max = Integer.MIN_VALUE;
        for (int i = 0; i < dataArray.length; i++){
            if (dataArray[i] == searchHit[0]) {
                if (max.compareTo(i) < 0)
                    max = i;
            }
        }
        return max;
    }

    public Integer minimumSearchSequential(final char[] dataArray, final char[] searchHit){
        Integer min = Integer.MAX_VALUE;
        for (int i = 0; i < dataArray.length; i++){
            if (dataArray[i] == searchHit[0]) {
                if (min.compareTo(i) > 0)
                    min = i;
            }
        }
        return min;
    }

    public List<Integer> searchSequential(final char[] dataArray, final char [] searchHit){
        List<Integer> resultOptions = new ArrayList<Integer>();
        for (int i = 0; i < dataArray.length; i++){
            if (dataArray[i] == searchHit[0]) {
                resultOptions.add(i);
            }
        }
        return resultOptions;
    }

}
