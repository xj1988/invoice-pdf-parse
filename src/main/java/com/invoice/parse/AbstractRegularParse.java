package com.invoice.parse;

import cn.hutool.core.util.ReflectUtil;
import com.invoice.domain.Invoice;
import com.invoice.domain.ParseRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xj
 * @description: 解析pdf抽象类
 * @date 2021/8/27 16:16
 */
public abstract class AbstractRegularParse implements Parse {

    private final static Logger logger = LoggerFactory.getLogger(AbstractRegularParse.class);

    /**
     * 属性占位符
     */
    private final static String regex = "\\?\\<(.*?)\\>";

    /**
     * 获取正则表达式
     */
    protected String getRegular() {
        return null;
    }

    /**
     * 获取关键字
     */
    protected String getKeyWord() {
        return null;
    }

    @Override
    public void doParse(ParseRequest parseRequest) {
        Invoice invoice = parseRequest.getInvoice();
        String fullText = parseRequest.getFullText();
        PDFTextStripperByArea most = parseRequest.getMost();

        String regular = getRegular();
        String keyWord = getKeyWord();
        if (!StringUtils.isEmpty(keyWord)) {
            fullText = replace(most.getTextForRegion(keyWord));
        }
        doSetInvoice(invoice, regular, fullText, parseRequest.getInvoiceField());
    }

    /**
     * 填充invoice属性
     *
     * @param invoice  票据
     * @param regular  正则
     * @param fullText pdf所有文本内容
     */
    public void doSetInvoice(Invoice invoice, String regular, String fullText, Map<String, Field> fieldSetMethod) {
        // 提取正则表达式中的key,即Invoice的属性名称
        List<String> keys = getKeys(regex, regular);
        // 匹配key
        Matcher matcher = Pattern.compile(regular).matcher(fullText);
        while (matcher.find()) {
            for (String key : keys) {
                String value = matcher.group(key);
                if (value == null) {
                    continue;
                }
                try {
                    Field field = fieldSetMethod.get(key);
                    ReflectUtil.setFieldValue(invoice, field, value);
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.warn("invoice set value error", e);
                }
            }
        }
    }

    /**
     * 获取正则中的key
     *
     * @param placeholder 占位符
     * @param regular     含有占位符的正则
     */
    private List<String> getKeys(String placeholder, String regular) {
        Pattern pattern = Pattern.compile(placeholder);
        Matcher matcher = pattern.matcher(regular);
        List<String> list = new ArrayList<>();
        int i = 1;
        while (matcher.find()) {
            list.add(matcher.group(i));
        }
        return list;
    }

    public static String replace(String str) {
        return str.replaceAll(" ", "")
                .replaceAll("　", "")
                .replaceAll("：", ":");
    }

}
