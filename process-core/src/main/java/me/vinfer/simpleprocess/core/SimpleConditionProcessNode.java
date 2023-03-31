package me.vinfer.simpleprocess.core;

import java.util.function.Supplier;

/**
 * @author vinfer
 * @date 2023-03-28 15:01
 */
public class SimpleConditionProcessNode extends ProcessNodeAbstract implements ConditionProcessNode {

    private final Supplier<Boolean> matchedCondition;
    private final ProcessNode matchedOperation;
    private final ProcessNode unmatchedOperation;

    public SimpleConditionProcessNode(String processId,
                                      Supplier<Boolean> matchedCondition,
                                      ProcessNode matchedOperation,
                                      ProcessNode unmatchedOperation) {
        this.matchedCondition = matchedCondition;
        this.matchedOperation = matchedOperation;
        this.unmatchedOperation = unmatchedOperation;
        setProcessId(processId);
    }

    public SimpleConditionProcessNode(Supplier<Boolean> matchedCondition,
                                      ProcessNode matchedOperation,
                                      ProcessNode unmatchedOperation) {
        this(null, matchedCondition, matchedOperation, unmatchedOperation);
    }

    public SimpleConditionProcessNode(Supplier<Boolean> matchedCondition,
                                      Runnable matchedOperation,
                                      Runnable unmatchedOperation){
        this(
                matchedCondition,
                new SimpleProcessNode(matchedOperation),
                new SimpleProcessNode(unmatchedOperation)
        );
    }

    public SimpleConditionProcessNode(String processId,
                                      Supplier<Boolean> matchedCondition,
                                      Runnable matchedOperation,
                                      Runnable unmatchedOperation){
        this(
                matchedCondition,
                new SimpleProcessNode(matchedOperation),
                new SimpleProcessNode(unmatchedOperation)
        );
        setProcessId(processId);
    }


    public SimpleConditionProcessNode(Supplier<Boolean> matchedCondition,
                                      Runnable matchedOperation) {
        this(matchedCondition, matchedOperation, () -> {});
    }

    public SimpleConditionProcessNode(String processId,
                                      Supplier<Boolean> matchedCondition,
                                      Runnable matchedOperation) {
        this(matchedCondition, matchedOperation, () -> {});
        setProcessId(processId);
    }

    @Override
    public boolean matchedCondition() {
        return null != matchedCondition && matchedCondition.get();
    }

    @Override
    public ProcessNode getConditionMatchedNode() {
        return matchedOperation;
    }

    @Override
    public ProcessNode getConditionUnmatchedNode() {
        return unmatchedOperation;
    }

}
