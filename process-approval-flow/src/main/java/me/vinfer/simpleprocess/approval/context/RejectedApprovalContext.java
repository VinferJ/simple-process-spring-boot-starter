package me.vinfer.simpleprocess.approval.context;

import me.vinfer.simpleprocess.approval.AbstractApprovalNode;
import me.vinfer.simpleprocess.approval.ApprovalNode;

/**
 * @author vinfer
 * @date 2023-04-03 16:08
 */
public class RejectedApprovalContext extends OperationApprovalContext{

    @Override
    public void applyOperation(AbstractApprovalNode approvalNode) {
        approvalNode.setStatus(ApprovalNode.Status.REJECTED);
    }

}
