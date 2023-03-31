package me.vinfer.simpleprocess.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import me.vinfer.simpleprocess.core.ConditionProcessNode;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.SimpleConditionProcessNode;
import me.vinfer.simpleprocess.core.SimpleProcessNode;
import me.vinfer.simpleprocess.core.common.ConditionNodeConf;
import me.vinfer.simpleprocess.core.common.NodeConf;
import me.vinfer.simpleprocess.core.common.PlainNodeConf;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author vinfer
 * @date 2023-03-28 15:10
 */
public class ProcessChoreographer {

    public static class Choreographer {
        private final ProcessNode root;

        private ProcessNode tailNode;

        public Choreographer(ProcessNode root) {
            this.root = root;
            this.tailNode = root;
        }

        public Choreographer() {
            // use empty root node(just for linking)
            this(new SimpleProcessNode(() -> {}, "DEFAULT_EMPTY_ROOT"));
        }

        /**
         * set this node as the current tailNode's next node.
         *
         * <p> if current tailNode is a {@link ConditionProcessNode},
         * this node will be set as the next node of {@link ConditionProcessNode#getConditionMatchedNode()}
         *
         * @param node      the node to set next
         * @return          {@link Choreographer}
         */
        public Choreographer next(ProcessNode node) {
            if (tailNode instanceof ConditionProcessNode) {
                return matchedNext(node);
            }
            tailNode.setNext(node);
            refreshTailNode();
            return this;
        }

        public Choreographer matchedNext(ProcessNode node) {
            return conditionNext(ConditionProcessNode::getConditionMatchedNode, node);
        }

        public Choreographer unmatchedNext(ProcessNode node) {
            return conditionNext(ConditionProcessNode::getConditionUnmatchedNode, node);
        }

        public Choreographer restTail(ProcessNode node) {
            tailNode = node;
            return this;
        }

        private Choreographer conditionNext(Function<ConditionProcessNode, ProcessNode> operationNodeGetter,
                                            ProcessNode linkNode) {
            if (!(tailNode instanceof ConditionProcessNode)) {
                throw new UnsupportedOperationException("tail node is not a condition node");
            }

            ProcessNode operationNode = operationNodeGetter.apply((ConditionProcessNode) tailNode);

            if (null == operationNode || operationNode.equals(ProcessNode.EMPTY_NODE)) {
                throw new IllegalArgumentException("condition operation node is null or empty node");
            }

            operationNode.setNext(linkNode);
            tailNode = linkNode;
            return this;
        }

        private void refreshTailNode() {
            this.tailNode = tailNode.next();
        }

        public ProcessNode build() {
            return root;
        }
    }

    public static Choreographer choreographer() {
        return new Choreographer();
    }

    public static Choreographer choreographer(ProcessNode rootNode) {
        return new Choreographer(rootNode);
    }

    /**
     * choreograph all process nodes as a complete process flow and return the root node.
     *
     * <p> if the {@code processNodes} contains {@link ConditionProcessNode},
     * this method is not recommended for process flow generation.
     * <p> please use {@link #choreograph(List, List)}
     *
     * @param processNodes      {@link ProcessNode}
     * @return                  root node of these {@code processNodes},
     *                          return {@link ProcessNode#EMPTY_NODE} if {@code processNodes} is empty
     */
    public static ProcessNode choreograph(List<ProcessNode> processNodes) {
        if (CollectionUtil.isEmpty(processNodes)) {
            return ProcessNode.EMPTY_NODE;
        }

        AnnotationAwareOrderComparator.sort(processNodes);
        ProcessNode root = CollectionUtil.getFirst(processNodes);
        ProcessNode currentNode = root;

        for (int i = 1; i < processNodes.size(); i++) {
            ProcessNode processNode = processNodes.get(i);
            // link this node
            currentNode.setNext(processNode);
            currentNode = currentNode.next();

            // process the condition node which isn't the last
            if (processNode instanceof ConditionProcessNode && i < processNodes.size() - 1) {
                // the next node of this conditionProcessNode will be auto set after executing it
                ConditionProcessNode conditionProcessNode = (ConditionProcessNode) processNode;
                ProcessNode matchedNode = conditionProcessNode.getConditionMatchedNode();
                ProcessNode unmatchedNode = conditionProcessNode.getConditionUnmatchedNode();

                // build for both unmatchedNode and matchedNode if not null
                List<ProcessNode> remainNodes = processNodes.subList(i + 1, processNodes.size());
                ProcessNode remainNodesProcessFlow = choreograph(remainNodes);
                if (null != matchedNode) {
                    matchedNode.setNext(remainNodesProcessFlow);
                }
                if (null != unmatchedNode) {
                    unmatchedNode.setNext(remainNodesProcessFlow);
                }

                return root;
            }
        }

        return root;
    }

