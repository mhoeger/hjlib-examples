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

    private final int[] intArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private final char[] multipleHitEurekaArray = "Madam Marsley, must Mixer make movies more mellow?".toCharArray();
    private final char[] searchHit = "m".toCharArray();

    private final int numChunks = 2;

    // Method test object declarations
    private final SequentialCheck check = new SequentialCheck();
    private final AsyncExample as = new AsyncExample();
    private final EurekaExample eu = new EurekaExample();
    private final FinishAccumulatorExample fa = new FinishAccumulatorExample();
    private final PhaserExample ph = new PhaserExample();
    private final PlacesRegionsExample pr = new PlacesRegionsExample();
    private final IsolatedExample iso = new IsolatedExample();
    private final FutureExample fut = new FutureExample();
    private final ForLoopExample fl = new ForLoopExample();
    private final ActorExample act = new ActorExample();
    private final SelectorExample selec = new SelectorExample();

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
            results[0] = check.sumSequential(start, end);
        });

        launchHabaneroApp(() -> {
            results[1] = as.sumWithAsyncFinish(start, end);
        });

        assertTrue("Sum: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Async/Finish test ends.");

    }

    public void testAsyncPhased(){

        System.out.println("Starting asyncPhased test...");

        HjSystemProperty.abstractMetrics.setProperty(true);
        final int[][] resultsPar = new int[1][];
        final int[] resultsSeq = new int[3];

        launchHabaneroApp(() -> {
            resultsSeq[0] = check.sumSequential(1, 5);
            resultsSeq[1] = check.sumSequential(1, 10);
            resultsSeq[2] = check.sumSequential(1, 15);
        });

        launchHabaneroApp(() -> {
            resultsPar[0] = as.sumWithAsyncPhased();
        });

        for (int i = 0; i < 3; i ++) {
            assertTrue("Sum: Expected = " + resultsSeq[i] + ", Actual = " + resultsPar[0][i], resultsPar[0][i] == resultsSeq[i]);
        }

        System.out.println("asyncPhased test ends.");
    }

    // EUREKA TESTS

    public void testSearchEureka() {
        System.out.println("Starting search eureka test...");
        final Integer[] resultPar = new Integer[1];
        final List[] resultSeq = new List[1];

        launchHabaneroApp(() -> {
            resultPar[0] = eu.searchWithEureka(multipleHitEurekaArray, searchHit);
        });

        launchHabaneroApp(() -> {
            resultSeq[0] = check.searchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Search Eureka: " + resultPar[0] + " not found in sequential search.", resultSeq[0].contains(resultPar[0]));

        System.out.println("Search eureka test ends.");
    }

    public void testMaximumEureka() {
        System.out.println("Starting maximum eureka test...");

        final Integer[] results = new Integer[2];

        launchHabaneroApp(() -> {
            results[0] = eu.searchMaximumWithEureka(multipleHitEurekaArray, searchHit);
        });

        launchHabaneroApp(() -> {
            results[1] = check.maximumSearchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Search Index: Expected = " + results[0] + ", Actual = " + results[1], results[1].equals(results[0]));

        System.out.println("Maximum eureka test ends.");
    }

    public void testMinimumEureka() {

        System.out.println("Starting minimum eureka test...");

        final Integer[] results = new Integer[2];

        launchHabaneroApp(() -> {
            results[0] = eu.searchMinimumWithEureka(multipleHitEurekaArray, searchHit);
        });

        launchHabaneroApp(() -> {
            results[1] = check.minimumSearchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Search Index: Expected = " + results[0] + ", Actual = " + results[1], results[1].equals(results[0]));

        System.out.println("Minimum eureka test ends.");
    }

    public void testCountEureka() {
        final int count = 4;
        System.out.println("Starting count eureka test...");

        final List[] results = new List[2];

        launchHabaneroApp(() -> {
            results[0] = eu.searchWithCountEureka(multipleHitEurekaArray, count, searchHit);
        });

        launchHabaneroApp(() -> {
            results[1] = check.searchSequential(multipleHitEurekaArray, searchHit);
        });

        List<Integer> parResults = results[0];

        for (Integer result : parResults){
            assertTrue("Count Eureka: " + result + " not found in sequential search.", results[1].contains(result));
        }

        System.out.println("Count eureka test ends.");
    }

    public void testTimerEureka() {
        System.out.println("Starting timer eureka test...");

        final Integer[] resultPar = new Integer[2];
        final List[] resultSeq = new List[1];

        launchHabaneroApp(() -> {
            resultPar[0] = eu.searchWithTimerEureka(multipleHitEurekaArray, searchHit, 1000);
        });

        launchHabaneroApp(() -> {
            resultPar[1] = eu.searchWithTimerEureka(multipleHitEurekaArray, searchHit, 0);
        });

        launchHabaneroApp(() -> {
            resultSeq[0] = check.searchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Timer Eureka (1,000 ms): " + resultPar[0] + " not found in sequential search.", resultSeq[0].contains(resultPar[0]));
        assertTrue("Timer Eureka (0 ms): " + resultPar[1] + " should be null.", resultPar[1] == null);

        System.out.println("Count eureka test ends.");
    }

    public void testEngineEureka() {
        System.out.println("Starting engine eureka test...");

        final Integer[] resultPar = new Integer[2];
        final List[] resultSeq = new List[1];

        launchHabaneroApp(() -> {
            resultPar[0] = eu.searchWithEngineEureka(multipleHitEurekaArray, searchHit, 1000);
        });

        launchHabaneroApp(() -> {
            resultPar[1] = eu.searchWithEngineEureka(multipleHitEurekaArray, searchHit, 0);
        });

        launchHabaneroApp(() -> {
            resultSeq[0] = check.searchSequential(multipleHitEurekaArray, searchHit);
        });

        assertTrue("Engine Eureka (1,000 time units): " + resultPar[0] + " not found in sequential search.", resultSeq[0].contains(resultPar[0]));
        assertTrue("Timer Eureka (0 time units): " + resultPar[1] + " should be null.", resultPar[1] == null);

        System.out.println("Engine eureka test ends.");
    }

    // ACTORS TEST

    public void testActor() {
        System.out.println("Starting actor test... ");

        int l = 5;
        int w = 10;

        int[][] int2DArray = new int[l][w];
        // Send lots of integers
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < w; j++) {
                int2DArray[i][j] = (i + 1) * (j + 1);
            }
        }

        int[] result = new int[2];

        launchHabaneroApp(() -> {
            result[0] = act.arraySumWithActor(int2DArray);
        });

        launchHabaneroApp(() -> {
            result[1] = check.sum2DArraySequential(int2DArray);
        });

        assertTrue("Sum: Expected = " + result[1] + ", Actual = " + result[0], result[0] == result[1]);

        System.out.println("Actor test ends.");
    }

    public void testSelector(){

        System.out.println("Starting selector test...");
        ArrayList<int[]>[] resultPar = new ArrayList[1];
        int[] myArray = new int[30];
        for (int i = 0; i < 30; i++){
            if (i < 10)
                myArray[i] = i;
            else if (i < 20)
                myArray[i] = 2*i;
            else if (i < 28)
                myArray[i] = 2*i + 1;
            else
                myArray[i] = 2*i;
        }


        launchHabaneroApp(() -> {
            resultPar[0] = selec.evenPairsWithSelector(myArray);
        });

        for (int i = 0; i < resultPar[0].size(); i++){
            int x = resultPar[0].get(i)[0];
            int y = resultPar[0].get(i)[1];
            System.out.println(x + " + " + y + " = " + (x+y));
            assertTrue("Sum of pair should be even, actual sum of " + x + " and " + y + " is " + (x + y),
                    (x+y) % 2 == 0);
        }

        System.out.println("Selector test ends.");

    }

    // PHASERS TEST

    public void testPhaser() {
        System.out.println("Starting phaser test...");

        launchHabaneroApp(() -> {
            ph.synchronizedPrintingWithPhasers();
        });

        System.out.println("Phaser test ends.");
    }

    // PLACES/REGIONS TESTS

    public void testPlace() {
        System.out.println("Starting place test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementSequential(intArray);
            results[1] = pr.incrementWithPlace(intArray);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Place test ends.");
    }

    public void testRegions() {
        System.out.println("Starting regions test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementSequential(intArray);
            results[1] = pr.incrementWithIterationRegion(intArray);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Regions test ends.");
    }

    // ISOLATED TESTS

    public void testIsolated() {
        System.out.println("Starting isolated test...");

        final int[] results = new int[2];

        launchHabaneroApp(() -> {
            results[0] = check.sumArraySequential(intArray);
            results[1] = iso.sumWithIsolation(intArray);
        });

        assertTrue("Sum: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Isolated test ends.");
    }

    public void testIsolatedWithReturnValue() {
        System.out.println("Starting isolated with return value test...");

        final int[] results = new int[2];

        launchHabaneroApp(() -> {
            results[0] = check.sumArraySequential(intArray);
            results[1] = iso.sumWithIsolationReturnValue(intArray);
        });

        assertTrue("Sum: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Isolated with return value test ends.");
    }

    // FUTURE EXAMPLES

    public void testFuture() {
        System.out.println("Starting future test...");

        final int[] results = new int[2];

        launchHabaneroApp(() -> {
            results[0] = check.sumArraySequential(intArray);
            results[1] = fut.sumWithFuture(intArray);
        });

        assertTrue("Sum: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Future test ends.");
    }

    public void testDataDrivenFuture() {
        System.out.println("Starting data driven future test...");

        final int[] results = new int[2];

        launchHabaneroApp(() -> {
            results[0] = check.sumArraySequential(intArray);
            results[1] = fut.sumWithDataDrivenFuture(intArray);
        });

        assertTrue("Sum: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Data driven future test ends.");
    }

    // FOR LOOP TESTS

    public void testForall() {
        System.out.println("Starting forall test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementSequential(intArray);
            results[1] = fl.incrementWithForAll(intArray);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Forall test ends.");
    }

    public void testForasync() {
        System.out.println("Starting forasync test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementSequential(intArray);
            results[1] = fl.incrementWithForAsync(intArray);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Forasync test ends.");
    }

    public void testForallChunked() {
        System.out.println("Starting forallChunked test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementSequential(intArray);
            results[1] = fl.incrementWithForAllChunked(intArray, numChunks);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("ForallChunked test ends.");
    }

    public void testForasyncChunked() {
        System.out.println("Starting forasyncChunked test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementSequential(intArray);
            results[1] = fl.incrementWithForAsyncChunked(intArray, numChunks);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("ForasyncChunked test ends.");
    }

    public void testForseq() {
        System.out.println("Starting forseq test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementSequential(intArray);
            results[1] = fl.incrementWithForSeq(intArray);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Forseq test ends.");
    }

    public void testForallPhased() {
        System.out.println("Starting forallPhased test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementTwiceSequential(intArray);
            results[1] = fl.incrementTwiceWithForAllPhased(intArray);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("ForallPhased test ends.");
    }

    public void testForasyncPhased() {
        System.out.println("Starting forasyncPhased test...");

        final int[][] results = new int[2][];

        launchHabaneroApp(() -> {
            results[0] = check.incrementTwiceSequential(intArray);
            results[1] = fl.incrementTwiceWithForAsyncPhased(intArray);
        });

        assertTrue("Increment: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("ForasyncPhased test ends.");
    }

    // FINISH ACCUMULATORS TEST

    public void testFinishAccumulator() {
        System.out.println("Starting finish accumulator test...");

        final int[] results = new int [2];

        launchHabaneroApp(() -> {
            results[0] = check.sumSequential(0, 10);
            results[1] = fa.sumWithFinishAccumulator(0, 10);
        });

        assertTrue("Sum: Expected = " + results[0] + ", Actual = " + results[1], results[1] == results[0]);

        System.out.println("Finish accumulator test ends.");
    }

}
