package me.vinfer.simpleprocess.core.common;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author vinfer
 * @date 2023-03-29 17:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ConditionNodeConf extends NodeConf{

    private List<? extends NodeConf> matchedNodeConfList;
    private List<? extends NodeConf> unmatchedNodeConfList;

    public ConditionNodeConf(String processId,
                             List<? extends NodeConf> matchedNodeConfList,
                             List<? extends NodeConf> unmatchedNodeConfList) {
        super(processId);
        this.matchedNodeConfList = matchedNodeConfList;
        this.unmatchedNodeConfList = unmatchedNodeConfList;
    }



}
