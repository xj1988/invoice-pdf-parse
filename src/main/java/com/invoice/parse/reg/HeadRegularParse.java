package com.invoice.parse.reg;


import com.invoice.parse.AbstractRegularParse;

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

}
