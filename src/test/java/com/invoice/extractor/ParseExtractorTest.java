package com.invoice.extractor;


import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.invoice.domain.Invoice;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ParseExtractorTest {

    String path = "C:\\Users\\bosssoft-xj\\Desktop\\文件\\发票\\68483288.pdf";

    String path2 = "/Users/xj/Downloads/税票解析/发票/68483288.pdf";

    @Test
    public void extract() throws IOException {
        parse(new File(path));
    }

    private String all = "C:\\Users\\bosssoft-xj\\Desktop\\文件\\发票";

    private String all2 = "/Users/xj/Downloads/税票解析/发票/";

    @Test
    public void extractAll() throws IOException {
        List<File> files = FileUtil.loopFiles(all);
        for (File file : files) {
            if (file.isFile() && (file.getAbsolutePath().contains("pdf") || file.getAbsolutePath().contains("PDF"))) {
                parse(file);
                System.out.println("---------------------");
            }
        }
    }

    private void parse(File file) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        System.out.println(file.getName());
        Invoice extract = ParseExtractor.getInvoice(file);
        System.out.println(JSONUtil.toJsonStr(extract));
        stopWatch.stop();
        System.out.println("耗时：" + stopWatch.getTotalTimeSeconds());
    }
}