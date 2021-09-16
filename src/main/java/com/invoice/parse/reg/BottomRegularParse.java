package com.invoice.parse.reg;


import com.invoice.parse.AbstractRegularParse;

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

}
