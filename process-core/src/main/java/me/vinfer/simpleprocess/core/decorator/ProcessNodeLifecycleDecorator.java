package me.vinfer.simpleprocess.core.decorator;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import cn.hutool.core.collection.CollectionUtil;
import me.vinfer.simpleprocess.core.AbstractStatefulProcessNode;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.ProcessNodeAbstract;
import me.vinfer.simpleprocess.core.ProcessNodeLifecycle;
import org.springframework.lang.Nullable;

/**
 * @author vinfer
 * @date 2023-03-31 15:02
 */
public class ProcessNodeLifecycleDecorator implements ProcessNodeDecorator{

    private final Collection<ProcessNodeLifecycle> callbacks;

    private final ExecutorService executorService;

    public ProcessNodeLifecycleDecorator(Collection<ProcessNodeLifecycle> callbacks,
                                         @Nullable ExecutorService executorService) {
        this.callbacks = callbacks;
        this.executorService = executorService;
    }

    @Override
    public ProcessNode decorate(ProcessNode originalNode) {
        if (CollectionUtil.isEmpty(callbacks)) {
            return originalNode;
        }

        if (originalNode instanceof AbstractStatefulProcessNode) {
            return decorateStatefulNode(originalNode);
        }

        return decoratePlainNode(originalNode);
    }

    protected AbstractStatefulProcessNode decorateStatefulNode(ProcessNode originalNode) {
        Runnable decorated = getDecorated(originalNode);
        return new AbstractStatefulProcessNode(originalNode.getProcessId()) {
            @Override
            protected void runInternal() {
                decorated.run();
            }
        };
    }

    protected ProcessNode decoratePlainNode(ProcessNode originalNode) {
        Runnable decorated = getDecorated(originalNode);
        return new ProcessNodeAbstract() {
            @Override
            public void start() {
                decorated.run();
            }
        };
    }

    protected Runnable getDecorated(ProcessNode originalNode) {
        return () -> {
            try {
                for (ProcessNodeLifecycle callback : callbacks) {
                    beforeStart(callback, originalNode);
                }
                originalNode.start();
                for (ProcessNodeLifecycle callback : callbacks) {
                    onFinished(callback, originalNode);
                }
            }catch (Throwable t) {
                for (ProcessNodeLifecycle callback : callbacks) {
                    onError(callback, originalNode, t);
                }
            }
        };
    }

    protected void beforeStart(ProcessNodeLifecycle callback, ProcessNode node) {
        executeCmd(() -> callback.beforeStart(node.getProcessId()));
    }

    protected void onFinished(ProcessNodeLifecycle callback, ProcessNode node) {
        executeCmd(() -> callback.onFinished(node));
    }

    protected void onError(ProcessNodeLifecycle callback, ProcessNode node, Throwable t) {
        executeCmd(() -> callback.onError(node, t));
    }

    protected void executeCmd(Runnable cmd) {
        if (null != executorService) {
            executorService.execute(cmd);
        }else {
            cmd.run();
        }
    }

}
