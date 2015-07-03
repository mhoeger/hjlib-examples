package UnitTests;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.experimental.ModuleZ;
import edu.rice.hj.experimental.api.*;

import java.util.List;

import static edu.rice.hj.Module1.async;
import static edu.rice.hj.experimental.ModuleZ.*;
import static edu.rice.hj.experimental.ModuleZ.newEngineEureka;

/**
 * Created by Marie on 7/3/15.
 */
public class EurekaExample {

    private final int numChunks = 2;
    /*
     * Find the index that contains searchHit in dataArray. If searchHit is present multiple times,
     * the answer may vary between runs.
     * Uses: newSearchEureka, HjSearchEureka
    */
    public Integer searchWithEureka(final char[] dataArray, final char[] searchHit) throws SuspendableException {
        final HjSearchEureka<Integer> resultEureka = newSearchEureka(null);
        boolean checkAgainstNull = true;
        return (Integer) searchWithHjEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    /*
     * Find the index of the last occurrence of searchHit in dataArray
     * Uses: newExtremaEureka, HjExtremaEureka
    */
    public Integer searchMaximumWithEureka(final char[] dataArray, final char[] searchHit) throws SuspendableException {
        final HjExtremaEureka<Integer> resultEureka = newExtremaEureka(Integer.MIN_VALUE, (Integer i, Integer j) -> i.compareTo(j), false);
        boolean checkAgainstNull = false;
        return (Integer) searchWithHjEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    /*
     * Find the index of the first occurrence of searchHit in dataArray
     * Uses: newExtremaEureka, HjExtremaEureka
    */
    public Integer searchMinimumWithEureka(final char[] dataArray, final char[] searchHit) throws SuspendableException {
        final HjExtremaEureka<Integer> resultEureka = newExtremaEureka(Integer.MAX_VALUE, (Integer i, Integer j) -> j.compareTo(i));
        boolean checkAgainstNull = false;
        return (Integer) searchWithHjEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }


    /*
     * Finds n indices that contain searchHit in dataArray, where n is equal to count. If there are more than n correct
     * answers, the output may vary between runs.
     * Uses: newCountEureka, HjCountEureka
    */
    public List<Integer> searchWithCountEureka(final char[] dataArray, final int count, final char [] searchHit) throws SuspendableException {
        final HjCountEureka<Integer> resultEureka = newCountEureka(count, count);
        boolean checkAgainstNull = true;
        return (List<Integer>) searchWithHjEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    /*
     * Searches for the index that contains searchHit in dataArray for n milliseconds, where n is "time". If an answer
     * is not present or not found due to early termination, a null value is returned. If searchHit is present multiple
     * times, the answer may vary between runs.
     * Uses: newTimerEureka, HjTimerEureka
    */
    public Integer searchWithTimerEureka(final char[] dataArray, final char[] searchHit, final int time) throws SuspendableException {
        final HjTimerEureka<Integer> resultEureka = newTimerEureka(time);
        boolean checkAgainstNull = true;
        return (Integer) searchWithHjEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }

    /*
    * Searches for the index that contains searchHit in dataArray for n units of time, where n is "time". If an answer
    * is not present or not found due to early termination, a null value is returned. If searchHit is present multiple
    * times, the answer may vary between runs.
    * Uses: newEngineEureka, HjEngineEureka
   */
    public Integer searchWithEngineEureka(final char[] dataArray, final char[] searchHit, final int time) throws SuspendableException {
        final HjEngineEureka<Integer> resultEureka = newEngineEureka(time);
        boolean checkAgainstNull = true;
        return (Integer) searchWithHjEureka(dataArray, searchHit, resultEureka, checkAgainstNull);
    }


    /*
    * Searches for searchHit in dataArray according to the behavior of resultEureka (see previous comments). If
    * checkAgainstNull is true, resultEureka.check() should check a null value to produce expected behavior. Otherwise,
    * the offer value should be used.
    * Uses:  HjEUreka, finish(eureka), async
   */
    public Object searchWithHjEureka(final char[] dataArray, final char[] searchHit, final HjEureka resultEureka, boolean checkAgainstNull) throws SuspendableException {
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

}
