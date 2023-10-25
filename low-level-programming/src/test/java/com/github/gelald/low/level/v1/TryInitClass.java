package com.github.gelald.low.level.v1;

/**
 * @author ngwingbun
 * date: 2023/10/22
 */
public class TryInitClass {
    public static void main(String[] args) {
        ChildClass cc = new ChildClass();
        // cc 的 setX 方法在构造方法前执行了，所以 cc 的 childX 被覆盖为 1
        cc.printX();
    }
}
