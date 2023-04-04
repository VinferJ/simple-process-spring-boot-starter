package me.vinfer.simpleprocess.core.factory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import me.vinfer.simpleprocess.core.ProcessChain;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.common.Component;
import me.vinfer.simpleprocess.core.common.NodeConf;
import me.vinfer.simpleprocess.core.decorator.ProcessNodeDecorator;
import me.vinfer.simpleprocess.core.parse.ProcessNodeParser;
import me.vinfer.simpleprocess.core.utils.ProcessChoreographer;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;

/**
 * ch
 * @author vinfer
 * @date 2023-03-31 15:20
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractProcessChainFactory implements Component {

    private final List<ProcessNodeDecorator> decorators;

    private final Collection<ProcessNodeParser> parsers;

    public AbstractProcessChainFactory(@Nullable List<ProcessNodeDecorator> decorators,
                                       @Nullable Collection<ProcessNodeParser> parsers) {
        if (null != decorators) {
            AnnotationAwareOrderComparator.sort(decorators);
        }
        this.decorators = decorators;
        this.parsers = parsers;
    }

    public AbstractProcessChainFactory() {
        this(null, null);
    }

    public <T extends ProcessChain> T createProcessChain(String chainId, List<ProcessNode> processNodes) {
        ProcessNode root = resolveRootNode(processNodes);
        return (T) createChain(chainId, root);
    }

    public <T extends ProcessChain> T createProcessChain(String chainId,
                                                         List<NodeConf> nodeConfList,
                                                         List<ProcessNode> processNodes) {
        ProcessNode root = resolveRootNode(nodeConfList, processNodes);
        return (T) createChain(chainId, root);
    }

    public <T extends ProcessChain, E> T parseProcessChain(String chainId, E confObject) {
        if (CollectionUtil.isEmpty(parsers)) {
            throw new UnsupportedOperationException("inner parsers is empty");
        }

        for (ProcessNodeParser parser : parsers) {
            if (parser.supports(confObject)) {
                return (T) parseProcessChain(chainId, confObject, parser);
            }
        }

        throw new IllegalArgumentException("no supported parser found");
    }

    public <T extends ProcessChain, E> T parseProcessChain(String chainId, E confObject, ProcessNodeParser<E> parser) {
        ProcessNode processNode = parser.parse(confObject);
        return (T) createChain(chainId, processNode);
    }

    protected ProcessNode resolveRootNode(List<NodeConf> nodeConfList,
                                          List<ProcessNode> processNodes) {
        List<ProcessNode> decoratedNodes = decorateNodeIfNecessary(processNodes);
        return ProcessChoreographer.choreograph(nodeConfList, decoratedNodes);
    }

    protected ProcessNode resolveRootNode(List<ProcessNode> processNodes) {
        List<ProcessNode> decoratedNodes = decorateNodeIfNecessary(processNodes);
        return ProcessChoreographer.choreograph(decoratedNodes);
    }

    protected abstract ProcessChain createChain(String chainId, ProcessNode rootNode);

    private List<ProcessNode> decorateNodeIfNecessary(List<ProcessNode> processNodes) {
        if (CollectionUtil.isEmpty(decorators)) {
            return processNodes;
        }

        List<ProcessNode> decoratedNodes = processNodes;

        // applied all decorators
        for (ProcessNodeDecorator decorator : decorators) {
            decoratedNodes = decoratedNodes.stream()
                    .map(decorator::decorate)
                    .collect(Collectors.toList());
        }

        return decoratedNodes;
    }

}
