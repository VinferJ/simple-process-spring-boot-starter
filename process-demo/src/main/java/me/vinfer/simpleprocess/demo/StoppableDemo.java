package me.vinfer.simpleprocess.demo;

import java.util.ArrayList;
import java.util.List;

import me.vinfer.simpleprocess.core.AbstractStatefulProcessNode;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.SimpleConditionProcessNode;
import me.vinfer.simpleprocess.core.StatefulProcessChain;
import me.vinfer.simpleprocess.core.factory.AbstractProcessChainFactory;
import me.vinfer.simpleprocess.core.factory.StatefulProcessChainFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author vinfer
 * @date 2023-03-27 15:32
 */
@Component
public class StoppableDemo implements InitializingBean {

    private StatefulProcessChain statefulProcessChain;

    private static class TaskNode extends AbstractStatefulProcessNode {

        private final String taskId;

        private TaskNode(String taskId, int order) {
            super(taskId);
            this.taskId = taskId;
            setOrder(order);
        }

        @Override
        protected void runInternal() {
            String taskName = "Task-"+taskId;
            System.out.println(taskName + " Started....");
            for (int i = 1; i <= 15; i++) {
                System.out.println(taskName + " Processing.... round: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(taskName + " Finished....");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ProcessNode> processNodes = new ArrayList<>(10);

        for (int i = 1; i <= 10; i++) {
            ProcessNode node = new TaskNode(String.valueOf(i), i);
            processNodes.add(node);
            if (i == 5 || i == 8) {
                int num = i;
                ProcessNode matchedNode = new TaskNode("true-" + num, num);
                ProcessNode unmatchedNode = new TaskNode("false-" + num, num);
                SimpleConditionProcessNode conditionProcessNode =
                        new SimpleConditionProcessNode(
                                "condition-"+num,
                                () -> num % 2 == 0,
                                matchedNode,
                                unmatchedNode
                        );
                conditionProcessNode.setOrder(i);
                processNodes.add(conditionProcessNode);
            }
        }

        AbstractProcessChainFactory chainFactory = new StatefulProcessChainFactory();
        this.statefulProcessChain = chainFactory.createProcessChain("StoppableTaskChain", processNodes);
    }

    public void start() throws Throwable {
        if (statefulProcessChain.isRunning()) {
            statefulProcessChain.resume();
        }else {
            statefulProcessChain.execute();
        }
    }

    public void stop() {
        statefulProcessChain.stop();
    }

    public void resume() {
        statefulProcessChain.resumeState();
    }


}
