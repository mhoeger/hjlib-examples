package UnitTests;

import edu.rice.hj.api.HjFinishAccumulator;
import edu.rice.hj.api.HjOperator;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module1.finish;
import static edu.rice.hj.Module1.async;
import static edu.rice.hj.Module1.newFinishAccumulator;

/*
 * Created by Alitha on 6/26/15.
 */
public class FinishAccumulatorExample {
    // Uses: HjFinishAccumulator
    public int sumWithFinishAccumulator(int start, int end) throws SuspendableException {
        // declare a finish accumulator that uses a sum operation with an int data type
        final HjFinishAccumulator acc = newFinishAccumulator(HjOperator.SUM, int.class);

        // must register accumulator to a finish scope
        finish(acc, () -> {
            async(() -> {
                for (int i = start; i < (start + end) / 2; i++) {
                    acc.put(i);
                }
            });
            for (int i = (start + end) / 2; i <= end; i++) {
                acc.put(i);
            }
        });

        return (int)acc.get();
    }
}
