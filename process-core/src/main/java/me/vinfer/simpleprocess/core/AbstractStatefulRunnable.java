package me.vinfer.simpleprocess.core;


import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vinfer
 * @date 2023-03-28 11:47
 */
@Slf4j
public abstract class AbstractStatefulRunnable implements StatefulRunnable {

    private static final VarHandle STATE;

    private volatile int state = STATE_CREATED;

    private final List<RunnableStateCallback> stateCallbacks;

    static {
        MethodHandles.Lookup l = MethodHandles.lookup();
        try {
            STATE = l.findVarHandle(AbstractStatefulRunnable.class, "state", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public AbstractStatefulRunnable(List<RunnableStateCallback> stateCallbacks) {
        this.stateCallbacks = stateCallbacks;
    }

    public AbstractStatefulRunnable(String runnableId) {
        this(new ArrayList<>());
    }

    @Override
    public final void run() {
        try {
            if (isRunning()) {
                return;
            }

            boolean started = STATE.compareAndSet(this, STATE_CREATED, STATE_RUNNING);
            if (!started) {
                // must resume the state
                throw new IllegalStateException("Start failed, illegal state: ["+mappingState()+"]" + ", " +
                        "you can resume the state if try to run again");
            }

            onRunning();
            runInternal();

            boolean finished = STATE.compareAndSet(this, STATE_RUNNING, STATE_FINISHED);
            if (finished) {
                onFinished();
            }
        } catch (IllegalStateException e) {
            throw e;
        }
        catch (Throwable t) {
            setState(STATE_EXITED);
            onExited(t);
            throw t;
        }
    }

    /**
     * resume the state as {@link #STATE_CREATED} to support run again.
     * @throws IllegalStateException if try to resume a running process
     */
    public void resumeState() throws IllegalStateException{
        if (isCreated()) {
            return;
        }
        if (isRunning()) {
            throw new IllegalStateException("Cannot resume state when process is running");
        }
        int oldState = getState();
        setState(STATE_CREATED);
        onResumeState(oldState);
    }

    @Override
    public int getState() {
        return state;
    }

    public List<RunnableStateCallback> getStateCallbacks() {
        return stateCallbacks;
    }

    public void addStateCallback(RunnableStateCallback callback) {
        stateCallbacks.add(callback);
    }

    void onRunning() {
        if (CollectionUtil.isNotEmpty(stateCallbacks)) {
            stateCallbacks.forEach(RunnableStateCallback::onRunning);
        }
    }

    void onFinished() {
        if (CollectionUtil.isNotEmpty(stateCallbacks)) {
            stateCallbacks.forEach(RunnableStateCallback::onFinished);
        }
    }

    void onExited(Throwable t) {
        if (CollectionUtil.isNotEmpty(stateCallbacks)) {
            stateCallbacks.forEach(callback -> callback.onExited(t));
        }
    }

    void onResumeState(int oldState) {
        if (CollectionUtil.isNotEmpty(stateCallbacks)) {
            stateCallbacks.forEach(callback -> callback.onResumeState(oldState));
        }
    }

    protected final void setState(int state) {
        this.state = state;
    }

    protected abstract void runInternal();

}
