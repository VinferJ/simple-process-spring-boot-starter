package me.vinfer.simpleprocess.core;

import org.springframework.core.Ordered;

/**
 * plain process node, sorting support {@link Ordered} or {@link org.springframework.core.annotation.Order}
 * @author vinfer
 * @date 2023-03-27 15:34
 */
public interface ProcessNode extends FlowableProcess<ProcessNode> {

    ProcessNode EMPTY_NODE = new ProcessNode() {
        @Override
        public void execute() {}

        @Override
        public ProcessNode next() {return null;}

        @Override
        public String getProcessId() {
            return "DEFAULT_EMPTY_NODE";
        }

        @Override
        public void setNext(ProcessNode node) {
            throw new IllegalArgumentException("Cannot set any node to an empty processNode");
        }
    };

    /**
     * execute this node
     */
    void execute();

}
