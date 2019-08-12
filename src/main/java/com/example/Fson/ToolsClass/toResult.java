package com.example.Fson.ToolsClass;

import com.example.Fson.BeanClass.LogBean;
import com.example.Fson.BeanClass.RecordBean;
import com.example.Fson.ControlClass.WorkDb;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by PC on 2017/11/10.
 */

public class toResult {

    public boolean toConvert(String title, String work_id, WorkDb workDb) {
        try {
            JSONObject itemObject = new JSONObject();
            JSONObject recordObject = new JSONObject();
            JSONArray logArray = new JSONArray();
            RecordBean recordBean = workDb.getRecord(work_id);
            recordObject.put("foreign_item_id", recordBean.getForeign_item_id());
            recordObject.put("work_id", recordBean.getWork_id());
            recordObject.put("is_worked", recordBean.getIs_worked());
            recordObject.put("timestamp", recordBean.getTimestamp());
            ArrayList<LogBean> logBeanArrayList = workDb.getLogs(work_id);
            for ( LogBean logBean :logBeanArrayList){
                JSONObject logObject = new JSONObject();
                logObject.put("foreign_title_id", logBean.getForeign_title_id());
                logObject.put("foreign_step_id", logBean.getForeign_step_id());
                logObject.put("work_id", logBean.getWork_id());
                logObject.put("bad_url", logBean.getBad_url());
                logObject.put("url", logBean.getUrl());
                logArray.put(logObject);
            }
            itemObject.put("work_id", work_id);
            itemObject.put("record", recordObject);
            itemObject.put("log", logArray);
            String result_string = itemObject.toString();

            StringBuffer stringBuffer = new StringBuffer();
            File data_txt = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_DATA, title, work_id);
            //String strContent = result_string + "\r\n";
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(data_txt));
                String line = "";
                while((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line);
                }
                String oldString = stringBuffer.toString();
                if (oldString.equals(null) || oldString.equals("")){
                    result_string = "[" + result_string + "]";
                } else {
                    oldString = oldString.substring(0, oldString.length() - 1);
                    result_string = oldString  + "," + result_string + "]";
                    data_txt.delete();
                }
                data_txt = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_DATA, title, work_id);
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(data_txt));
                bufferedWriter.write(result_string);
                bufferedWriter.flush();
                bufferedReader.close();
                bufferedWriter.close();
                return  true;
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
