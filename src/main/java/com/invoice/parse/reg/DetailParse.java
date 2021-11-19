package com.invoice.parse.reg;

import com.invoice.domain.Invoice;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.AbstractRegularParse;
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
                if (detail.getAmount() != null || detail.getTaxAmount() != null) {
                    detailList.add(detail);
                }
            } else if (2 < itemArrayLength) {
                // 目前发现金额、税额是必须有的。并且税率出现"免税"字样，税额出现过"***"，暂不知是否有其他字样
                detail.setAmount(new BigDecimal(itemArray[itemArrayLength - 3]));
                String taxRateStr = itemArray[itemArrayLength - 2].replaceAll("%", "");
                if (NumberUtils.isDigits(taxRateStr)) {
                    detail.setTaxRate(new BigDecimal(Integer.parseInt(taxRateStr)));
                }
                if (NumberUtils.isDigits(itemArray[itemArrayLength - 1])) {
                    detail.setTaxAmount(new BigDecimal(itemArray[itemArrayLength - 1]));
                }

                for (int j = 0; j < itemArrayLength - 3; j++) {
                    String temp = itemArray[j];
                    if (temp.matches("^(-?\\d+)(\\.\\d+)?$")) {
                        // 如果匹配到数字，第一个是数量，第二个是单价
                        if (null == detail.getCount()) {
                            detail.setCount(new BigDecimal(temp));
                        } else {
                            detail.setPrice(new BigDecimal(temp));
                        }
                    } else {
                        // 如果找到第一个文字，则看下面一个是否也是文字，如果也是，就是 规格和单位，如果只有一个，默认放到单位（目前样本看来，单位的更多）
                        if (itemArrayLength >= j + 1 && !itemArray[j + 1].matches("^(-?\\d+)(\\.\\d+)?$")) {
                            detail.setUnit(itemArray[j + 1]);
                            detail.setModel(temp);
                            j++;
                        } else if (temp.length() > 2) {
                            detail.setModel(temp);
                        } else {
                            detail.setUnit(temp);
                        }
                    }
                }
                detailList.add(detail);
            }
        }

        // 设置明细名称
        setDetailName(parseRequest, detailList);
        Invoice invoice = parseRequest.getInvoice();
        invoice.setDetailList(detailList);
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
