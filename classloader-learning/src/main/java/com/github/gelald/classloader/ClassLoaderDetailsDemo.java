package com.github.gelald.classloader;

public class ClassLoaderDetailsDemo {
    public static void main(String[] args) {
        // JDK 9 查看 Bootstrap 加载的模块
        ModuleLayer.boot().modules().forEach(m -> System.out.println("Bootstrap ClassLoader load: " + m.getName()));

        System.out.println("====================================");
        System.out.println("====================================");

        // 获取 Platform ClassLoader
        ClassLoader platformClassLoader = ClassLoader.getPlatformClassLoader();
        // JDK 9 之后用 Platform ClassLoader 替换了 Extension ClassLoader
        System.out.println("Platform ClassLoader: " + platformClassLoader);

        System.out.println("====================================");
        System.out.println("====================================");

        System.out.println("Platform ClassLoader's parent => Bootstrap ClassLoader: " + platformClassLoader.getParent());

        System.out.println("====================================");
        System.out.println("====================================");

        // 获取 App ClassLoader
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        // JDK 9 之前的实现：sun.misc.Launcher$AppClassLoader
        // JDK 9 之后的实现：jdk.internal.loader.ClassLoaders$AppClassLoader
        System.out.println("App ClassLoader: " + appClassLoader);
    }
}
