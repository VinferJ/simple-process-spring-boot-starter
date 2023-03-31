package me.vinfer.simpleprocess.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author vinfer
 * @date 2023-03-31 16:24
 */
public interface ProcessChain extends ProcessNode{

    ProcessNode getRoot();

    default List<ProcessNode> getProcessNodes() {
        ProcessNode root = getRoot();
        if (root == null){
            return Collections.emptyList();
        }

        List<ProcessNode> processNodes = new ArrayList<>();
        ProcessNode node = root;
        while (null != node) {
            processNodes.add(node);
            node = node.next();
        }

        return processNodes;
    }

}
