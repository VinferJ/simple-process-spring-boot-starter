package me.vinfer.simpleprocess.approval.common;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author vinfer
 * @date 2023-04-03 15:00
 */
public interface CodeEnum {

    int getCode();

    default String getDescription() {
        return "";
    }

    default boolean match(Integer code) {
        return code != null && getCode() == code;
    }

    default <T extends CodeEnum> boolean match(T codeEnum) {
        return null != codeEnum && match(codeEnum.getCode());
    }

    @SuppressWarnings("unchecked")
    default boolean isValid(int code) {
        Class<? extends CodeEnum> instanceClass = getClass();
        if (Enum.class.isAssignableFrom(instanceClass)) {
            Class<Enum<? extends CodeEnum>> enumClass =
                    (Class<Enum<? extends CodeEnum>>) instanceClass;
            try {
                Method values = enumClass.getMethod("values");
                Enum<? extends CodeEnum>[] enums =
                        (Enum<? extends CodeEnum>[]) values.invoke(instanceClass);
                return Arrays.stream(enums)
                        .anyMatch(codeEnum -> ((CodeEnum)codeEnum).match(code));
            }catch (Exception e) {
                return false;
            }

        }
        return false;
    }

}
