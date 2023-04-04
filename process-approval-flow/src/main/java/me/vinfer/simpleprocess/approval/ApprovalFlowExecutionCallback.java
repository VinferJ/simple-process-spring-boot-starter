package me.vinfer.simpleprocess.approval;

import me.vinfer.simpleprocess.approval.context.ApprovalContext;

/**
 * callback operation on executing an {@link ApprovalFlow}
 * @author vinfer
 * @date 2023-04-03 15:25
 */
public interface ApprovalFlowExecutionCallback {

    default void onFinished(ApprovalContext ac) {}

    /**
     * callback on all approval node of approval flow had been approved.
     * @param ac    {@link ApprovalContext}
     */
    default void onApproved(ApprovalContext ac) {}

    /**
     * there was one node of approval flow had been rejected.
     * @param ac        {@link ApprovalContext}
     */
    default void onRejected(ApprovalContext ac) {}

    default void onCanceled(ApprovalContext ac) {}

}
