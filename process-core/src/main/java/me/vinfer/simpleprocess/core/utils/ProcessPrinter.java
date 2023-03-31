package me.vinfer.simpleprocess.core.utils;

import me.vinfer.simpleprocess.core.ConditionProcessNode;
import me.vinfer.simpleprocess.core.ProcessNode;

/**
 * @author vinfer
 * @date 2023-03-28 16:45
 */
public class ProcessPrinter {

    private static final String PLAIN_PROCESS_CHART = "┌%s┐\n" +
                                                      "│%s│\n" +
                                                      "└%s┘\n";

    private static final String CONDITION_PROCESS_CHART = "";

    private static final String HORIZONTAL_ARROW = " ─────▶ ";
    private static final String VERTICAL_LINE = "│";
    private static final String DOWN_ARROW = "▼";

    interface ConditionNodePrinter {
        String CONDITION_PREFIX = "Condition-";
        String print(ConditionProcessNode cpn, int pending);
    }

    static class CompleteFlowChartPrinter implements ConditionNodePrinter {

        private static final String MATCHED_ARROW_FLAG = "T" + HORIZONTAL_ARROW;
        private static final String UNMATCHED_ARROW_FLAG = "F" + HORIZONTAL_ARROW;


        @Override
        public String print(ConditionProcessNode cpn, int pending) {
            // gen matched node flow chart
            ProcessNode matchedNode = cpn.getConditionMatchedNode();
            String matchedNodeFlowChart = doPrintFlowChart(matchedNode,
                    pending + MATCHED_ARROW_FLAG.length(), this);

            // gen unmatched node flow chart
            ProcessNode unmatchedNode = cpn.getConditionUnmatchedNode();
            String unmatchedNodeFlowChart = doPrintFlowChart(unmatchedNode,
                    pending + UNMATCHED_ARROW_FLAG.length(), this);

            // concat flow chart string
            String pendingStr = repeatStr(pending, " ");
            return CONDITION_PREFIX + cpn.getProcessId() + "\n" + pendingStr
                    + MATCHED_ARROW_FLAG + matchedNodeFlowChart + "\n" + pendingStr
                    + UNMATCHED_ARROW_FLAG + unmatchedNodeFlowChart;
        }

    }

    static ConditionNodePrinter completeFlowChartPrinter = new CompleteFlowChartPrinter();

    public static String printFlowChart(ProcessNode root) {
        return doPrintFlowChart(root, 0, completeFlowChartPrinter) ;
    }

    private static String doPrintFlowChart(ProcessNode node,
                                           int pending,
                                           ConditionNodePrinter conditionNodePrinter){
        if (null == node) {
            return "END";
        }

        if (node instanceof ConditionProcessNode) {
            return conditionNodePrinter.print((ConditionProcessNode) node, pending);
        }

        String line = node.getProcessId() + HORIZONTAL_ARROW;
        return line + doPrintFlowChart(node.next(), pending + line.length(), conditionNodePrinter);
    }

    public static String printExecutedFlowChart(ProcessNode root) {
        return "";
    }

    private static String doPrintExecutedFlowChart(ProcessNode node) {
        if (null == node) {
            return "END";
        }
        return "";
    }

    private static String drawWithBorder(String content) {
        int contentLen = content.length();
        int pending = 2;
        int borderLen = contentLen + pending * 2;
        String borderLine = repeatStr(borderLen, "─");
        String contentLine = repeatStr(pending, " ") + content + repeatStr(pending, " ");
        String arrowPending = repeatStr(borderLen / 2, " ");
        String arrowPointer = arrowPending + VERTICAL_LINE + "\n" + arrowPending + DOWN_ARROW + "\n";
        return String.format(PLAIN_PROCESS_CHART, borderLine, contentLine, borderLine) + arrowPointer;
    }

    public static void main(String[] args) {
        String p1 = drawWithBorder("Process-1");
        String p2 = drawWithBorder("Process-2");
        System.out.print(p1 + p2);
    }

    static String repeatStr(int len, String str) {
        return str.repeat(Math.max(0, len));
    }

}
