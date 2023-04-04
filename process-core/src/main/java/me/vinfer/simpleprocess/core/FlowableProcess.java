package me.vinfer.simpleprocess.core;

import me.vinfer.simpleprocess.core.common.Component;
import org.springframework.core.Ordered;

/**
 * a struct interface for any type process node or any linkable struct component.
 *
 * <p> supports sort by {@link Ordered} or {@link org.springframework.core.annotation.Order}
 *
 * @author vinfer
 * @date 2023-04-03 14:42
 * @param <T>   actual type of process node
 */
public interface FlowableProcess<T> extends Ordered, Component {

    String getProcessId();

    T next();

    void setNext(T node);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
