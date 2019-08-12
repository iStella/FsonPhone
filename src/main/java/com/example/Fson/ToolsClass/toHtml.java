package com.example.Fson.ToolsClass;

import java.io.File;
import java.util.ArrayList;

public class toHtml {
    private static final String mHtmlHead = "<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>报表</title></head><body></table><p> </p>"
            + "<table width=\"566\" height=\"36\" border=\"1\">  <caption>  <strong>标题</strong>  <br />  姓名：魏加栋  日期： 2017-10-31 <br /> </caption> "
            + "<tr> <td height=\"30\">名称</td>    <td>单价</td>    <td>数量</td>  </tr>";
    private static final String mHtmlItem = "<tr> <td height=\"30\">name</td>    <td>price</td>    <td>num</td>  </tr>";
    private static final String mHtmlEnd = "</table></body></html>";

    public static boolean convert(File file, String title, String date, ArrayList<String> mediaarrays) {
        boolean addresult = false;
        String username = "David";
        try {
            String result = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=\"UTF-8\">\n" +
                    "<title>报表</title>\n" +
                    "</head>\n" +
                    "<body style=\"text-align: center;\">\n" +
                    "\t\n" +
                    "\t<table  border=\"1\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:100%;\">\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th colspan=\"2\"><h1>" +  title + "</h1></th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th colspan=\"2\"><h3>操作人：" + username + "&nbsp;&nbsp;&nbsp;&nbsp;日期：" + date + "</h3></th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th colspan=\"2\"><h2>第一步，测量与计算。</h2></th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th colspan=\"2\"><h3>1.1.测量浮动环内径。</h3></th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th><p style=\"size: 16px; font-family: '楷体';\">取出零件AA211-10浮动环，拍下零件型号。</p></th>\n" +
                    "\t\t\t<th>\n" +
                    "\t\t\t\t" + mediaarrays.get(0) + "\n" +
                    "\t\t\t</th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th><p style=\"size: 16px; font-family: '楷体';\">测量零件内径，拍下测量值。参考值为φ70.10。</p></th>\n" +
                    "\t\t\t<th>\n" +
                    "\t\t\t\t" + mediaarrays.get(1) + "\n" +
                    "\t\t\t</th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th colspan=\"2\"><h3>1.2.测量石墨环厚度（代号m）。</h3></th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th><p style=\"size: 16px; font-family: '楷体';\">取出零件AA22-063a石墨环，拍下零件。</p></th>\n" +
                    "\t\t\t<th>\n" +
                    "\t\t\t\t" + mediaarrays.get(2) + "\n" +
                    "\t\t\t</th>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<th><p style=\"size: 16px; font-family: '楷体';\">测量零件厚度，拍摄测量值（参考值为3.7）。</p></th>\n" +
                    "\t\t\t<th>\n" +
                    "\t\t\t\t" + mediaarrays.get(3) + "\n" +
                    "\t\t\t</th>\n" +
                    "\t\t</tr>\n" +
                    "\t</table>\n" +
                    "</body>\n" +
                    "</html>";
            addresult = FileUtil.writeDataToFile(result.getBytes(), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addresult;
    }
}