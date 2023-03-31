package me.vinfer.simpleprocess.core.parse;

import cn.hutool.core.util.StrUtil;
import me.vinfer.simpleprocess.core.ProcessNode;

/**
 * @author vinfer
 * @date 2023-03-31 15:59
 */
public class JsonProcessNodeParser implements ProcessNodeParser<String> {

    private static final String START_WITH = "{";

    @Override
    public ProcessNode parse(String parseObject) {
        // TODO: 2023/3/31
        return null;
    }

    @Override
    public boolean supports(String checkObject) {
        if (StrUtil.isBlank(checkObject)) {
            return false;
        }

        return (checkObject.startsWith("{") && checkObject.endsWith("}")) ||
                (checkObject.startsWith("[") && checkObject.endsWith("]"));
    }


}
