package com.self_back.utils;

public class PdfConvertUtil {
    public static void convert2Pdf(String sourPath, String targetPath) {
        final String cmd_2_pdf = "soffice --convert-to pdf --outdir \"%s\" \"%s\"";
        String cmd = String.format(cmd_2_pdf, targetPath, sourPath);
        System.out.println("Executing command: " + cmd);
        try {
            ProcessUtils.executeCommand(cmd, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
