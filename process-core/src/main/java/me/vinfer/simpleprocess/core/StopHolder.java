package me.vinfer.simpleprocess.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @author vinfer
 * @date 2023-03-27 17:00
 */
public class StopHolder {

    private final Thread targetThread;

    private final AtomicBoolean stopped = new AtomicBoolean(false);

    public StopHolder(Thread targetThread) {
        this.targetThread = targetThread;
    }

    public StopHolder() {
        this(Thread.currentThread());
    }

    /**
     * stop the target thread which maintain by this instance.
     * <p> the caller must execute in the same thread as the target thread
     */
    public void stop() {
        if (stopped.get()) {
            return;
        }
        if (isStoppable() && stopped.compareAndSet(false, true)) {
            LockSupport.park();
            return;
        }
        // only allow the thread which create this object instance to call this method
        throw new UnsupportedOperationException("Current thread is not equals to target thread ["+targetThread.getName()+"]");
    }

    /**
     * resume the stopped thread, the caller must execute in a difference thread from the target thread
     */
    public void resume() {
        if (stopped.get()) {
            LockSupport.unpark(targetThread);
            stopped.set(false);
        }
    }

    public boolean isStoppable() {
        return Thread.currentThread().equals(targetThread);
    }

}
