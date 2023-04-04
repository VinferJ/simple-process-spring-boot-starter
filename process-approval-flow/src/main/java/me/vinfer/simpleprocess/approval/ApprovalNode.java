package me.vinfer.simpleprocess.approval;

import java.util.Arrays;
import java.util.List;

import me.vinfer.simpleprocess.approval.common.CodeEnum;
import me.vinfer.simpleprocess.core.FlowableProcess;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * @author vinfer
 * @date 2023-04-03 14:27
 */
public interface ApprovalNode extends FlowableProcess<ApprovalNode> {

    enum Status implements CodeEnum {
        PENDING(0, "Waiting to be executed"),
        APPROVED(1, "Had been approved"),
        REJECTED(2, "Had been rejected");

        private final int code;
        private final String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return desc;
        }

        @NonNull
        public static Status of(int code) {
            Status status = Arrays.stream(Status.values())
                    .filter(e -> e.match(code))
                    .findFirst()
                    .orElse(null);
            Assert.notNull(status, "Cannot find a status enum with code ["+code+"]");
            return status;
        }
    }

    Integer getStatusCode();

    void addExecutionCallback(ApprovalExecutionCallback executionCallback);

    List<ApprovalExecutionCallback> getExecutionCallbacks();

    default Status getStatus() {
        return Status.of(getStatusCode());
    }

    default boolean isPending() {
        return getStatus().match(Status.PENDING);
    }

    default boolean isApproved() {
        return getStatus().match(Status.APPROVED);
    }

    default boolean isRejected() {
        return getStatus().match(Status.REJECTED);
    }

}
