package UnitTests;

import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module2.*;

/*
 * Created by Alitha on 6/18/15.
 */
public class IsolatedExample {
    // Uses: isolated
    public int sumWithIsolation(final int[] myArray) throws SuspendableException {
        final int[] sum = {0};
        forall(0, myArray.length - 1, (i) -> {
            // isolated blocks do not run in parallel with each other
            isolated(() -> {
                sum[0] += myArray[i];
            });
        });

        return sum[0];
    }

    // Uses: isolatedWithReturn
    public int sumWithIsolationWithReturnValue(final int[] myArray) throws SuspendableException {
        final int[] sum = {0, 0};

        finish(() -> {
            async(() -> {
                // a regular isolated block that returns a value
                sum[0] = isolatedWithReturn(() -> {
                    int newsum = 0;
                    for (int i = 0; i < myArray.length / 2; i++) {
                        newsum += myArray[i];
                    }
                    return newsum;
                });
            });
            sum[1] = isolatedWithReturn(() -> {
                int newsum = 0;
                for (int i = myArray.length / 2; i < myArray.length; i++) {
                    newsum += myArray[i];
                }
                return newsum;
            });
        });

        return sum[0] + sum[1];
    }
}
