package com.invoice.parse;

import com.invoice.domain.ParseRequest;

/**
 * @author xj
 * @description: 解析器
 * @date 2021/8/27 16:15
 */
public interface Parse {

    /**
     * 解析
     *
     * @param parseRequest 解析请求
     */
    void doParse(ParseRequest parseRequest);

}
