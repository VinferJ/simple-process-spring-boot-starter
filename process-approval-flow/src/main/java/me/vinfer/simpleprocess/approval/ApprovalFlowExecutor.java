package me.vinfer.simpleprocess.approval;

import me.vinfer.simpleprocess.approval.context.ApprovalContext;
import me.vinfer.simpleprocess.approval.exception.ApprovalExecutionException;

/**
 * @author vinfer
 * @date 2023-04-03 15:13
 */
public interface ApprovalFlowExecutor {

    /**
     * execute an approval flow specified by {@link ApprovalContext}.
     * @param ac        {@link ApprovalContext}
     * @return          the next pending node of approval flow,
     *                  there is no pending node if is null and that means
     *                  the approval flow finished.
     */
    String execute(ApprovalContext ac) throws ApprovalExecutionException;

    /**
     * cancel an approval flow specified by {@link ApprovalContext}.
     * @param ac        {@link ApprovalContext}
     */
    void cancel(ApprovalContext ac) throws ApprovalExecutionException;

}
