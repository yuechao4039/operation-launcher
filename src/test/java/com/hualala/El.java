package com.hualala;

import com.dld.hll.financial.core.util.DataMap;
import com.google.common.collect.Maps;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yuechao 2018/7/16
 */
public class El {

    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();

        Map<String, String> map = Maps.newHashMap();
        map.put("age", "13");
        map.put("name", "Jack");
        Expression exp = parser.parseExpression("['name']");
        String value = (String) exp.getValue(map);
        System.out.println(value);

        DataMap req = new DataMap();

        DataMap header =new DataMap();
        header.put("trcid", "xxxx333");
        header.put("debug", false);
        header.put("zhCn", "中华人民");
        req.put("header", header);

        Map<String, Object> context = new HashMap<>();
        context.put("req", req.toMap());

        Expression expression = parser.parseExpression("['req']['header']['trcid']");
        String trcid = (String) expression.getValue(context);
        System.out.println(trcid);


        Expression express = parser.parseExpression("T(com.dld.hll.financial.common.pinyin.PinYinUtils).getPinYinHeadChar(['req']['header']['zhCn'], true)");
        System.out.println(express.getValue(context));
    }

}
