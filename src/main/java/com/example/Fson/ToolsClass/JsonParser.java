package com.example.Fson.ToolsClass;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by jiang on 2016/8/3.
 */
public class JsonParser {
    String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//              如果需要多候选结果，解析数组其他字段
//              for(int j = 0; j < items.length(); j++)
//              {
//                  JSONObject obj = items.getJSONObject(j);
//                  ret.append(obj.getString("w"));
//              }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    public static String parseLocalGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            /* 普通查询 */
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    //ret.append("【结果】" + obj.getString("w"));
                    ret.append(obj.getString("w"));
                    //ret.append("\n");
                }
            }
            /* 特殊判定-查询ID */
            for (int i = 0; i < words.length(); i++){
                String slot = words.getJSONObject(i).getString("slot");
                if (slot.equals("<cmdnumber>")){
                    JSONArray cw = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject jw = cw.getJSONObject(0);
                    String w = "select";
                    w += jw.getString("w");
                    if(w.contains("nomatch"))
                    {
                        w = "没有匹配结果";
                    }
                    ret = new StringBuffer();
                    ret.append(w);
                }
            }
            /* 数值采集判定 */
            boolean b = false;
            String ww = "collect";
            for (int i = 0; i < words.length(); i ++){
                String slot = words.getJSONObject(i).getString("slot");
                if (slot.equals("<cmdtype>") || slot.equals("cmdprefix")){
                    b = false;
                    break;
                }
                if (slot.equals("<cmdnumber>")){
                    JSONArray cw = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject jw = cw.getJSONObject(0);
                    ww += jw.getString("w");
                    if(ww.contains("nomatch")) {
                        ww = "没有匹配结果";
                    }
                    b = true;
                }
            }
            if (b){
                ret = new StringBuffer();
                ret.append(ww);
            }
            /* 特殊判定-模糊查询 */
            for (int i = 0; i < words.length(); i++){
                String slot = words.getJSONObject(i).getString("slot");
                if (slot.equals("<cmdmachines>")){
                    JSONArray cw = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject jw = cw.getJSONObject(0);
                    String w = "like";
                    w += jw.getString("w");
                    ret = new StringBuffer();
                    ret.append(w);
                }
            }
            //ret.append("【置信度】" + joResult.optInt("sc"));
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }

}
