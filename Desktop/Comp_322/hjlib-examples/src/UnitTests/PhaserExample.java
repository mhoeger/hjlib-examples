package UnitTests;

import java.util.*;

import edu.rice.hj.api.HjPhaser;
import edu.rice.hj.api.HjPhaserMode;
import edu.rice.hj.api.HjPhaserPair;
import edu.rice.hj.api.SuspendableException;
import static edu.rice.hj.Module0.*;

/*
 * Created by Alitha on 6/18/15.
 */
public class PhaserExample {
    // Uses: newPhaser, HjPhaser, HjPhaserMode, HjPhaserPair, asyncPhased
    public void synchronizedPrintingWithPhasers () throws SuspendableException {
        finish(() -> {
            final HjPhaser[] phasers = new HjPhaser[10];

            // creating new phasers
            for (int i = 0; i < 10; i++) {
                phasers[i] = newPhaser(HjPhaserMode.SIG_WAIT);
            }

            for (int j = 1; j < 9; j++) {
                // if registering more than two phasers on an asyncPhased, put them into a List<>
                final List<HjPhaserPair> phaserList = Arrays.asList(
                        phasers[j - 1].inMode(HjPhaserMode.WAIT),
                        phasers[j].inMode(HjPhaserMode.SIG),
                        phasers[j + 1].inMode(HjPhaserMode.WAIT)
                );

                asyncPhased(phaserList, () -> {
                    System.out.println("Before next");
                    next(); // signals phasers[j]; waits on phasers[j - 1] & phasers[j + 1]
                    System.out.println("After next");
                });
            }
        });
    }
}
