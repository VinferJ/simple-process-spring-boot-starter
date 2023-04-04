package me.vinfer.simpleprocess.core;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * @author vinfer
 * @date 2023-03-27 15:39
 */
@Slf4j
public class StatefulProcessChain extends AbstractStatefulProcessNode
        implements ProcessChain, StoppableProcessing {

    private final ProcessNode root;

    private final AtomicInteger flowState = new AtomicInteger(STATE_CREATED);

    private final StopHolder stopHolder;

    private ProcessNode currentRunNode;

    public StatefulProcessChain(String processId, ProcessNode root) {
        super(processId);
        Assert.hasText(processId, "processId must has text");
        this.root = root;
        this.stopHolder = new StopHolder();
    }

    @Override
    protected void runInternal() {
        if (!flowState.compareAndSet(STATE_CREATED, STATE_RUNNING)) {
            return;
        }

        setCurrentRunNode(root);

        while (getCurrentRunNode() != null) {
            ProcessNode current = getCurrentRunNode();
            onCheckPoint(current);
            current.execute();
            setCurrentRunNode(current.next());
        }

    }

    private void onCheckPoint(ProcessNode current) {
        if (isStopped()) {
            stopInternal(current);
        }
    }

    private boolean isStopped() {
        return flowState.get() > STATE_RUNNING;
    }

    private ProcessNode getCurrentRunNode() {
        return currentRunNode;
    }

    private void setCurrentRunNode(ProcessNode node) {
        this.currentRunNode = node;
    }

    @Override
    public void setNext(ProcessNode node) {
        throw new UnsupportedOperationException("Cannot set a next node in this process chain");
    }

    @Override
    public void resumeState() {
        super.resumeState();
        resumeStateInternal();
    }

    private void resumeStateInternal() {
        flowState.set(STATE_CREATED);

        setCurrentRunNode(root);

        // refresh all node's state, begin from root node
        doResumeStateInternal(root);
    }

    private void doResumeStateInternal(ProcessNode node) {
        while (null != node) {
            // handle plain node
            if (node instanceof AbstractStatefulProcessNode) {
                ((AbstractStatefulProcessNode)node).resumeState();
            }

            // handle condition node
            if (node instanceof ConditionProcessNode) {
                ConditionProcessNode conditionNode = (ConditionProcessNode) node;
                ProcessNode matchedNode = conditionNode.getConditionMatchedNode();
                ProcessNode unmatchedNode = conditionNode.getConditionUnmatchedNode();

                if (matchedNode instanceof AbstractStatefulProcessNode) {
                    doResumeStateInternal(matchedNode);
                }

                if (unmatchedNode instanceof AbstractStatefulProcessNode) {
                    doResumeStateInternal(unmatchedNode);
                }
            }

            node = node.next();
        }
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            throw new IllegalStateException("Not Running, Current State is: ["+mappingState()+"]");
        }

        if (isStopped()) {
            throw new IllegalStateException("Stopped Already");
        }

        flowState.incrementAndGet();
    }

    private void stopInternal(ProcessNode stoppedNode) {
        log.info("Processing stopped, stopped node processId: {}", stoppedNode.getProcessId());
        stopHolder.stop();
    }

    @Override
    public void resume() {
        if (!isStopped()) {
            throw new IllegalStateException("Not Stopped");
        }

        flowState.set(STATE_RUNNING);

        restartInternal();
    }

    private void restartInternal() {
        log.info("Processing resume, recover node processId: {}", getCurrentRunNode().getProcessId());
        stopHolder.resume();
    }

    @Override
    public ProcessNode getRoot() {
        return root;
    }
}
