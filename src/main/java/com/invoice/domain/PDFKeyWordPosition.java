package com.invoice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * @description: 关键字坐标拾取
 * @author: xj
 * @date: 2021/8/30 17:19
 */
public class PDFKeyWordPosition extends PDFTextStripper {

    private List<String> keywordList;

    private Map<String, List<Position>> positionListMap = new HashMap<>();

    public PDFKeyWordPosition(List<String> keywordList) throws IOException {
        super();
        this.keywordList = keywordList;
    }

    // 获取坐标信息
    public Map<String, List<Position>> getCoordinate(PDDocument document) throws IOException {
        super.setSortByPosition(true);
        super.setStartPage(1);
        super.setEndPage(1);
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        super.writeText(document, dummy);
        return positionListMap;
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) {
        for (String keyword : keywordList) {
            int foundIndex = 0;
            List<Position> positionList = positionListMap.computeIfAbsent(keyword, k -> new ArrayList<>());
            for (int i = 0; i < textPositions.size(); i++) {
                TextPosition textPosition = textPositions.get(i);
                String str = textPosition.getUnicode();
                if (0 < str.length() && str.charAt(0) == keyword.charAt(foundIndex)) {
                    foundIndex++;
                    int count = foundIndex;
                    for (int j = foundIndex; j < keyword.length(); j++) {
                        if (i + j >= textPositions.size()) {
                            break;
                        } else {
                            String s = textPositions.get(i + j).getUnicode();
                            if (0 < s.length() && s.charAt(0) == keyword.charAt(j)) {
                                count++;
                            }
                        }
                    }
                    if (count == keyword.length()) {
                        foundIndex = 0;
                        Position position = new Position();
                        position.setX(textPosition.getX());
                        position.setY(textPosition.getY());
                        positionList.add(position);
                        positionListMap.put(keyword, positionList);
                    }
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Position {

        float x;

        float y;

    }

}

