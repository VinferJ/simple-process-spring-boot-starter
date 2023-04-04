package me.vinfer.simpleprocess.core.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vinfer
 * @date 2023-04-03 11:18
 */
public abstract class ComponentSelector<R extends Component> implements Selector<String, R> {

    private final Map<String, R> selectionMap;

    public ComponentSelector(Map<String, R> selectionMap) {
        this.selectionMap = selectionMap;
    }

    public ComponentSelector() {
        this(new HashMap<>());
    }

    public ComponentSelector(Collection<R> selectValues) {
        this(new HashMap<>());
        initSelectionMap(selectValues);
    }

    protected boolean isEagerInit() {
        return true;
    }

    private void initSelectionMap(Collection<R> selectValues) {
        for (R selectValue : selectValues) {
            selectionMap.put(selectValue.name(), selectValue);
        }
    }

    @Override
    public R select(String selectKey) {
        R r = selectionMap.get(selectKey);
        if (null == r) {
            return getDefaultInstance();
        }
        return r;
    }

    public R selectByType(Class<? extends R> componentType) {
        return select(componentType.getName());
    }

    protected R getDefaultInstance() {
        return null;
    }

}
