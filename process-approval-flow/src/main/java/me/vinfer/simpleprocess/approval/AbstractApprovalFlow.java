package me.vinfer.simpleprocess.approval;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * @author vinfer
 * @date 2023-04-03 15:41
 */
@Slf4j
public abstract class AbstractApprovalFlow implements ApprovalFlow{

    private final String flowId;
    private final String flowType;
    private Status status;
    private final ApprovalNode rootNode;
    private final List<ApprovalFlowExecutionCallback> flowExecutionCallbacks;


    public AbstractApprovalFlow(String flowId,
                                String flowType,
                                Status initStatus,
                                ApprovalNode rootNode) {
        this.flowId = flowId;
        this.flowType = flowType;
        this.rootNode = rootNode;
        this.status = initStatus;
        this.flowExecutionCallbacks = new ArrayList<>();
    }

    @Override
    public void addExecutionCallback(ApprovalFlowExecutionCallback executionCallback) {
        Assert.notNull(executionCallback, "executionCallback must not null");
        this.flowExecutionCallbacks.add(executionCallback);
    }

    @Override
    public List<ApprovalFlowExecutionCallback> getFlowExecutionCallbacks() {
        return flowExecutionCallbacks;
    }

    @Override
    public Integer getStatusCode() {
        return status.getCode();
    }

    @Override
    public String getFlowId() {
        return flowId;
    }

    @Override
    public String getFlowType() {
        return flowType;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    ApprovalNode getRootNode() {
        return rootNode;
    }

}
