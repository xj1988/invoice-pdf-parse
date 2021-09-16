package com.invoice.parse.reg;

import com.invoice.domain.Invoice;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.AbstractRegularParse;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/****
 * @description: 密码
 * @author: xj
 * @date: 2021/8/30 17:15
 */
public class PasswordRegularParse extends AbstractRegularParse {

    @Override
    public void doParse(ParseRequest parseRequest) {
        Invoice invoice = parseRequest.getInvoice();
        PDFTextStripperByArea most = parseRequest.getMost();

        invoice.setPassword(StringUtils.trim(most.getTextForRegion("password")));
    }
}
