package com.self_back.utils;

public class Test {
    public static void main(String[] args) throws Exception {
        String ppturl = "E:\\selfPan_Upload\\uploads\\1\\英语诗歌No man is an island.pptx";
        String pdfurl = "E:\\selfPan_Upload\\uploads\\1";
        PdfConvertUtil.convert2Pdf(ppturl, pdfurl);
    }
}
