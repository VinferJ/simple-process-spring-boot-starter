package me.vinfer.simpleprocess.approval.context;

import me.vinfer.simpleprocess.approval.AbstractApprovalNode;

/**
 * @author vinfer
 * @date 2023-04-03 16:00
 */
public abstract class OperationApprovalContext implements ApprovalContext {

    private String operateFlowId;
    private String operateProcessId;
    private Approver approver;
    private String postscript;

    @Override
    public String operateProcessId() {
        return operateProcessId;
    }

    public void setOperateProcessId(String operateProcessId) {
        this.operateProcessId = operateProcessId;
    }

    @Override
    public String operateFlowId() {
        return operateFlowId;
    }

    public void setOperateFlowId(String operateFlowId) {
        this.operateFlowId = operateFlowId;
    }

    @Override
    public Approver getApprover() {
        return approver;
    }

    public void setApprover(Approver approver) {
        this.approver = approver;
    }

    @Override
    public String getPostscript() {
        return postscript;
    }

    public void setPostscript(String postscript) {
        this.postscript = postscript;
    }

    public String getOperateProcessId() {
        return operateProcessId;
    }

    public abstract void applyOperation(AbstractApprovalNode approvalNode);

}
