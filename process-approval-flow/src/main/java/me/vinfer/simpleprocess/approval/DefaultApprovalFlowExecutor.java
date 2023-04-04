package me.vinfer.simpleprocess.approval;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.vinfer.simpleprocess.approval.context.ApprovalContext;
import me.vinfer.simpleprocess.approval.context.Approver;
import me.vinfer.simpleprocess.approval.context.OperationApprovalContext;
import me.vinfer.simpleprocess.approval.exception.ApprovalExecutionException;
import me.vinfer.simpleprocess.approval.provider.ApprovalOperationAuthorizer;

/**
 * @author vinfer
 * @date 2023-04-03 18:26
 */
@Slf4j
public class DefaultApprovalFlowExecutor implements ApprovalFlowExecutor{

    private final ApprovalFlowFactory approvalFlowFactory;
    private final ApprovalOperationAuthorizer operationAuthorizer;

    public DefaultApprovalFlowExecutor(ApprovalFlowFactory approvalFlowFactory,
                                       ApprovalOperationAuthorizer operationAuthorizer) {
        this.approvalFlowFactory = approvalFlowFactory;
        this.operationAuthorizer = operationAuthorizer;
    }

    @Override
    public void cancel(ApprovalContext ac) throws ApprovalExecutionException {
        log.info("DefaultApprovalFlowExecutor cancel ac details: {}", ac);
        AbstractApprovalFlow approvalFlow = approvalFlowFactory.getApprovalFlow(ac.operateFlowId());
        ApprovalFlow.Status status = approvalFlow.getStatus();
        String flowId = approvalFlow.getFlowId();
        List<ApprovalFlowExecutionCallback> flowExecutionCallbacks = approvalFlow.getFlowExecutionCallbacks();

        if (status.match(ApprovalFlow.Status.CANCELED)) {
            return;
        }

        if (status.match(ApprovalFlow.Status.FINISHED)) {
            throw new ApprovalExecutionException(flowId, "approval flow already finished");
        }

        approvalFlow.setStatus(ApprovalFlow.Status.CANCELED);
        applyApprovalFlowCallback(flowExecutionCallbacks, ApprovalFlowExecutionCallback::onCanceled, ac);
    }

    @Override
    public String execute(ApprovalContext ac) throws ApprovalExecutionException{
        log.info("DefaultApprovalFlowExecutor execute ac details: {}", ac);
        AbstractApprovalFlow approvalFlow = approvalFlowFactory.getApprovalFlow(ac.operateFlowId());
        return doExecuteFlow(approvalFlow, ac);
    }

    protected String doExecuteFlow(AbstractApprovalFlow approvalFlow, ApprovalContext ac) {
        String flowId = approvalFlow.getFlowId();
        String operateProcessId = ac.operateProcessId();
        Approver approver = ac.getApprover();
        log.info("AbstractApprovalFlow execute operate process id: {} approver: {}", operateProcessId, approver);

        if (approvalFlow.isCanceled()) {
            throw new ApprovalExecutionException(flowId, "approval flow was canceled, cannot execute anymore");
        }

        if (approvalFlow.isFinished()) {
            throw new ApprovalExecutionException(flowId, "approval flow was finished, cannot execute again");
        }

        ApprovalNode operateNode = findOperateNode(approvalFlow, operateProcessId);
        if (operationAuthorizer.isPermitted(operateProcessId, approver)) {
            ((OperationApprovalContext)ac).applyOperation((AbstractApprovalNode) operateNode);
        }else {
            throw new ApprovalExecutionException(operateProcessId, "Operation not permitted on approver ["+approver.getName()+"]" +
                    ", approverId is: ["+approver.getId()+"]");
        }

        // apply callback operations
        List<ApprovalExecutionCallback> executionCallbacks = operateNode.getExecutionCallbacks();
        String nextPendingNode = getNextPendingNode(operateNode);
        if (operateNode.isApproved()) {
            applyApprovalCallback(executionCallbacks, (callback, ctx) -> callback.onApproved(ctx, nextPendingNode), ac);
        }else if (operateNode.isRejected()) {
            applyApprovalCallback(executionCallbacks, ApprovalExecutionCallback::onRejected, ac);
        }

        refreshFlowStatus(approvalFlow, operateNode, ac);

        return nextPendingNode;
    }

    protected ApprovalNode findOperateNode(AbstractApprovalFlow approvalFlow, String operateProcessId) {
        ApprovalNode execNode = approvalFlow.getRootNode();
        ApprovalNode prevNode = null;
        while (null != execNode) {
            if (StrUtil.equals(execNode.getProcessId(), operateProcessId)) {
                if (null != prevNode) {
                    // prev node of this node must be approved
                    if (prevNode.isApproved()) {
                        // make sure the next node is pending!
                        return execNode;
                    }else {
                        throw new ApprovalExecutionException(operateProcessId, "operate node's prev node must be approved");
                    }
                }
            }

            prevNode = execNode;
            execNode = execNode.next();
        }

        throw new ApprovalExecutionException(operateProcessId, "Cannot find an approval node with processId ["+operateProcessId+"]");
    }

    protected String getNextPendingNode(ApprovalNode approvalNode) {
        ApprovalNode next = approvalNode.next();
        if (null != next && next.isPending()) {
            return next.getProcessId();
        }
        return null;
    }

    protected void applyApprovalCallback(Collection<ApprovalExecutionCallback> executionCallbacks,
                                         BiConsumer<ApprovalExecutionCallback, ApprovalContext> callbackOperation,
                                         ApprovalContext ac) {
        for (ApprovalExecutionCallback executionCallback : executionCallbacks) {
            callbackOperation.accept(executionCallback, ac);
        }
    }

    protected void applyApprovalFlowCallback(Collection<ApprovalFlowExecutionCallback> flowExecutionCallbacks,
                                             BiConsumer<ApprovalFlowExecutionCallback, ApprovalContext> callbackOperation,
                                             ApprovalContext ac) {
        for (ApprovalFlowExecutionCallback flowExecutionCallback : flowExecutionCallbacks) {
            callbackOperation.accept(flowExecutionCallback, ac);
        }
    }

    protected void refreshFlowStatus(AbstractApprovalFlow approvalFlow,
                                     ApprovalNode operateNode,
                                     ApprovalContext ac) {
        boolean isAllApproved = false;
        approvalFlow.setStatus(ApprovalFlow.Status.PENDING);
        if (operateNode.isRejected()){
            approvalFlow.setStatus(ApprovalFlow.Status.FINISHED);
        }else if (getNextPendingNode(operateNode) == null){
            approvalFlow.setStatus(ApprovalFlow.Status.FINISHED);
            isAllApproved = true;
        }

        invokeFlowCallbacks(approvalFlow, ac, isAllApproved);
    }

    protected void invokeFlowCallbacks(AbstractApprovalFlow approvalFlow, ApprovalContext ac, boolean isAllApproved) {
        if (approvalFlow.isFinished()) {
            List<ApprovalFlowExecutionCallback> flowExecutionCallbacks = approvalFlow.getFlowExecutionCallbacks();
            applyApprovalFlowCallback(flowExecutionCallbacks, ApprovalFlowExecutionCallback::onFinished, ac);
            if (isAllApproved) {
                applyApprovalFlowCallback(flowExecutionCallbacks, ApprovalFlowExecutionCallback::onApproved, ac);
            }else {
                applyApprovalFlowCallback(flowExecutionCallbacks, ApprovalFlowExecutionCallback::onRejected, ac);
            }
        }
    }

}
