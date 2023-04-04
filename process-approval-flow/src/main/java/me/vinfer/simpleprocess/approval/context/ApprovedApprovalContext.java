package me.vinfer.simpleprocess.approval.context;

import me.vinfer.simpleprocess.approval.AbstractApprovalNode;
import me.vinfer.simpleprocess.approval.ApprovalNode;

/**
 * @author vinfer
 * @date 2023-04-03 16:06
 */
public class ApprovedApprovalContext extends OperationApprovalContext{

    @Override
    public void applyOperation(AbstractApprovalNode approvalNode) {
        approvalNode.setStatus(ApprovalNode.Status.APPROVED);
    }

}
