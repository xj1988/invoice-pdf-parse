package com.invoice.parse.reg;


import com.invoice.parse.AbstractRegularParse;

import java.util.regex.Pattern;

/****
 * @description: 购方信息
 * @author: xj
 * @date: 2021/8/30 17:14
 */
public class BuyerInfoRegularParse extends AbstractRegularParse {

    @Override
    protected String getRegular() {
        String reg = "名称:(?<buyerName>\\S*)|纳税人识别号:(?<buyerCode>\\S*)|地址、电话:(?<buyerAddress>\\S*)|开户行及账号:(?<buyerAccount>\\S*)";
        //Pattern pattern = Pattern.compile(reg);
        return reg;
    }

    @Override
    protected String getKeyWord() {
        return "buyer";
    }

}
