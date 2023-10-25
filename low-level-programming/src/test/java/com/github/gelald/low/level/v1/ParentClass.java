package com.github.gelald.low.level.v1;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ngwingbun
 * date: 2023/10/22
 */
@Slf4j
public class ParentClass {
    private int parentX;

    public ParentClass() {
        log.info("ParentClass 构造方法执行");
        setX(100);
    }

    public void setX(int x) {
        log.info("ParentClass setX方法执行");
        parentX = x;
    }
}
