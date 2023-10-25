package com.github.gelald.low.level.v1;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ngwingbun
 * date: 2023/10/22
 */
@Slf4j
public class ChildClass extends ParentClass {
    private int childX = 1;

    public ChildClass() {
        super();
        log.info("ChildClass 构造方法执行");
    }

    @Override
    public void setX(int x) {
        log.info("ChildClass setX方法执行");
        super.setX(x);
        childX = x;
        System.out.println("ChildX 被赋值为 " + childX);
    }

    public void printX() {
        System.out.println("ChildX = " + childX);
    }

}
