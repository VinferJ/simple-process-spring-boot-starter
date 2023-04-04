package me.vinfer.simpleprocess.core.decorator;

import me.vinfer.simpleprocess.core.ProcessNode;
import org.springframework.core.Ordered;

/**
 * decorate the {@link ProcessNode} before it was build as a {@link me.vinfer.simpleprocess.core.ProcessChain}.
 *
 * <p> supports sort by {@link Ordered} and {@link org.springframework.core.annotation.Order}
 *
 * @author vinfer
 * @date 2023-03-31 15:04
 */
public interface ProcessNodeDecorator extends Ordered {

    /**
     * decorate the {@link ProcessNode} before it was choreographed or executed.
     * @param originalNode  {@link ProcessNode}
     * @return              {@link ProcessNode} after decorating
     */
    ProcessNode decorate(ProcessNode originalNode);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
