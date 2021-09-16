package com.invoice.parse.reg;


import com.invoice.domain.Invoice;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.AbstractRegularParse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xj
 * @description: 票据类型
 * @date 2021/8/27 16:50
 */
public class TypeRegularParse extends AbstractRegularParse {

    /**
     * 普通发票
     */
    private final static String ordinaryInvoice = "(?<p>\\S*)通发票";

    /**
     * 专用发票
     */
    private final static String specialInvoice = "(?<p>\\S*)用发票";

    @Override
    public void doParse(ParseRequest parseRequest) {
        String fullText = parseRequest.getFullText();
        Invoice invoice = parseRequest.getInvoice();

        Pattern type00Pattern = Pattern.compile(ordinaryInvoice);
        Matcher m00 = type00Pattern.matcher(fullText);
        if (m00.find()) {
            invoice.setTitle(m00.group("p").replaceAll("(?:国|统|一|发|票|监|制)", "") + "通发票");
            if (null == invoice.getType()) {
                invoice.setType("普通发票");
            }
        } else {
            Pattern type01Pattern = Pattern.compile(specialInvoice);
            Matcher m01 = type01Pattern.matcher(fullText);
            if (m01.find()) {
                invoice.setTitle(m01.group("p").replaceAll("(?:国|统|一|发|票|监|制)", "") + "用发票");
                if (null == invoice.getType()) {
                    invoice.setType("专用发票");
                }
            }
        }
    }

}
