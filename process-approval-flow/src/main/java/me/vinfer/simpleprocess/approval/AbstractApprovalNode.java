package me.vinfer.simpleprocess.approval;

/**
 * @author vinfer
 * @date 2023-04-03 16:03
 */
public abstract class AbstractApprovalNode implements ApprovalNode{

    private Integer statusCode;

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatus(Status status) {
        setStatusCode(status.getCode());
    }

}
