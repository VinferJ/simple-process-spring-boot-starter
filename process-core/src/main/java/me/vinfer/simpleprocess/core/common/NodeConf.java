package me.vinfer.simpleprocess.core.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;

/**
 * @author vinfer
 * @date 2023-03-29 17:03
 */
@Data
@NoArgsConstructor
public abstract class NodeConf implements Ordered {

    private int order;
    private String processId;

    public NodeConf(String processId) {
        this.processId = processId;
    }

}
