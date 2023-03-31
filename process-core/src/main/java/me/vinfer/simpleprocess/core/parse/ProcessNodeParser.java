package me.vinfer.simpleprocess.core.parse;

import me.vinfer.simpleprocess.core.ProcessNode;
import me.vinfer.simpleprocess.core.common.Supportable;

/**
 * parse a specified object to {@link ProcessNode}
 * @author vinfer
 * @date 2023-03-31 15:57
 */
public interface ProcessNodeParser<T> extends Supportable<T> {

    ProcessNode parse(T parseObject);

}
