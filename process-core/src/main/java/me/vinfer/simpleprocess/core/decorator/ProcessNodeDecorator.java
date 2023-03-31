package me.vinfer.simpleprocess.core.decorator;

import me.vinfer.simpleprocess.core.ProcessNode;

/**
 * @author vinfer
 * @date 2023-03-31 15:04
 */
public interface ProcessNodeDecorator {

    /**
     * decorate the {@link ProcessNode} before it was choreographed or executed.
     * @param originalNode  {@link ProcessNode}
     * @return              {@link ProcessNode} after decorating
     */
    ProcessNode decorate(ProcessNode originalNode);

}
