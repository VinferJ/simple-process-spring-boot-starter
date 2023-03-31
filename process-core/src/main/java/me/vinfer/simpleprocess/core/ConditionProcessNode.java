package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-28 11:23
 */
public interface ConditionProcessNode extends ProcessNode {

    ConditionProcessNode EMPTY_CONDITION = new ConditionProcessNode() {
        @Override
        public boolean matchedCondition() {
            return true;
        }

        @Override
        public ProcessNode getConditionMatchedNode() {
            return next();
        }

        @Override
        public ProcessNode getConditionUnmatchedNode() {
            return next();
        }

        @Override
        public ProcessNode next() {
            return EMPTY_NODE;
        }

        @Override
        public void setNext(ProcessNode node) {}

        @Override
        public String getProcessId() {
            return "DEFAULT_EMPTY_CONDITION_NODE";
        }
    };

    boolean matchedCondition();

    @Override
    default void start() {
        ProcessNode execNode;

        if (matchedCondition()) {
            execNode = getConditionMatchedNode();
        }else {
            execNode = getConditionUnmatchedNode();
        }

        // keep the flow connecting
        setNext(execNode.next());
        execNode.start();
    }

    ProcessNode getConditionMatchedNode();

    ProcessNode getConditionUnmatchedNode();

}
