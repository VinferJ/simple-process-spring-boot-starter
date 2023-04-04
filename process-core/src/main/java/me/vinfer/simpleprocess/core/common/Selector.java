package me.vinfer.simpleprocess.core.common;

/**
 * @author vinfer
 * @date 2023-04-03 11:16
 */
public interface Selector<T, R> {

    R select(T selectKey);

}
