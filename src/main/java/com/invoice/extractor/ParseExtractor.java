package com.invoice.extractor;

import com.invoice.domain.Invoice;
import com.invoice.domain.PDFKeyWordPosition;
import com.invoice.domain.ParseChain;
import com.invoice.domain.ParseRequest;
import com.invoice.parse.reg.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author xj
 * @description: 解析执行器
 * @date 2021/8/27 16:47
 */
public class ParseExtractor {

    static {
        ParseChain.addParse(new HeadRegularParse());
        ParseChain.addParse(new TypeRegularParse());
        ParseChain.addParse(new TaxAmountRegularParse());
        ParseChain.addParse(new TotalAmountRegularParse());
        ParseChain.addParse(new BottomRegularParse());

        // 添加关键字区域
        ParseChain.addParse(new AddKeyWordsPositionParse());
        // 根据关键字区域添加区域
        ParseChain.addParse(new AddRegionParse());

        ParseChain.addParse(new PasswordRegularParse());
        ParseChain.addParse(new BuyerInfoRegularParse());
        ParseChain.addParse(new SellerRegularParse());
        ParseChain.addParse(new DetailParse());
    }

    /**
     * 关键字
     */
    private static final List<String> keyWords = Arrays.asList("机器编号", "税率", "价税合计", "合计", "开票日期", "规格型号",
            "车牌号", "开户行及账号", "密", "码", "区");

    public static Invoice extract(File file) throws IOException {
        // 读出完整的内容
        PDDocument document = PDDocument.load(file);
        PDPage firstPage = document.getPage(0);
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setSortByPosition(true);
        String fullText = textStripper.getText(document);

        // 获取页面大小，找一个基准值，用来修正区域坐标
        int pageWidth = Math.round(firstPage.getCropBox().getWidth());
        if (firstPage.getRotation() != 0) {
            pageWidth = Math.round(firstPage.getCropBox().getHeight());
        }

        // 去除空格，将中文符号换成英文
        String allText = fullText.replaceAll(" ", "").replaceAll("　", "")
                .replaceAll("（", "(").replaceAll("）", ")")
                .replaceAll("￥", "¥").replaceAll("＊", "*")
                .replaceAll("：", ":");

        ParseRequest parseRequest = new ParseRequest();
        parseRequest.setFullText(allText);
        parseRequest.setPageWidth(pageWidth);
        parseRequest.setFirstPage(firstPage);

        PDFTextStripperByArea most = new PDFTextStripperByArea();
        most.setSortByPosition(true);
        parseRequest.setMost(most);

        PDFTextStripperByArea detail = new PDFTextStripperByArea();
        detail.setSortByPosition(true);
        parseRequest.setDetail(detail);

        // 识别关键字坐标
        PDFKeyWordPosition kwp = new PDFKeyWordPosition(keyWords);
        parseRequest.setPositionListMap(kwp.getCoordinate(document));

        // 解析
        Invoice invoice = ParseChain.doParse(parseRequest);
        document.close();
        return invoice;
    }

}
