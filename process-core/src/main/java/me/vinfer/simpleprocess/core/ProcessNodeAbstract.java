package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-28 16:35
 */
public abstract class ProcessNodeAbstract implements ProcessNode{

    private String processId;
    private String description;
    private ProcessNode next;
    private Integer order;

    @Override
    public ProcessNode next() {
        return next;
    }

    @Override
    public void setNext(ProcessNode node) {
        this.next = node;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    @Override
    public String getDescription() {
        if (null != description) {
            return description;
        }
        return ProcessNode.super.getDescription();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getOrder() {
        if (null != order) {
            return order;
        }
        return ProcessNode.super.getOrder();
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

}
