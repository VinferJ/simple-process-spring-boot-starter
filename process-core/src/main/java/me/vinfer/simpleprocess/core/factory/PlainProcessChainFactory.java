package me.vinfer.simpleprocess.core.factory;

import java.util.Collection;
import java.util.List;

import me.vinfer.simpleprocess.core.PlainProcessChain;
import me.vinfer.simpleprocess.core.ProcessChain;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.decorator.ProcessNodeDecorator;
import me.vinfer.simpleprocess.core.parse.ProcessNodeParser;
import org.springframework.lang.Nullable;

/**
 * @author vinfer
 * @date 2023-03-31 16:31
 */
@SuppressWarnings({"rawtypes"})
public class PlainProcessChainFactory extends AbstractProcessChainFactory{

    public PlainProcessChainFactory(@Nullable List<ProcessNodeDecorator> decorators,
                                    @Nullable Collection<ProcessNodeParser> parsers) {
        super(decorators, parsers);
    }

    public PlainProcessChainFactory() {
    }

    @Override
    protected ProcessChain createChain(String chainId, ProcessNode rootNode) {
        return new PlainProcessChain(chainId, rootNode);
    }

}
