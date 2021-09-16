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
        List<String> skipList = new ArrayList<>();
        for (String detailExcludeName : detailExcludeNameArray) {
            Invoice.Detail detail = new Invoice.Detail();
            // 规格型号、单位、数量、单价、金额、税率、税额
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
                // 金额、税率、税额设置值
                String taxRateStr = itemArray[itemArrayLength - 2].replaceAll("%", "");
                if (taxRateStr.matches("[*￥%]")) {
                    continue;
                }
                detail.setAmount(new BigDecimal(itemArray[itemArrayLength - 3]));
                if (NumberUtils.isCreatable(taxRateStr)) {
                    BigDecimal taxRate = new BigDecimal(Integer.parseInt(taxRateStr));
                    detail.setTaxRate(taxRate.divide(new BigDecimal(100)));
                }
                if (NumberUtils.isCreatable(itemArray[itemArrayLength - 1])) {
                    detail.setTaxAmount(new BigDecimal(itemArray[itemArrayLength - 1]));
                }

                for (int j = 0; j < itemArrayLength - 3; j++) {
                    String temp = itemArray[j];
                    if (temp.matches("^(-?\\d+)(\\.\\d+)?$")) {
                        if (null == detail.getCount()) {
                            detail.setCount(new BigDecimal(temp));
                        } else {
                            detail.setPrice(new BigDecimal(temp));
                        }
                    } else {
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
            } else {
                skipList.add(detailExcludeName);
            }
        }

        // 设置明细名称
        setDetailName(parseRequest, detailList, skipList);
        Invoice invoice = parseRequest.getInvoice();
        invoice.setDetailList(detailList);
    }

    private void setDetailName(ParseRequest parseRequest, List<Invoice.Detail> detailList, List<String> skipList) {
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
