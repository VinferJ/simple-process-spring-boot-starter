package me.vinfer.simpleprocess.core.common;

/**
 * check if this instance supports to handle the object.
 * <p> it can be used as strategy type interface.
 * @author vinfer
 * @date 2023-03-31 16:00
 */
public interface Supportable<T> {

    boolean supports(T checkObject);

}
