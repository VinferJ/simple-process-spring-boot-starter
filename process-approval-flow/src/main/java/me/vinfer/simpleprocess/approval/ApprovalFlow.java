package me.vinfer.simpleprocess.approval;

import java.util.Arrays;
import java.util.List;

import me.vinfer.simpleprocess.approval.common.CodeEnum;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * combine many {@link ApprovalNode} as a complete approval processing flow.
 *
 * @author vinfer
 * @date 2023-04-03 15:13
 */
public interface ApprovalFlow {

    enum Status implements CodeEnum {
        PENDING(0, "Waiting execute"),

        FINISHED(1, "All nodes had been approved or one of them had been rejected"),

        CANCELED(2, "This approval flow had been canceled, " +
                "that means it can't be execute anymore")
        ;

        private final int code;
        private final String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return desc;
        }

        @NonNull
        public static Status of(Integer code) {
            Status status = Arrays.stream(Status.values())
                    .filter(e -> e.match(code))
                    .findFirst()
                    .orElse(null);
            Assert.notNull(status, "Cannot find a status enum with code ["+code+"]");
            return status;
        }
    }

    String getFlowId();

    String getFlowType();

    Integer getStatusCode();

    void addExecutionCallback(ApprovalFlowExecutionCallback executionCallback);

    List<ApprovalFlowExecutionCallback> getFlowExecutionCallbacks();

    default Status getStatus() {
        return Status.of(getStatusCode());
    }

    default boolean isPending() {
        return getStatus().match(Status.PENDING);
    }

    /**
     * is this approval flow was finished already.
     * <p> to decide it can be executed repeatedly
     * @return      true if approved or rejected, or else false
     */
    default boolean isFinished() {
        return getStatus().match(Status.FINISHED);
    }

    /**
     * is this approval flow has been canceled or not.
     * <p> if canceled, that means this approval flow can't be executed anymore.
     * @return      true if canceled, or else false
     */
    default boolean isCanceled() {
        return getStatus().match(Status.CANCELED);
    }

}
