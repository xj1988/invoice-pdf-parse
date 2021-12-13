package com.invoice.parse.reg;


import com.invoice.domain.Invoice;
import com.invoice.parse.AbstractRegularParse;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xj
 * @description: 头部信息
 * @date 2021/8/27 16:39
 */
public class HeadRegularParse extends AbstractRegularParse {

    @Override
    protected String getRegular() {
        String reg = "机器编号:(?<machineNumber>\\d{12})" +
                "|发票代码:(?<code>\\d{12})" +
                "|发票号码:(?<number>\\d{8})" +
                "|开票日期:(?<date>\\d{4}年\\d{2}月\\d{2}日)" +
                "|校验码:(?<checkCode>\\d{20}|\\S{4,})";
        //Pattern pattern = Pattern.compile(reg);
        return reg;
    }

    @Override
    protected void check(String fullText, Invoice invoice, Map<String, Field> invoiceField) {
        if (StringUtils.isEmpty(invoice.getCode())) {
            // 有样本显示，票代码被识被提取到标题中
            String invoiceTitle = "通发票(?<code>\\d{12})";
            Matcher code = Pattern.compile(invoiceTitle).matcher(fullText);
            if (code.find()) {
                invoice.setCode(code.group("code"));
            }
        }
    }
}
