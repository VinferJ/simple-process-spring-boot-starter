package me.vinfer.simpleprocess.core.common;

/**
 * mark an interface instance as {@link Component},
 * this component can be selected by {@link ComponentSelector}
 *
 * @author vinfer
 * @date 2023-04-03 11:50
 * @see ComponentSelector
 */
public interface Component {

    default String name() {
        String simpleName = getClass().getSimpleName();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    default String getDescription() {
        return "";
    }

}
