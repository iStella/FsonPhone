package com.example.Fson.WorkClass;

import android.util.Log;

import com.example.Fson.BeanClass.LogBean;
import com.example.Fson.BeanClass.RecordBean;
import com.example.Fson.ControlClass.WorkDb;
import com.example.Fson.ToolsClass.CameraHelper;

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

public class toResultUtil {

    public boolean toConvert(String work_id,String user_id, WorkDb workDb) {
        // 数据包报错，暂时注释
        try {
            JSONObject itemObject = new JSONObject();
            JSONObject recordObject = new JSONObject();
            JSONArray logArray = new JSONArray();
            RecordBean recordBean = workDb.getRecord(work_id);
            recordObject.put("id", recordBean.getId());
            recordObject.put("foreign_item_id", recordBean.getForeign_item_id());
            recordObject.put("device_number", recordBean.getDevice_number());
            recordObject.put("order_number", recordBean.getOrder_number());
            recordObject.put("work_id", recordBean.getWork_id());
            recordObject.put("user_id", recordBean.getUser_id());
            recordObject.put("is_worked", recordBean.getIs_worked());
            recordObject.put("timestamp", recordBean.getTimestamp());
            recordObject.put("state", recordBean.getState());


            ArrayList<LogBean> logBeanArrayList = workDb.getLogs(work_id);

            for ( LogBean logBean :logBeanArrayList){
                JSONObject logObject = new JSONObject();
                RecordBean recordBean1=workDb.getRecord(logBean.getWork_id());
                logObject.put("id", logBean.getId());
                logObject.put("foreign_item_id", recordBean1.getForeign_item_id());
                logObject.put("foreign_title_id", logBean.getForeign_title_id());
                logObject.put("foreign_step_id", logBean.getForeign_step_id());
                logObject.put("work_id", logBean.getWork_id());
                logObject.put("bad_url", logBean.getBad_url());
                logObject.put("url", logBean.getUrl());
                logObject.put("user_id", recordBean1.getUser_id());
                logArray.put(logObject);
            }
            itemObject.put("work_id", work_id);
            itemObject.put("record", recordObject);
            itemObject.put("log", logArray);
            String result_string = itemObject.toString();

            StringBuffer stringBuffer = new StringBuffer();
            File data_txt = CameraHelper.getOutputMediaFile_(CameraHelper.MEDIA_TYPE_DATA, work_id);
            //String strContent = result_string + "\r\n";
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(data_txt));
                String line = "";
                while((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line);
                }
                String oldString = stringBuffer.toString();
                Log.e("oldString", oldString);
                if (oldString.equals(null) || oldString.equals("")){
                    result_string = "[" + result_string + "]";
                } else {
                    //oldString = oldString.substring(0, oldString.length() - 1);
                    result_string = oldString  + ",[" + result_string + "]";
                    data_txt.delete();
                }
                data_txt = CameraHelper.getOutputMediaFile_(CameraHelper.MEDIA_TYPE_DATA, work_id);
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
