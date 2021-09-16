package com.invoice.parse.reg;


import com.invoice.domain.Invoice;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.AbstractRegularParse;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xj
 * @description: 合计金额
 * @date 2021/8/27 16:43
 */
public class TaxAmountRegularParse extends AbstractRegularParse {

    @Override
    public void doParse(ParseRequest parseRequest) {
        Invoice invoice = parseRequest.getInvoice();
        String fullText = parseRequest.getFullText();

        String reg = "合计¥?(?<amount>[^¥\\s\\*]*)(?:¥?(?<taxAmount>\\S*)|\\*+)\\s";
        //Pattern.compile(reg);
        doSetAmount(invoice, reg, fullText);
        if (null == invoice.getAmount()) {
            reg = "合\\u0020*计\\u0020*¥?(?<amount>[^ ]*)\\u0020+¥?(?:(?<taxAmount>\\S*)|\\*+)\\s";
            doSetAmount(invoice, reg, fullText);
        }
    }

    private void doSetAmount(Invoice invoice, String reg, String fullText) {
        Matcher matcher = Pattern.compile(reg).matcher(fullText);
        if (!matcher.find()) {
            return;
        }

        String amount = matcher.group("amount");
        if (NumberUtils.isCreatable(amount)) {
            invoice.setAmount(new BigDecimal(amount));
        }
        String taxAmount = matcher.group("taxAmount");
        if (NumberUtils.isCreatable(taxAmount)) {
            invoice.setTaxAmount(new BigDecimal(taxAmount));
        }
    }

}
