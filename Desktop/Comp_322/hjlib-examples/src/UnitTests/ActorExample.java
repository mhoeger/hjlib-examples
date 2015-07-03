package UnitTests;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.actors.Actor;

import static edu.rice.hj.Module0.finish;

/**
 * Created by Marie on 7/3/15.
 */
public class ActorExample {

    public void startActors() throws SuspendableException {
        finish (() -> {
            MasterActor driverActor = new MasterActor(5);
            driverActor.start();
            for (int i = 0; i < 50; i++) {
                driverActor.process(Integer.valueOf(i));
                if (i % 10 == 9)
                    driverActor.changeTag();
            }
            driverActor.process(TaggingActor.STOP_MSG);
        });
    }

    private class MasterActor extends Actor<Object> {

        private TaggingActor[] workerActors;
        private int messageCount, numWorkers, tag;

        MasterActor(int numWorkers) {
            super();
            this.numWorkers = numWorkers;
            workerActors = new TaggingActor[numWorkers];
            messageCount = 0;
            tag = 65;

            // Instantiate and start worker actors
            for (int i = 0; i < numWorkers; i++){
                workerActors[i] = new TaggingActor();
                workerActors[i].start();
            }
        }

        @Override
        protected void process(final Object msg) {
            // Terminate an actor using special a stop message
            if (TaggingActor.STOP_MSG.equals(msg)) {
                if (workerActors != null) {
                    for (TaggingActor actor : workerActors)
                        actor.process(msg);
                }
                exit(); // never forget to terminate an actor
                // Distribute work equally to actors
            } else if (msg instanceof Integer) {
                System.out.println("here" + msg);
                workerActors[messageCount % numWorkers].process(msg);
                messageCount++;

                // Output an error message for invalid input
            } else {
                System.out.println("Invalid input '" + msg +
                        "' of non-Integer type " + msg.getClass().getName());
            }
        }

        protected void changeTag(){
            for (TaggingActor actor : workerActors){
                actor.pause();
            }
            TaggingActor.tag = (char) ++tag + "";
            if (tag > 90)
                tag = 65;
            System.out.println("resuming...");

//            for (int i = 0; i < numWorkers; i++){
//                System.out.println("starting" + i);
//                workerActors[i].resume();
//            }
            System.out.println("done resuming");
        }
    }

    private static class TaggingActor extends Actor<Object> {
        static final Object STOP_MSG = Integer.MAX_VALUE;
        static String tag = "A";
        private String message = "";

        @Override
        protected void process(final Object msg) {
            // Terminate if stop message
            if (TaggingActor.STOP_MSG.equals(msg)) {
                System.out.println("Exiting Actor: " + message);
                exit(); // never forget to terminate an actor
                // Process Integer message
            } else if (msg instanceof Integer) {
                message += tag + msg + "\t";
                System.out.println(message);
                // Output an error message for invalid input
            } else {
                System.out.println("Invalid input '" + msg +
                        "' of non-Integer type " + msg.getClass().getName());
            }
        }

    }


}
