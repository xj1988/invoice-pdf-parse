package com.invoice.parse.reg;


import com.invoice.parse.AbstractRegularParse;

import java.util.regex.Pattern;

/**
 * @author xj
 * @description: 价税合计
 * @date 2021/8/27 16:44
 */
public class TotalAmountRegularParse extends AbstractRegularParse {

    @Override
    protected String getRegular() {
        String reg = "价税合计\\u0028大写\\u0029(?<totalAmountZH>\\S*)\\u0028小写\\u0029¥?(?<totalAmount>\\S*)\\s";
        //Pattern pattern = Pattern.compile(reg);
        return reg;
    }

}
