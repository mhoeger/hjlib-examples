package UnitTests;

import edu.rice.hj.api.*;

import static edu.rice.hj.Module1.*;

/*
 * Created by Alitha on 6/26/15.
 */
public class ForLoopExample {
    // Uses: forall
    public int[] incrementWithForAll(final int[] myArray) throws SuspendableException {
        forall(0, myArray.length - 1, (i) -> {
            myArray[i] += 1;
        });

        return myArray;
    }

    // Uses: forasync
    public int[] incrementWithForAsync(final int[] myArray) throws SuspendableException {
        finish(() -> {
            forasync(0, myArray.length - 1, (i) -> {
                myArray[i] += 1;
            });
        });

        return myArray;
    }

    // Uses: forallChunked
    public int[] incrementWithForAllChunked(final int[] myArray, int numChunks) throws SuspendableException {
        forallChunked(0, myArray.length - 1, numChunks, (i) -> {
            myArray[i] += 1;
        });

        return myArray;
    }

    // Uses: forasyncChunked
    public int[] incrementWithForAsyncChunked(final int[] myArray, int numChunks) throws SuspendableException {
        finish(() -> {
            forasyncChunked(0, myArray.length - 1, numChunks, (i) -> {
                myArray[i] += 1;
            });
        });

        return myArray;
    }

    // Uses: forseq
    public int[] incrementWithForSeq(final int[] myArray) throws SuspendableException {
        // semantically the same as a regular for loop
        forseq(0, myArray.length - 1, (i) -> {
            myArray[i] += 1;
        });

        return myArray;
    }

    // Uses: forallPhased, next
    public int[] incrementTwiceWithForAllPhased(final int[] myArray) throws SuspendableException {
        forallPhased(0, myArray.length - 1, (i) -> {
            myArray[i] += 1;
            next(); // barrier; does not advance until all iterations finish executing the above statement
            myArray[i] += 1;
        });

        return myArray;
    }

    // Uses: forasyncPhased, signal
    public int[] incrementTwiceWithForAsyncPhased(final int[] myArray) throws SuspendableException {
        finish(() -> {
            forasyncPhased(0, myArray.length - 1, (i) -> {
                myArray[i] += 1;
                if (i == 0) {
                    signal();
                    doWork(10); // this work happens while waiting for the other iterations to reach next()
                }
                next(); // barrier; does not advance until all iterations finish executing the above statement
                myArray[i] += 1;
            });
        });

        return myArray;
    }
}
