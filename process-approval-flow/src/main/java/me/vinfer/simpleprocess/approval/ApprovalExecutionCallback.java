package me.vinfer.simpleprocess.approval;

import me.vinfer.simpleprocess.approval.context.ApprovalContext;

/**
 * callback operation on executing an {@link ApprovalNode}
 * @author vinfer
 * @date 2023-04-03 15:25
 */
public interface ApprovalExecutionCallback {

    /**
     * callback on an approval node has been approved.
     * @param ac                {@link ApprovalContext}
     * @param nextPendingNode   processId of the next pending node of this approved node's approval flow,
     *                          there is no next pending node if it's null.
     */
    default void onApproved(ApprovalContext ac, String nextPendingNode) {}

    default void onRejected(ApprovalContext ac) {}

}
