package com.invoice.parse.reg;


import com.invoice.domain.Invoice;
import com.invoice.parse.AbstractRegularParse;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author xj
 * @description: 发票底部信息
 * @date 2021/8/27 16:45
 */
public class BottomRegularParse extends AbstractRegularParse {

    @Override
    protected String getRegular() {
        String reg = "收款人:(?<payee>\\S*)复核:(?<reviewer>\\S*)开票人:(?<drawer>\\S*)销售方";
        //Pattern pattern = Pattern.compile(reg);
        return reg;
    }

    @Override
    protected void check(String fullText, Invoice invoice, Map<String, Field> invoiceField) {
        // 有样本没有销售方盖章区域
        if (StringUtils.isEmpty(invoice.getPayee()) && StringUtils.isEmpty(invoice.getReviewer()) && StringUtils.isEmpty(invoice.getDrawer())) {
            String reg = "收款人:(?<payee>\\S*)复核:(?<reviewer>\\S*)开票人:(?<drawer>\\S*)";
            doSetInvoice(invoice, reg, fullText, invoiceField);
        }
    }
}
