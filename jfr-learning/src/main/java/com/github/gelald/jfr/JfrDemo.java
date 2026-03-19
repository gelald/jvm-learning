package com.github.gelald.jfr;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JfrDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("JFR Demo 开始运行...");
        System.out.println("PID: " + ProcessHandle.current().pid());
        System.out.println("可以通过以下命令开始 JFR 录制：");
        System.out.println("  jcmd " + ProcessHandle.current().pid() + " JFR.start name=demo duration=30s filename=jfr-demo.jfr");
        System.out.println();

        // 模拟业务场景：税务申报消息处理
        for (int i = 0; i < 20; i++) {
            // 每次处理一条申报消息
            processTaxDeclaration(i);

            if (i % 10 == 0) {
                System.out.println("已处理 " + i + " 条申报消息...");
            }
        }

        System.out.println("JFR Demo 运行结束！");
        System.out.println("请使用 JMC 打开 jfr-demo.jfr 文件分析");
    }

    /**
     * 模拟处理税务申报消息
     * 这个方法会创建大对象，用于演示 JFR 捕获分配热点
     */
    private static void processTaxDeclaration(int index) throws InterruptedException {
        // 1. 创建申报 DTO（模拟从数据库加载）
        TaxAuditDTO dto = buildTaxAuditDTO(index);

        System.out.println("dto size: " + ClassLayout.parseInstance(dto).instanceSize());

        // 2. 序列化（这里是大对象分配的关键点）
        byte[] payload = serializeToJson(dto);

        System.out.println("payload size: " + ClassLayout.parseInstance(payload).instanceSize());

        // 3. 方法结束，dto 和 payload 都会被回收（短命对象）
        // 模拟业务间隔
        TimeUnit.MILLISECONDS.sleep(300);
    }

    /**
     * 构建税务审核 DTO（模拟组合 3 个表的数据）
     */
    private static TaxAuditDTO buildTaxAuditDTO(int index) {
        TaxAuditDTO dto = new TaxAuditDTO();

        // 企业基本信息（来自 t_enterprise 表）
        dto.enterpriseId = "ENT_" + index;
        dto.enterpriseName = "企业名称_" + index;
        dto.taxId = "91110000MA00ABCD" + index;

        // 申报基本信息（来自 t_tax_declaration 表）
        dto.declarationId = "DEC_" + index;
        dto.period = "2024-03";
        dto.totalTax = 10000.00 + index;

        // 发票明细列表（来自 t_invoice_detail 表）← 大字段来源
        dto.invoices = new ArrayList<>();
        // 平均每个申报有 150 条发票
        for (int i = 0; i < 150; i++) {
            InvoiceDetail invoice = new InvoiceDetail();
            invoice.invoiceId = "INV_" + index + "_" + i;
            invoice.invoiceCode = "12345678";
            invoice.invoiceNumber = "12345678";
            invoice.totalAmount = 1000.00 + i;
            // goodsInfo 是 JSON 格式的货物详情，模拟 2KB 的数据
            invoice.goodsInfo = buildGoodsInfo(i);  // 每条约 2KB
            dto.invoices.add(invoice);
        }

        return dto;
    }

    /**
     * 构建货物信息（模拟 JSON 格式的富文本）
     */
    private static String buildGoodsInfo(int index) {

        return "{\"goodsName\":\"商品_" + index + "\"," +
                "\"goodsDesc\":\"" +

                // 模拟商品详情，约 2KB
                "这是一段商品描述文字，包含商品的详细信息、规格参数、使用说明等内容。".repeat(100) +
                "\",\"specs\":{\"color\":\"红色\",\"size\":\"L\",\"weight\":\"500g\"}}";
    }

    /**
     * 序列化为 JSON（模拟大对象分配）
     */
    private static byte[] serializeToJson(TaxAuditDTO dto) {
        // 简单模拟 JSON 序列化过程
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"enterpriseId\":\"").append(dto.enterpriseId).append("\",");
        json.append("\"enterpriseName\":\"").append(dto.enterpriseName).append("\",");
        json.append("\"taxId\":\"").append(dto.taxId).append("\",");
        json.append("\"declarationId\":\"").append(dto.declarationId).append("\",");
        json.append("\"period\":\"").append(dto.period).append("\",");
        json.append("\"totalTax\":").append(dto.totalTax).append(",");
        json.append("\"invoices\":[");

        for (int i = 0; i < dto.invoices.size(); i++) {
            InvoiceDetail inv = dto.invoices.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{");
            json.append("\"invoiceId\":\"").append(inv.invoiceId).append("\",");
            json.append("\"invoiceCode\":\"").append(inv.invoiceCode).append("\",");
            json.append("\"invoiceNumber\":\"").append(inv.invoiceNumber).append("\",");
            json.append("\"totalAmount\":").append(inv.totalAmount).append(",");
            json.append("\"goodsInfo\":\"").append(escapeJson(inv.goodsInfo)).append("\"");
            json.append("}");
        }

        json.append("]}");

        // 这里创建 byte[]，是大对象分配的关键点
        return json.toString().getBytes();
    }

    /**
     * 简单的 JSON 转义
     */
    private static String escapeJson(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n");
    }

    // ========== DTO 定义 ==========

    static class TaxAuditDTO {
        String enterpriseId;
        String enterpriseName;
        String taxId;
        String declarationId;
        String period;
        double totalTax;
        List<InvoiceDetail> invoices;  // 大字段
    }

    static class InvoiceDetail {
        String invoiceId;
        String invoiceCode;
        String invoiceNumber;
        double totalAmount;
        String goodsInfo;  // 大字段（JSON 格式，约 2KB）
    }
}
