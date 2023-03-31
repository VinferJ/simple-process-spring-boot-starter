package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-28 11:47
 */
public interface StatefulRunnable extends Runnable{

    /**
     * created but not start.
     */
    int STATE_CREATED = 0;

    /**
     * in running.
     */
    int STATE_RUNNING = 1;

    /**
     * execution finished normally.
     */
    int STATE_FINISHED = 3;

    /**
     * execution exited in some error circumstance in running.
     */
    int STATE_EXITED = 4;

    int getState();

    default boolean isCreated() {
        return getState() == STATE_CREATED;
    }

    default boolean isRunning() {
        return getState() == STATE_RUNNING;
    }

    default boolean isFinished() {
        return getState() == STATE_FINISHED;
    }

    default boolean isExited() {
        return getState() == STATE_EXITED;
    }

    default String mappingState() {
        return mappingState(getState());
    }

    static String mappingState(int state) {
        switch (state) {
            case STATE_CREATED: return "CREATED";
            case STATE_RUNNING: return "RUNNING";
            case STATE_FINISHED: return "FINISHED";
            case STATE_EXITED: return "EXITED";
        }
        throw new IllegalArgumentException("Unknown State: ["+ state +"]");
    }

}
