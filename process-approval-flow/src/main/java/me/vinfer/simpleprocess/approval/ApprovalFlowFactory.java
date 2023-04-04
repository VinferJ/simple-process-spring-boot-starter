package me.vinfer.simpleprocess.approval;

/**
 * @author vinfer
 * @date 2023-04-03 18:27
 */
public interface ApprovalFlowFactory {

    AbstractApprovalFlow getApprovalFlow(String flowId);

}
