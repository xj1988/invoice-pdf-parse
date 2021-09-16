package com.invoice.parse.reg;


import com.invoice.domain.PDFKeyWordPosition;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.AbstractRegularParse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/****
 * @description: 添加关键字坐标信息
 * @author: xj
 * @date: 2021/8/30 17:14
 */
public class AddKeyWordsPositionParse extends AbstractRegularParse {

    @Override
    public void doParse(ParseRequest parseRequest) {
        Map<String, List<PDFKeyWordPosition.Position>> positionListMap = parseRequest.getPositionListMap();

        // 机器编号坐标
        PDFKeyWordPosition.Position machineNumber;
        if (positionListMap.get("机器编号").size() > 0) {
            machineNumber = positionListMap.get("机器编号").get(0);
        } else {
            machineNumber = positionListMap.get("开票日期").get(0);
            machineNumber.setY(machineNumber.getY() + 30);
        }
        positionListMap.put("machineNumber", Collections.singletonList(machineNumber));

        // 规格型号坐标
        PDFKeyWordPosition.Position model;
        if (!positionListMap.get("规格型号").isEmpty()) {
            model = positionListMap.get("规格型号").get(0);
        } else {
            model = positionListMap.get("车牌号").get(0);
            model.setX(model.getX() - 15);
        }
        positionListMap.put("model", Collections.singletonList(model));

        // 开户行及账号坐标
        List<PDFKeyWordPosition.Position> account = positionListMap.get("开户行及账号");
        // 购买方、销售方坐标
        PDFKeyWordPosition.Position buyer, seller;
        if (account.size() < 2) {
            buyer = new PDFKeyWordPosition.Position(51, 122);
            seller = new PDFKeyWordPosition.Position(51, 341);
        } else {
            buyer = account.get(0);
            seller = account.get(1);
        }
        positionListMap.put("buyer", Collections.singletonList(buyer));
        positionListMap.put("seller", Collections.singletonList(seller));
    }
}
