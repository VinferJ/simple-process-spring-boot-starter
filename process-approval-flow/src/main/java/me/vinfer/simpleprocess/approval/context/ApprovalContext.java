package me.vinfer.simpleprocess.approval.context;

import me.vinfer.simpleprocess.approval.ApprovalFlow;
import me.vinfer.simpleprocess.approval.ApprovalNode;

/**
 * approval operation context.
 *
 * @author vinfer
 * @date 2023-04-03 14:28
 */
public interface ApprovalContext {

    /**
     * which approval flow to operate
     * @return      {@link ApprovalFlow#getFlowId()}
     */
    String operateFlowId();

    /**
     * which approval node to operate.
     * @return      {@link ApprovalNode#getProcessId()}
     */
    String operateProcessId();

    /**
     * who submit this approval operation
     * @return  {@link Approver}
     */
    Approver getApprover();

    default String getPostscript() {
        return "";
    }

}
