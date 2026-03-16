package com.github.gelald.jvm.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JDK17 启动参数
 * java -Xms256m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./oom-output/MemoryLeakIssue/dump_demo.hprof "-Xlog:gc*:file=./oom-output/MemoryLeakIssue/gc_demo.log:time,uptime,level,tags"
 * JDK8 需要把GC日志部分修改为 -verbose:gc -Xloggc:./oom-output/MemoryLeakIssue/gc_demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps
 */
public class MemoryLeakIssue {
    // 静态集合，生命周期与 JVM 相同，会导致其中的对象无法被回收
    private static final List<String> LEAKY_CACHE = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始模拟内存泄漏...");
        System.out.println("当前最大堆内存: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");

        int count = 0;

        /*for (int i = 0; i < 10; i++) {
            String data = UUID.randomUUID().toString() + "_padding_data_to_consume_memory_" + count;
            LEAKY_CACHE.add(data);

            count++;
        }

        while (true) {

        }*/

        while (true) {
            // 制造大对象字符串，快速消耗内存
            String data = UUID.randomUUID().toString() + "_padding_data_to_consume_memory_" + count;
            LEAKY_CACHE.add(data);

            count++;

            if (count % 10000 == 0) {
                System.out.println("已添加对象数量: " + count + ", 当前列表大小: " + LEAKY_CACHE.size());
                // 稍微休眠，方便观察 GC 日志频率
                Thread.sleep(50);
            }
        }
    }
}