    public static ProcessNode choreograph(List<? extends NodeConf> nodeConfList,
                                          List<? extends ProcessNode> processNodes) {
        // check empty
        if (CollectionUtil.isEmpty(nodeConfList) || CollectionUtil.isEmpty(processNodes)) {
            throw new IllegalArgumentException("Empty nodeConfList or processNodes");
        }

        // all nodes must set processId
        validProcessNodes(processNodes);

        // sort conf list
        AnnotationAwareOrderComparator.sort(nodeConfList);

        // build process flow by choreographer
        Choreographer choreographer = new Choreographer();
        doChoreograph(choreographer, nodeConfList, processNodes);

        return choreographer.build();
    }

    private static void doChoreograph(Choreographer choreographer,
                                      List<? extends NodeConf> nodeConfList,
                                      List<? extends ProcessNode> processNodes) {
        if (CollectionUtil.isEmpty(nodeConfList)) {
            return;
        }

        // iterate conf list and build the process flow
        for (NodeConf nodeConf : nodeConfList) {
            if (StrUtil.equals(choreographer.tailNode.getProcessId(), nodeConf.getProcessId())) {
                continue;
            }

            ProcessNode processNode = findProcessNode(nodeConf, processNodes);
            Assert.notNull(processNode, "Cannot find a processNode with processId ["+nodeConf.getProcessId()+"]");
            choreographer.next(processNode);

            if (nodeConf instanceof ConditionNodeConf) {
                Assert.isTrue(processNode instanceof ConditionProcessNode, "required a ConditionProcessNode " +
                        "but find a plain ProcessNode with processId ["+nodeConf.getProcessId()+"]");
                ConditionProcessNode conditionNode = (ConditionProcessNode) processNode;
                ConditionNodeConf conditionNodeConf = (ConditionNodeConf) nodeConf;

                if (null != conditionNode.getConditionMatchedNode()) {
                    Choreographer matchNodeChoreographer = new Choreographer(conditionNode.getConditionMatchedNode());
                    doChoreograph(matchNodeChoreographer, conditionNodeConf.getMatchedNodeConfList(), processNodes);
                }

                if (null != conditionNode.getConditionUnmatchedNode()) {
                    Choreographer unmatchedNodeChoreographer = new Choreographer(conditionNode.getConditionUnmatchedNode());
                    doChoreograph(unmatchedNodeChoreographer, conditionNodeConf.getUnmatchedNodeConfList(), processNodes);
                }
            }
        }
    }

    private static void validProcessNodes(List<? extends ProcessNode> processNodes) {
        boolean processIdAllNotBlank = processNodes.stream()
                .allMatch(node -> StringUtils.hasText(node.getProcessId()));
        Assert.isTrue(processIdAllNotBlank, "All processNodes processId must not blank!");
    }

    private static ProcessNode findProcessNode(NodeConf nodeConf, List<? extends ProcessNode> processNodes) {
        return processNodes.stream()
                .filter(node -> StrUtil.equals(node.getProcessId(), nodeConf.getProcessId()))
                .findFirst()
                .orElse(null);
    }

