package me.vinfer.simpleprocess.core.factory;

import java.util.Collection;

import me.vinfer.simpleprocess.core.common.ComponentSelector;

/**
 * @author vinfer
 * @date 2023-04-03 11:49
 */
public class ProcessChainFactorySelector extends ComponentSelector<AbstractProcessChainFactory> {

    public ProcessChainFactorySelector(Collection<AbstractProcessChainFactory> processChainFactories) {
        super(processChainFactories);
    }

}
