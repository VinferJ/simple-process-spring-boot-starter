package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-28 11:38
 * @see AbstractStatefulRunnable
 */
public interface RunnableStateCallback {

    /**
     * callback before running.
     */
    default void onRunning() {}

    /**
     * callback after running.
     */
    default void onFinished() {}

    /**
     * callback after occurring error in running.
     * @param t     error
     */
    default void onExited(Throwable t) {}

    /**
     * callback after resuming the state as {@link StatefulRunnable#STATE_CREATED}
     * @param oldState      the old state before resuming
     */
    default void onResumeState(int oldState) {}

}
