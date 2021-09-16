package com.invoice.domain;

import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 解析器请求参数
 */
@Data
public class ParseRequest {

    /**
     * 发票实体
     */
    private Invoice invoice;

    /**
     * 发票实体字段，用户解析设置属性值
     */
    private Map<String, Field> invoiceField;

    /**
     * 发票文本内容
     */
    private String fullText;

    /**
     * pdf页面宽度
     */
    private int pageWidth;

    /**
     * 第一页
     */
    private PDPage firstPage;

    /**
     * 关键字
     */
    private Map<String, List<PDFKeyWordPosition.Position>> positionListMap;

    /**
     * 其他区域
     */
    private PDFTextStripperByArea most;

    /**
     * 明细区域
     */
    private PDFTextStripperByArea detail;

}
