package UnitTests;

import edu.rice.hj.api.HjFinishAccumulator;
import edu.rice.hj.api.HjOperator;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module1.*;

/*
 * Created by Alitha on 6/26/15.
 */
public class FinishAccumulatorExample {
    // Uses: HjFinishAccumulator
    public int sumWithFinishAccumulator() throws SuspendableException {
        // declare a finish accumulator that uses a sum operation with an int data type
        final HjFinishAccumulator acc = newFinishAccumulator(HjOperator.SUM, int.class);

        // must register accumulator to a finish scope
        finish(acc, () -> {
            async(() -> {
                for (int i = 0; i < 10; i++) {
                    acc.put(i);
                }
            });
            for (int i = 0; i < 10; i++) {
                acc.put(i);
            }
        });

        return (int)acc.get();
    }
}
