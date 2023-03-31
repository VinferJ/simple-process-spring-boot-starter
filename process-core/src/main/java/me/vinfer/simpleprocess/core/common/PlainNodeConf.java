package me.vinfer.simpleprocess.core.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author vinfer
 * @date 2023-03-29 17:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlainNodeConf extends NodeConf{

    public static PlainNodeConf of(String processId) {
        PlainNodeConf pnc = new PlainNodeConf();
        pnc.setProcessId(processId);
        return pnc;
    }

}
