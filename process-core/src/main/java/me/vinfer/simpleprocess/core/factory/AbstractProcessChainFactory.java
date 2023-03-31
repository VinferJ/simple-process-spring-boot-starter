package me.vinfer.simpleprocess.core.factory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import me.vinfer.simpleprocess.core.ProcessChain;
import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.common.NodeConf;
import me.vinfer.simpleprocess.core.decorator.ProcessNodeDecorator;
import me.vinfer.simpleprocess.core.parse.ProcessNodeParser;
import me.vinfer.simpleprocess.core.utils.ProcessChoreographer;
import org.springframework.lang.Nullable;

/**
 * ch
 * @author vinfer
 * @date 2023-03-31 15:20
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractProcessChainFactory {

    private final ProcessNodeDecorator decorator;

    private final Collection<ProcessNodeParser> parsers;

    public AbstractProcessChainFactory(@Nullable ProcessNodeDecorator decorator,
                                       @Nullable Collection<ProcessNodeParser> parsers) {
        this.decorator = decorator;
        this.parsers = parsers;
    }

    public AbstractProcessChainFactory() {
        this(null, null);
    }

    public <T extends ProcessChain> T createStatefulProcessChain(String chainId, List<ProcessNode> processNodes) {
        ProcessNode root = resolveRootNode(processNodes);
        return (T) createChain(chainId, root);
    }

    public <T extends ProcessChain> T createStatefulProcessChain(String chainId,
                                                           List<NodeConf> nodeConfList,
                                                           List<ProcessNode> processNodes) {
        ProcessNode root = resolveRootNode(nodeConfList, processNodes);
        return (T) createChain(chainId, root);
    }

    public <T extends ProcessChain, E> T parseStatefulProcessChain(String chainId, E confObject) {
        if (CollectionUtil.isEmpty(parsers)) {
            throw new UnsupportedOperationException("inner parsers is empty");
        }

        for (ProcessNodeParser parser : parsers) {
            if (parser.supports(confObject)) {
                return (T) parseStatefulProcessChain(chainId, confObject, parser);
            }
        }

        throw new IllegalArgumentException("no supported parser found");
    }

    public <T extends ProcessChain, E> T parseStatefulProcessChain(String chainId, E confObject, ProcessNodeParser<E> parser) {
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
        if (null == decorator) {
            return processNodes;
        }

        return processNodes.stream()
                .map(decorator::decorate)
                .collect(Collectors.toList());
    }

}
