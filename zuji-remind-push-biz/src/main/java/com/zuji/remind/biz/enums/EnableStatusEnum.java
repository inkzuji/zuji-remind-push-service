package com.zuji.remind.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 常用启用/禁用枚举.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-27 16:21
 **/
@Getter
@AllArgsConstructor
public enum EnableStatusEnum {
    /**
     * 禁用/否。
     */
    DISABLE(0),

    /**
     * 启用/是。
     */
    ENABLE(1);

    private final int code;

    /**
     * 根据编码获取启用状态枚举。
     *
     * @param code 状态编码
     * @return 启用状态枚举，未匹配返回null
     */
    public static EnableStatusEnum getByCode(int code) {
        return Stream.of(EnableStatusEnum.values())
                .filter(v -> v.getCode() == code)
                .findAny()
                .orElse(null);
    }
}
