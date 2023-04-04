package me.vinfer.simpleprocess.core;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vinfer
 * @date 2023-03-31 15:33
 */
@Slf4j
public class PlainProcessChain extends ProcessNodeAbstract implements ProcessChain {

    private final ProcessNode root;

    public PlainProcessChain(String processId, ProcessNode root) {
        this.root = root;
        setProcessId(processId);
    }

    @Override
    public void execute() {
        if (null == root) {
            return;
        }

        ProcessNode runNode = root;
        while (null != runNode) {
            if (runNode instanceof AbstractStatefulProcessNode) {
                log.warn("should not add a stateful node into a plain process chain, " +
                        "you should use the StatefulProcessChain to build it");
                // will auto resume the state if allow the stateful node running
                if (allowStatefulNode()) {
                    AbstractStatefulProcessNode statefulNode = (AbstractStatefulProcessNode) runNode;
                    if (!statefulNode.isCreated()) {
                        statefulNode.resumeState();
                    }
                }else {
                    throw new UnsupportedOperationException("Not allow stateful node running in a plain process chain");
                }
            }
            runNode.execute();
            runNode = runNode.next();
        }

        // run self next node
        if (next() != null) {
            next().execute();
        }

    }

    @Override
    public void setNext(ProcessNode node) {
        if (node instanceof PlainProcessChain && node != this) {
            super.setNext(node);
        }
        throw new IllegalArgumentException("Only support set another PlainProcessChain type node as next node");
    }

    @Override
    public ProcessNode getRoot() {
        return root;
    }

    protected boolean allowStatefulNode() {
        return true;
    }

}
