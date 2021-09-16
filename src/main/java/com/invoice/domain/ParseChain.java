package com.invoice.domain;


import cn.hutool.core.util.ReflectUtil;
import com.invoice.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * @description: 解析器链
 * @author: xj
 * @date: 2021/8/30 17:18
 */
public class ParseChain {

    private static final Logger logger = LoggerFactory.getLogger(ParseChain.class);

    /**
     * 解析实例
     */
    private static final List<Parse> parses = new ArrayList<>();

    /**
     * 添加解析器
     *
     * @param parse 解析器
     */
    public static void addParse(Parse parse) {
        parses.add(parse);
    }

    /**
     * 执行解析方法
     *
     * @param parseRequest 处理请求
     */
    public static Invoice doParse(ParseRequest parseRequest) {
        parseRequest.setInvoice(new Invoice());
        parseRequest.setInvoiceField(getFieldSetMethod(Invoice.class));
        for (Parse parse : parses) {
            try {
                parse.doParse(parseRequest);
            } catch (Exception e) {
                //e.printStackTrace();
                logger.warn("parse error", e);
            }
        }
        return parseRequest.getInvoice();
    }

    /**
     * 反射获取票据包装类的字段
     *
     * @param clazz 类
     */
    private static Map<String, Field> getFieldSetMethod(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        Map<String, Field> fieldSetMethod = new HashMap<>();
        for (Field declaredField : declaredFields) {
            String declaredFieldName = declaredField.getName();
            fieldSetMethod.put(declaredFieldName, declaredField);
        }
        return fieldSetMethod;
    }

}
