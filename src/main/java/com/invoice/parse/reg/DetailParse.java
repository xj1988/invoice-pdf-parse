package com.invoice.parse.reg;

import com.invoice.domain.Invoice;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.AbstractRegularParse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/****
 * @description: 明细区域
 * @author: xj
 * @date: 2021/8/30 17:13
 */
public class DetailParse extends AbstractRegularParse {

    @Override
    public void doParse(ParseRequest parseRequest) {
        PDFTextStripperByArea mostArea = parseRequest.getMost();
        // 排除服务名称外明细数组
        String[] detailExcludeNameArray = mostArea.getTextForRegion("detailPrice").split("\n");

        List<Invoice.Detail> detailList = new ArrayList<>();
        for (String detailExcludeName : detailExcludeNameArray) {
            // 清除非法行
            if (detailExcludeName.matches("\\S*(金额|税率|税额|¥|￥)\\S*")) {
                continue;
            }
            Invoice.Detail detail = new Invoice.Detail();
            // 理想情况分割后：规格型号、单位、数量、单价、金额、税率、税额
            String[] itemArray = detailExcludeName.split("\\s+");
            int itemArrayLength = itemArray.length;
            if (2 == itemArrayLength) {
                if (NumberUtils.isCreatable(itemArray[0])) {
                    detail.setAmount(new BigDecimal(itemArray[0]));
                }
                if (NumberUtils.isCreatable(itemArray[1])) {
                    detail.setTaxAmount(new BigDecimal(itemArray[1]));
                }
            } else if (2 < itemArrayLength) {
                // 目前发现金额、税额是必须有的。并且税率出现"免税"字样，税额出现过"*"，暂不知是否有其他字样
                // 税额
                String taxAmount = itemArray[itemArrayLength - 1];
                if (NumberUtils.isCreatable(taxAmount)) {
                    detail.setTaxAmount(new BigDecimal(taxAmount));
                }
                // 税率
                String taxRate = itemArray[itemArrayLength - 2].replaceAll("%", "");
                if (NumberUtils.isCreatable(taxRate)) {
                    detail.setTaxRate(new BigDecimal(taxRate));
                }
                // 金额
                String amount = itemArray[itemArrayLength - 3];
                if (NumberUtils.isCreatable(amount)) {
                    detail.setAmount(new BigDecimal(amount));
                }
                // 单价
                if (itemArrayLength >= 4 && NumberUtils.isCreatable(itemArray[itemArrayLength - 4])) {
                    detail.setPrice(new BigDecimal(itemArray[itemArrayLength - 4]));
                }
                // 数量
                if (itemArrayLength >= 5 && NumberUtils.isDigits(itemArray[itemArrayLength - 5])) {
                    detail.setCount(new BigDecimal(itemArray[itemArrayLength - 5]));
                }
                // 单位
                if (itemArrayLength >= 6 && !itemArray[itemArrayLength - 6].matches("^(-?\\d+)(\\.\\d+)?$")) {
                    detail.setUnit(itemArray[itemArrayLength - 6]);
                }
                // 规格型号
                if (itemArrayLength >= 7 && !itemArray[itemArrayLength - 7].matches("^(-?\\d+)(\\.\\d+)?$")) {
                    detail.setModel(itemArray[itemArrayLength - 7]);
                }
            }
            detailList.add(detail);
        }

        // 设置明细名称
        setDetailName(parseRequest, detailList);
        Invoice invoice = parseRequest.getInvoice();
        // 排除没有识别完整的明细
        excludeDetails(detailList);
        invoice.setDetailList(detailList);
    }

    private void excludeDetails(List<Invoice.Detail> detailList) {
        detailList.removeIf(next -> StringUtils.isEmpty(next.getName()));
    }

    private void setDetailName(ParseRequest parseRequest, List<Invoice.Detail> detailList) {
        PDFTextStripperByArea detailArea = parseRequest.getDetail();
        PDFTextStripperByArea mostArea = parseRequest.getMost();

        String[] detailNameArray = mostArea.getTextForRegion("detailName").replaceAll("\r", "")
                .split("\n");

        String[] detailArray = detailArea.getTextForRegion("detail").replaceAll("\r", "")
                .split("\n");

        for (int i = 0, j = detailNameArray.length, k = detailArray.length, m = detailList.size(); i < j; i++) {
            String separateName = detailNameArray[i];
            if (i >= k) {
                break;
            }
            if (!detailArray[i].contains(separateName)) {
                continue;
            }
            if (i < m) {
                Invoice.Detail temp = detailList.get(i);
                temp.setName(separateName);
            } else {
                Invoice.Detail temp = new Invoice.Detail();
                temp.setName(separateName);
                detailList.add(temp);
            }
        }
    }

}