    public static void main(String[] args) {
        NodeConf n1 = PlainNodeConf.of("N1");

        List<NodeConf> c1MatchedList = new ArrayList<>();
        List<NodeConf> c1UnmatchedList = new ArrayList<>();
        ConditionNodeConf c1 = new ConditionNodeConf("C1", c1MatchedList, c1UnmatchedList);

        List<NodeConf> c2MatchedList = new ArrayList<>();
        List<NodeConf> c2UnmatchedList = new ArrayList<>();
        ConditionNodeConf c2 = new ConditionNodeConf("C2", c2MatchedList, c2UnmatchedList);
        c2MatchedList.add(PlainNodeConf.of("N4"));
        c2MatchedList.add(PlainNodeConf.of("N9"));
        c2UnmatchedList.add(PlainNodeConf.of("N8"));
        c2UnmatchedList.add(PlainNodeConf.of("N9"));

        List<NodeConf> c3MatchedList = new ArrayList<>();
        List<NodeConf> c3UnmatchedList = new ArrayList<>();
        ConditionNodeConf c3 = new ConditionNodeConf("C3", c3MatchedList, c3UnmatchedList);
        c3UnmatchedList.add(PlainNodeConf.of("N3"));
        c3UnmatchedList.add(c2);
        c3MatchedList.add(PlainNodeConf.of("N7"));
        c3MatchedList.add(PlainNodeConf.of("N8"));
        c3MatchedList.add(PlainNodeConf.of("N9"));

        c1MatchedList.add(PlainNodeConf.of("N2"));
        c1MatchedList.add(PlainNodeConf.of("N3"));
        c1MatchedList.add(c2);
        c1UnmatchedList.add(PlainNodeConf.of("N5"));
        c1UnmatchedList.add(PlainNodeConf.of("N6"));
        c1UnmatchedList.add(c3);

        List<NodeConf> nodeConfList = new ArrayList<>();
        nodeConfList.add(n1);
        nodeConfList.add(c1);

        List<ProcessNode> processNodes = new ArrayList<>();
        processNodes.add(createNode("N1"));//0
        processNodes.add(createNode("N3"));//1
        processNodes.add(createNode("N4"));//2
        processNodes.add(createNode("N5"));//3
        processNodes.add(createNode("N6"));//4
        processNodes.add(createNode("N7"));//5
        processNodes.add(createNode("N8"));//6
        processNodes.add(createNode("N9"));//7
        processNodes.add(createConditionNode("C1", false, "N2", "N5"));//8
        processNodes.add(createConditionNode("C2", false, "N4", "N8"));//9
        processNodes.add(createConditionNode("C3", true, "N7", "N3"));//10

        // build with node conf
        ProcessNode flowNode = choreograph(nodeConfList, processNodes);
        runNode(flowNode);

        // build by manual
        ProcessNode builtFlowNode = choreographer()
                .next(processNodes.get(0))
                .next(processNodes.get(8))
                .matchedNext(processNodes.get(1))
                .next(processNodes.get(9))
                .matchedNext(processNodes.get(7))
                .restTail(processNodes.get(9))
                .unmatchedNext(processNodes.get(7))
                .restTail(processNodes.get(8))
                .unmatchedNext(processNodes.get(4))
                .next(processNodes.get(10))
                .matchedNext(processNodes.get(6))
                .next(processNodes.get(7))
                .restTail(processNodes.get(10))
                .unmatchedNext(processNodes.get(9))
                .build();
        runNode(builtFlowNode);

        System.out.println(ProcessPrinter.printFlowChart(flowNode));
    }

    static void runNode(ProcessNode root) {
        while (root != null) {
            root.start();
            root = root.next();
        }
        System.out.println("\n");
    }

    static ProcessNode createNode(String processId) {
        return new SimpleProcessNode(() -> {
            System.out.println("=================>Execute Node: " + processId);
        }, processId);
    }

    static ConditionProcessNode createConditionNode(String processId,
                                                    boolean condition,
                                                    String matchedNodeProcessId,
                                                    String unmatchedNodeProcessId) {
        return new SimpleConditionProcessNode(
                processId,
                () -> {
                    System.out.println("===========>Calculate Condition: " + processId + "\tResult: " + condition);
                    return condition;
                },
                createNode(matchedNodeProcessId),
                createNode(unmatchedNodeProcessId)
        );
    }

}
