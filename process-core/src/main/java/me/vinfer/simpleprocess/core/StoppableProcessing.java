package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-27 16:52
 */
public interface StoppableProcessing {

    // TODO: 2023/3/31 use interruptable design base on ProcessChain.
    //  use check point mechanism to allow the interrupt operation before each ProcessNode execution,
    //  this interrupt operation can be a thread-wait operation, or just record the state and return in advance.

    /**
     * start processing, which can only be called once.
     */
    void start();

    /**
     * park the current thread to stop processing.
     * <p> process must be started before call this method.
     *
     * @throws IllegalStateException if try to stop a not started processing
     */
    void stop() throws IllegalStateException;

    /**
     * un-park the current thread to resume the processing.
     * <p> must call {@link #stop()} first then call this method.
     *
     * @throws IllegalStateException if try to resume a not stopped processing
     */
    void resume() throws IllegalStateException;

}
