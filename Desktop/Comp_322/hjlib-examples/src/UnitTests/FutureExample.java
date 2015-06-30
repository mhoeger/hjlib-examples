package UnitTests;

import edu.rice.hj.api.HjDataDrivenFuture;
import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.SuspendableException;
import static edu.rice.hj.Module1.*;

/*
 * Created by Alitha on 6/18/15.
 */
public class FutureExample {
    // Uses: HjFuture, future
    public int sumWithFuture (final int[] myArray) throws SuspendableException {
        final int[] totalSum = {0};

        finish(() -> {
            // add the first half of the array
            HjFuture<Integer> sum1 = future(() -> {
                int sum = 0;
                for (int i = 0; i < myArray.length / 2; i++) {
                    sum += myArray[i];
                }
                return sum;
            });

            // add the second half of the array
            HjFuture<Integer> sum2 = future(() -> {
                int sum = 0;
                for (int i = myArray.length / 2; i < myArray.length; i++) {
                    sum += myArray[i];
                }
                return sum;
            });

            // sum1 and sum2 may run in parallel and the following statement will block until both are completed
            async(() -> {
                totalSum[0] = sum1.get() + sum2.get();
            });
        });

        return totalSum[0];
    }

    // Uses: HjDataDrivenFuture, newDataDrivenFuture()
    public int sumWithDataDrivenFuture() throws SuspendableException {
        final int[] myArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        HjDataDrivenFuture<Integer> sum1 = newDataDrivenFuture();
        HjDataDrivenFuture<Integer> sum2 = newDataDrivenFuture();
        final int[] totalSum = {0};

        finish(() -> {
            // add the first half of the array
            async(() -> {
                int mySum = 0;
                for (int i = 0; i < myArray.length / 2; i++) {
                    mySum += myArray[i];
                }
                sum1.put(mySum);
            });

            // add the second half of the array
            async(() -> {
                int mySum = 0;
                for (int i = myArray.length / 2; i < myArray.length; i++) {
                    mySum += myArray[i];
                }
                sum2.put(mySum);
            });

            // the following block only starts executing when sum1 and sum2 are available,
            // so it waits until the first two asyncs are completed
            asyncAwait(sum1, sum2, () -> {
                totalSum[0] = sum1.get() + sum2.get();
            });
        });

        return totalSum[0];
    }
}
