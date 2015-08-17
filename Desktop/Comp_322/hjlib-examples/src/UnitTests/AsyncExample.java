package UnitTests;

import edu.rice.hj.api.HjPhaser;
import edu.rice.hj.api.HjPhaserMode;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.newPhaser;
import static edu.rice.hj.Module0.asyncPhased;
import static edu.rice.hj.Module0.next;
import static edu.rice.hj.Module1.async;

/**
 * Created by Marie on 7/3/15.
 */
public class AsyncExample {

    private final SequentialCheck seq = new SequentialCheck();

    // Uses: async, finish
    public int sumWithAsyncFinish(int start, int end) throws SuspendableException {
        final int[] sum = new int[2];
        finish(() -> {
            final int mid = (end - start) / 2;
            async(() -> {
                sum[0] = seq.sumSequential(start, mid);
            });
            if (mid > start) {
                sum[1] = seq.sumSequential(mid + 1, end);
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
                chunkSums[0] = seq.sumSequential(1,5);
                next();     // Signal that self is ready, continue to phase 2 without waiting
                resultSums[0] = chunkSums[0];
            });
            asyncPhased(ph.inMode(HjPhaserMode.SIG_WAIT), () -> {
                chunkSums[1] = seq.sumSequential(6,10);
                next();        // Signal that self is ready, waits on A1 to signal
                resultSums[1] = chunkSums[1] + chunkSums[0];
            });
            asyncPhased(ph.inMode(HjPhaserMode.WAIT), () -> {
                chunkSums[2] = seq.sumSequential(11,15);
                next();     // Waits on signal from A1 and A2
                resultSums[2] = chunkSums[2] + chunkSums[1] + chunkSums[0];
            });
        });
        return resultSums;
    }
}
