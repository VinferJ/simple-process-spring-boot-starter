package me.vinfer.simpleprocess.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * a process node supports the state management.
 * @author vinfer
 * @date 2023-03-27 15:41
 * @see AbstractStatefulRunnable
 */
@Slf4j
public abstract class AbstractStatefulProcessNode extends AbstractStatefulRunnable implements ProcessNode {

    private final String processId;
    private String description;
    private ProcessNode next;
    private Integer order;

    // TODO: 2023/3/31 when this mechanism is introduced, it should be necessary for the process chain to have the ability to recover the running state.
    //  it may be possible to provide this above ability by doing another abstraction layer.
    //  we can use the callback mechanism to build a execution layer to provide this recover ability.

    public AbstractStatefulProcessNode(String processId, List<RunnableStateCallback> stateCallbacks) {
        super(stateCallbacks);
        this.processId = processId;
        addStateCallback(new InternalLoggerStateCallback(this::getProcessId));
    }

    public AbstractStatefulProcessNode(String processId) {
        this(processId, new ArrayList<>());
    }

    static class InternalLoggerStateCallback implements RunnableStateCallback {
        private final Supplier<String> processIdProvider;

        InternalLoggerStateCallback(Supplier<String> processIdProvider) {
            this.processIdProvider = processIdProvider;
        }

        @Override
        public void onRunning() {
            log.info("AbstractStatefulProcessNode process start, id: {}", processIdProvider.get());
        }

        @Override
        public void onFinished() {
            log.info("AbstractStatefulProcessNode process finished, id: {}", processIdProvider.get());
        }

        @Override
        public void onExited(Throwable t) {
            log.info("AbstractStatefulProcessNode process exited, id: {}", processIdProvider.get());
            log.error("AbstractStatefulProcessNode process with id ["+processIdProvider.get()+"] occurs error: ", t);
        }

        @Override
        public void onResumeState(int oldState) {
            log.info("AbstractStatefulProcessNode process [{}] resume state from {} to {}",
                    processIdProvider.get(), StatefulRunnable.mappingState(oldState), StatefulRunnable.mappingState(STATE_CREATED));
        }
    }

    @Override
    public void start() {
        run();
    }

    @Override
    public ProcessNode next() {
        return next;
    }

    @Override
    public void setNext(ProcessNode next) {
        this.next = next;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        if (null == order) {
            return ProcessNode.super.getOrder();
        }
        return order;
    }
}
