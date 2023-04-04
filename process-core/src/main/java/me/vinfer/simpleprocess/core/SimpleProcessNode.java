package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-28 15:08
 */
public class SimpleProcessNode extends ProcessNodeAbstract implements ProcessNode{

    private final Runnable operation;

    public SimpleProcessNode(Runnable operation) {
        this.operation = operation;
    }

    public SimpleProcessNode(Runnable operation, String processId) {
        this.operation = operation;
        setProcessId(processId);
    }

    public SimpleProcessNode(Runnable operation,
                             String processId,
                             String description,
                             ProcessNode next,
                             Integer order) {
        this.operation = operation;
        setProcessId(processId);
        setDescription(description);
        setNext(next);
        setOrder(order);
    }

    @Override
    public void execute() {
        if (null == operation) {
            return;
        }
        operation.run();
    }

}
