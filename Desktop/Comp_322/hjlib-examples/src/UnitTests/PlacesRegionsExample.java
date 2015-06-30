package UnitTests;

import edu.rice.hj.api.HjRegion;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;

import java.util.Arrays;

import static edu.rice.hj.Module1.*;

/*
 * Created by Alitha on 6/18/15.
 */
public class PlacesRegionsExample {
    // Uses: place, asyncNbAt
    public int[] incrementWithPlace(final int[] myArray) throws SuspendableException {
        finish(() -> {
            for (int i = 0; i < myArray.length; i++) {
                final int ii = i;
                // use async with a place specified
                // note: place(0) == here(); in this case, this asyncNbAt is the same as regular async
                asyncNbAt(place(0), () -> {
                    myArray[ii] += 1;
                });
            }
        });

        return myArray;
    }

    // Uses: HjRegion, newRectangularRegion1D
    public int[] incrementWithIterationRegion(final int[] myArray) throws SuspendableException {
        // create a new one-dimensional region with the array length as its size
        HjRegion.HjRegion1D myRegion = newRectangularRegion1D(0, myArray.length - 1);
        // forallChunked automatically divides up the region into chunks that is run in parallel
        forallChunked(myRegion, (i) -> {
            myArray[i] += 1;
        });

        return myArray;
    }
}
