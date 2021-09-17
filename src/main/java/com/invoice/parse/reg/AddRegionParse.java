package com.invoice.parse.reg;

import com.invoice.domain.PDFKeyWordPosition;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.AbstractRegularParse;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/****
 * @description: 添加区域信息
 * @author: xj
 * @date: 2021/8/30 17:14
 */
public class AddRegionParse extends AbstractRegularParse {

    @Override
    public void doParse(ParseRequest parseRequest) {
        Map<String, List<PDFKeyWordPosition.Position>> positionListMap = parseRequest.getPositionListMap();
        PDFTextStripperByArea most = parseRequest.getMost();
        PDFTextStripperByArea detail = parseRequest.getDetail();
        int pageWidth = parseRequest.getPageWidth();

        // 找出“密码区”的坐标。密码区三个字是独立的，竖着的。需要每个字单独找出来，还有可能有重复的。
        List<PDFKeyWordPosition.Position> mi = positionListMap.get("密");
        List<PDFKeyWordPosition.Position> ma = positionListMap.get("码");
        List<PDFKeyWordPosition.Position> qu = positionListMap.get("区");

        // 密码区x坐标
        int mmqX = 370;
        // 找出三个数组中x相同。定义为差值在5以内的。
        for (PDFKeyWordPosition.Position miP : mi) {
            float x1 = miP.getX();
            for (PDFKeyWordPosition.Position maP : ma) {
                float x2 = maP.getX();
                if (Math.abs(x1 - x2) < 5) {
                    // 认为相同
                    for (PDFKeyWordPosition.Position qP : qu) {
                        float x3 = qP.getX();
                        if (Math.abs(x2 - x3) < 5) {
                            mmqX = Math.round((x1 + x2 + x3) / 3);
                        }
                    }
                }
            }
        }

        // 密码区
        PDFKeyWordPosition.Position taxRate = positionListMap.get("税率").get(0);
        PDFKeyWordPosition.Position machineNumber = positionListMap.get("machineNumber").get(0);
        {
            int x = mmqX + 10;
            int y = Math.round(machineNumber.getY()) + 10;
            int w = pageWidth - mmqX - 10;
            int h = Math.round(taxRate.getY() - 5) - y;
            most.addRegion("password", new Rectangle(x, y, w, h));
        }

        // 购方
        PDFKeyWordPosition.Position buyer = positionListMap.get("buyer").get(0);
        {
            // 开户行及账号的x为参考
            int x = Math.round(buyer.getX()) - 15;
            // 机器编号的y坐标为参考
            int y = Math.round(machineNumber.getY()) + 10;
            // 密码区x坐标为参考
            int w = mmqX - x - 5;
            // 开户行及账号的y坐标为参考
            int h = Math.round(buyer.getY()) - y + 20;
            most.addRegion("buyer", new Rectangle(x, y, w, h));
        }

        // 销方
        PDFKeyWordPosition.Position seller = positionListMap.get("seller").get(0);
        PDFKeyWordPosition.Position totalAmount = positionListMap.get("价税合计").get(0);
        {
            // 开户行及账号为x参考
            int x = Math.round(seller.getX()) - 15;
            // 价税合计的y坐标为参考
            int y = Math.round(totalAmount.getY()) + 10;
            // 密码区的x为参考
            int w = mmqX - x - 5;
            // 开户行及账号的y为参考
            int h = Math.round(seller.getY()) - y + 20;
            most.addRegion("seller", new Rectangle(x, y, w, h));
        }

        // 规格型号
        PDFKeyWordPosition.Position model = positionListMap.get("model").get(0);
        {
            // 规格型号作为x轴
            int x = Math.round(model.getX()) - 13;
            // 用税率的作为y轴
            int y = Math.round(taxRate.getY()) + 5;
            // 价税合计的y坐标 减 税率的y坐标 作为高
            int h = Math.round(totalAmount.getY()) - Math.round(taxRate.getY()) - 25;
            detail.addRegion("detail", new Rectangle(0, y, pageWidth, h));
            most.addRegion("detailName", new Rectangle(0, y, x, h));
            most.addRegion("detailPrice", new Rectangle(x, y, pageWidth, h));
        }

        PDPage firstPage = parseRequest.getFirstPage();
        try {
            most.extractRegions(firstPage);
            detail.extractRegions(firstPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
