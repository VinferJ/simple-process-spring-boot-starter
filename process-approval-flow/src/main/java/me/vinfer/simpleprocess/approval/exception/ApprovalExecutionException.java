package me.vinfer.simpleprocess.approval.exception;

/**
 * @author vinfer
 * @date 2023-04-03 16:10
 */
public class ApprovalExecutionException extends RuntimeException{

    private final String processId;

    private static final String PREFIX = "Approval Execute Error: ";

    public ApprovalExecutionException(String processId, String msg) {
        super(PREFIX + msg);
        this.processId = processId;
    }

    public String getProcessId() {
        return processId;
    }
}
