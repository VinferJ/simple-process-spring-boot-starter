package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-31 15:33
 */
public class PlainProcessChain extends ProcessNodeAbstract implements ProcessChain {

    private final ProcessNode root;

    public PlainProcessChain(String processId, ProcessNode root) {
        this.root = root;
        setProcessId(processId);
    }

    @Override
    public void start() {
        if (null == root) {
            return;
        }

        ProcessNode runNode = root;
        while (null != runNode) {
            runNode.start();
            runNode = runNode.next();
        }

        // run self next node
        if (next() != null) {
            next().start();
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

}
