package me.vinfer.simpleprocess.core.factory;

import java.util.Collection;

import me.vinfer.simpleprocess.core.ProcessChain;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.StatefulProcessChain;
import me.vinfer.simpleprocess.core.decorator.ProcessNodeDecorator;
import me.vinfer.simpleprocess.core.parse.ProcessNodeParser;
import org.springframework.lang.Nullable;

/**
 * @author vinfer
 * @date 2023-03-31 16:31
 */
@SuppressWarnings({"rawtypes"})
public class StatefulProcessChainFactory extends AbstractProcessChainFactory{

    public StatefulProcessChainFactory(@Nullable ProcessNodeDecorator decorator,
                                       @Nullable Collection<ProcessNodeParser> parsers) {
        super(decorator, parsers);
    }

    public StatefulProcessChainFactory() {
    }

    @Override
    protected ProcessChain createChain(String chainId, ProcessNode rootNode) {
        return new StatefulProcessChain(chainId, rootNode);
    }

}
