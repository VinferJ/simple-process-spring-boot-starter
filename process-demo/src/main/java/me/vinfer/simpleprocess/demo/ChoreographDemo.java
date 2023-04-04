package me.vinfer.simpleprocess.demo;

import java.util.ArrayList;
import java.util.List;

import me.vinfer.simpleprocess.core.ConditionProcessNode;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.SimpleConditionProcessNode;
import me.vinfer.simpleprocess.core.SimpleProcessNode;
import me.vinfer.simpleprocess.core.common.ConditionNodeConf;
import me.vinfer.simpleprocess.core.common.NodeConf;
import me.vinfer.simpleprocess.core.common.PlainNodeConf;
import me.vinfer.simpleprocess.core.utils.ProcessChoreographer;
import me.vinfer.simpleprocess.core.utils.ProcessPrinter;

/**
 * @author vinfer
 * @date 2023-04-03 11:05
 */
public class ChoreographDemo {

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
        ProcessNode flowNode = ProcessChoreographer.choreograph(nodeConfList, processNodes);
        runNode(flowNode);

        // build by manual
        ProcessNode builtFlowNode = ProcessChoreographer.choreographer()
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
            root.execute();
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
