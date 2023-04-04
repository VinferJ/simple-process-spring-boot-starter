package me.vinfer.simpleprocess.approval.provider;

import me.vinfer.simpleprocess.approval.context.Approver;

/**
 * @author vinfer
 * @date 2023-04-03 16:42
 */
public interface ApprovalOperationAuthorizer {

    boolean isPermitted(String operateProcessId, Approver approver);

}
