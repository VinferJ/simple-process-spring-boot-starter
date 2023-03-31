package me.vinfer.simpleprocess.core;

/**
 * @author vinfer
 * @date 2023-03-31 15:07
 */
public interface ProcessNodeLifecycle {

    default void beforeStart(String processId) {}

    default void onFinished(ProcessNode node) {}

    default void onError(ProcessNode node, Throwable error) {}

}
