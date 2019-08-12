package com.example.Fson.ControlClass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.Fson.BeanClass.ItemBean;
import com.example.Fson.BeanClass.LogBean;
import com.example.Fson.BeanClass.RecordBean;
import com.example.Fson.BeanClass.StepBean;
import com.example.Fson.BeanClass.TitleBean;
import com.example.Fson.BeanClass.UserBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2016/11/25.
 */

public class WorkDb extends SQLiteOpenHelper {
    String msg = "Android : ";
    private static String INIT_JSON_STRING;
    private static final String DATABASENAME = "sql.db";
    private static final int DATABASEVERSION = 11;  //K34 14

    private static boolean DEBUG = false;

    public WorkDb(Context context, String init_jsonarray) {
        super(context, DATABASENAME, null, DATABASEVERSION);
        INIT_JSON_STRING = init_jsonarray;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         /* 添加user表 */
        db.execSQL(
                "CREATE TABLE if not exists users(" +
                        UserBean.USER_ID + " varchar(255) DEFAULT NULL, " +
                        UserBean.PASSWORD + " varchar(255) DEFAULT NULL, " +
                        UserBean.ROLE + " varchar(255) DEFAULT NULL, " +
                        UserBean.REALNAME + " varchar(255) DEFAULT NULL, " +
                        UserBean.DEPARTMENT + " varchar(255) DEFAULT NULL )"
        );
        /* 添加item表 */
        db.execSQL(
                "CREATE TABLE if not exists item(" +
                        ItemBean.ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                        ItemBean.NUM + " varchar(255) DEFAULT NULL, " +
                        ItemBean.TITLE + " varchar(255) DEFAULT NULL )"
        );
        /* 添加title表 */
        db.execSQL(
                "CREATE TABLE if not exists title(" +
                        TitleBean.ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                        TitleBean.FOREIGN_ITEM_ID + " varchar(255) DEFAULT NULL, " +
                        TitleBean.NUM + " varchar(255) DEFAULT NULL, " +
                        TitleBean.TITLE + " varchar(255) DEFAULT NULL )"
        );
        /* 添加step表 */
        db.execSQL(
                "CREATE TABLE if not exists step(" +
                        StepBean.ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                        StepBean.FOREIGN_ITEM_ID + " varchar(255) DEFAULT NULL, " +
                        StepBean.FOREIGN_TITLE_ID + " varchar(255) DEFAULT NULL, " +
                        StepBean.NUM + " varchar(255) DEFAULT NULL, " +
                        StepBean.STEP + " varchar(255) DEFAULT NULL, " +
                        StepBean.OPERATE + " varchar(255) DEFAULT NULL, " +
                        StepBean.IMPORTS + " varchar(255) DEFAULT NULL )"
        );
        /* 添加record表 */
        db.execSQL(
                "CREATE TABLE if not exists record(" +
                        RecordBean.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecordBean.FOREIGN_ITEM_ID + " VARCHAR(255) DEFAULT NULL, " +
                        RecordBean.DEVICE_NUMBER + " VARCHAR(255) DEFAULT NULL, " +
                        RecordBean.ORDER_NUMBER + " VARCHAR(255) DEFAULT NULL, " +
                        RecordBean.WORK_ID + " VARCHAR(255) DEFAULT NULL, " +
                        RecordBean.USER_ID + " VARCHAR(255) DEFAULT NULL, " +
                        RecordBean.IS_WORKED + " VARCHAR(255) DEFAULT NULL, " +
                        RecordBean.STATE + " VARCHAR(255) DEFAULT NULL, " +
                        RecordBean.TIMESTAMP + " VARCHAR(255) DEFAULT NULL )"
        );
        /* 添加log表 */
        db.execSQL(
                "CREATE TABLE if not exists log(" +
                        LogBean.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        LogBean.FOREIGN_TITLE_ID + " VARCHAR(255) DEFAULT NULL, " +
                        LogBean.FOREIGN_STEP_ID + " VARCHAR(255) DEFAULT NULL, " +
                        LogBean.WORK_ID + " VARCHAR(255) DEFAULT NULL, " +
                        LogBean.BAD_URL + " VARCHAR(255) DEFAULT NULL, " +
                        LogBean.URL + " VARCHAR(255) DEFAULT NULL," +
                        LogBean.USER_ID + " VARCHAR(255) DEFAULT NULL )"
        );
        /* 标记stap，临时使用的表 */
        db.execSQL(
                "CREATE TABLE if not exists step_tab(" +
                        "id INTEGER DEFAULT NULL, " +
                        "step_order INTEGER DEFAULT NULL," +
                        "user_id INTEGER DEFAULT NULL)"
        );
        upDateDB(db);
    }

    /* 更新Class */
    public void upDateDB(SQLiteDatabase db) {
        try {
            JSONObject jsonObject = new JSONObject(INIT_JSON_STRING);
//            JSONArray jsonArray =jsonObject.getJSONArray("Item");
            JSONArray titleArray = jsonObject.getJSONArray("Title");
            JSONArray stepArray = jsonObject.getJSONArray("Step");
            JSONArray usersrray = jsonObject.getJSONArray("Users");
            JSONArray itemarray = jsonObject.getJSONArray("Item");
            String old_item_id = "-1";
            int index = 0;
//            for (int i = 0; i < jsonArray.length(); i ++) {
//                JSONObject itemobject = jsonArray.getJSONObject(i);
//                if (!old_item_id.equals(itemobject.getString("FOREIGN_ITEM_ID"))){
//                    old_item_id = itemobject.getString("FOREIGN_ITEM_ID");
//                    JSONObject itemObject = new JSONObject();
//                    itemObject.put("num", old_item_id);
//                    itemObject.put("title", itemobject.getString("title"));
//                    itemObject.put("content", new JSONArray());
//                    itemarray.put(itemObject);
//                }
//                if (itemarray.length() > 0) itemarray.getJSONObject(itemarray.length() - 1).getJSONArray("content").put(itemobject);
//            }
            for (int i = 0; i < usersrray.length(); i++) {

                JSONObject usersobject = usersrray.getJSONObject(i);
                Log.e("",usersobject.get("user").toString());
                db.execSQL("insert into users(user_id, password, role, realname, department) values(?, ?, ?, ?, ?)",
                        new Object[]{usersobject.get("user"), usersobject.get("password"), usersobject.get("role"), usersobject.get("realName"), usersobject.get("department")});
            }
            for (int i = 0; i < titleArray.length(); i++) {
                /* insert---item */
                JSONObject titleobject = titleArray.getJSONObject(i);
                db.execSQL("insert into title(foreign_item_id, num, title) values(?, ?, ?)",
                        new Object[]{titleobject.get("foreignItemId"), titleobject.get("num"), titleobject.get("title")});
            }
            for (int i = 0; i < stepArray.length(); i++) {
                /* insert---item */
                JSONObject stepobject = stepArray.getJSONObject(i);
                db.execSQL("insert into step(foreign_item_id, foreign_title_id, num, step, operate, imports) values(?, ?, ?, ?, ?, ?)",
                        new Object[]{stepobject.get("foreignItemId"), stepobject.get("foreignTitleId"), stepobject.get("num"), stepobject.get("step"), stepobject.get("operate"), stepobject.get("imports")});
            }

            for (int i = 0; i < itemarray.length(); i++) {
                /* insert---item */
                JSONObject itemobject = itemarray.getJSONObject(i);
                db.execSQL("insert into item(num, title) values(?, ?)",
                        new Object[]{itemobject.get("num"), itemobject.get("title")});
//                JSONArray contentarray1 = itemobject.getJSONArray("content");
//                JSONArray titlearray = new JSONArray();
//                index = 0;
//                for (int j = 0; j < contentarray1.length(); j ++){
//                    JSONObject titleobjet = contentarray1.getJSONObject(j);
//                    if (!titleobjet.getString("TITLE").equals("null") && !titleobjet.getString("TITLE").equals("")){
//                        index ++;
//                        JSONObject titleObject = new JSONObject();
//                        titleObject.put("NUM", String.valueOf(index));
//                        titleObject.put("TITLE", titleobjet.getString("TITLE"));
//                        titleObject.put("content", new JSONArray());
//                        titlearray.put(titleObject);
//                    }
//                    if(titlearray.length() > 0) titlearray.getJSONObject(titlearray.length() - 1).getJSONArray("content").put(titleobjet);
//                }
//
//                for (int k = 0; k < titlearray.length(); k++){
//                    /* insert---title */
//                    JSONObject titleobject = titlearray.getJSONObject(k);
//                    db.execSQL("insert into title(foreign_item_id, num, title) values(?, ?, ?)",
//                            new Object[]{itemobject.get("num"), titleobject.get("NUM"), titleobject.get("TITLE")});
//                    JSONArray contentarray2 = titleobject.getJSONArray("content");
//                    index = 0;
//                    for (int l = 0; l < contentarray2.length(); l ++){
//                        index ++;
//                        /* insert---step */
//                        JSONObject stepobject = contentarray2.getJSONObject(l);
//                        db.execSQL("insert into step(foreign_item_id, foreign_title_id, num, step, operate, imports) values(?, ?, ?, ?, ?, ?)",
//                                new Object[]{itemobject.get("num"), titleobject.get("NUM"), String.valueOf(index), stepobject.get("STEP"), stepobject.get("OPERATE"), stepobject.get("IMPORTS")});
//                    }
//                }
            }
            for (int i = 0; i < itemarray.length(); i++) {
                /* 遍历用户数量 */
                for (int j = 0; j < usersrray.length(); j++) {
                    JSONObject itemobject = itemarray.getJSONObject(i);
                    JSONObject usersobject = usersrray.getJSONObject(j);
                    db.execSQL("insert into step_tab values(?, 0, ?)", new Object[]{itemobject.get("num"), usersobject.get("user")});
                }

            }
            if (DEBUG) Log.d(msg, "重置数据库成功！");
        } catch (Exception e) {
            if (DEBUG) Log.d(msg, "重置数据库失败！");
            if (DEBUG) Log.d(msg, e.toString());
            e.printStackTrace();
        }
    }

    /* 更新数据库 */
    public void upDate() {
        SQLiteDatabase db = getReadableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS log");
//        db.execSQL("DROP TABLE IF EXISTS record");
        db.execSQL("DROP TABLE IF EXISTS step_tab");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS step");
        db.execSQL("DROP TABLE IF EXISTS title");
        db.execSQL("DROP TABLE IF EXISTS item");
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(msg, String.valueOf(oldVersion));
        Log.e(msg, String.valueOf(newVersion));
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS step_tab");
            db.execSQL("DROP TABLE IF EXISTS log");
            db.execSQL("DROP TABLE IF EXISTS record");
            db.execSQL("DROP TABLE IF EXISTS step");
            db.execSQL("DROP TABLE IF EXISTS title");
            db.execSQL("DROP TABLE IF EXISTS item");
            onCreate(db);
        }
    }

    /* 清除false */
    public void workToTrue() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update record set is_worked = 'true' where id > 0");
        if (DEBUG) Log.d(msg, "The log workToTrue() event");
    }

    /* 查询item */
    public ItemBean getItem(Integer num) {
        /* 如果只对数据进行读取，建议使用此方法 */
        SQLiteDatabase db = getReadableDatabase();
        ItemBean itemBean = new ItemBean();
        /* 得到游标 */
        Cursor cursor = db.rawQuery("select * from item where num = ?",
                new String[]{num.toString()});
        if (cursor.moveToFirst()) {
            itemBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            itemBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            itemBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            cursor.close();
            if (DEBUG) Log.d(msg, "The getItem() event: " + itemBean);
            return itemBean;
        }
        return itemBean;
    }

    /* 查询items */
    public ArrayList<ItemBean> getItems() {
        /* 如果只对数据进行读取，建议使用此方法 */
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<ItemBean> itemBeanArrayList = new ArrayList<>();
        /* 得到游标 */
        Cursor cursor = db.rawQuery("select * from item order by id asc", null);
        while (cursor.moveToNext()) {
            ItemBean itemBean = new ItemBean();
            itemBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            itemBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            itemBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            itemBeanArrayList.add(itemBean);
        }
        cursor.close();
        if (DEBUG) Log.d(msg, "item : The getItems() event: " + itemBeanArrayList);
        return itemBeanArrayList;
    }

    /* 查询step */
    public StepBean getStep(Integer foreign_item_id, Integer foreign_title_id, Integer num) {
        SQLiteDatabase db = getReadableDatabase();
        StepBean stepBean = new StepBean();
        Cursor cursor = db.rawQuery("select * from step where foreign_item_id = ? and foreign_title_id = ? and num = ?",
                new String[]{foreign_item_id.toString(), foreign_title_id.toString(), num.toString()});
        if (cursor.moveToFirst()) {
            stepBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            stepBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            stepBean.setForeign_title_id(cursor.getString(cursor.getColumnIndex("foreign_title_id")));
            stepBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            stepBean.setStep(cursor.getString(cursor.getColumnIndex("step")));
            stepBean.setOperate(cursor.getString(cursor.getColumnIndex("operate")));
            stepBean.setImports(cursor.getString(cursor.getColumnIndex("imports")));
            cursor.close();
            //if (DEBUG) Log.d(msg, "The getStep() event: " + stepBean);
            return stepBean;
        }
        return stepBean;
    }

    public StepBean getStep(Integer foreign_item_id, Integer id) {
        SQLiteDatabase db = getReadableDatabase();
        StepBean stepBean = new StepBean();
        Cursor cursor = db.rawQuery("select * from step where foreign_item_id = ? and id= ?",
                new String[]{foreign_item_id.toString(), id.toString()});
        if (cursor.moveToFirst()) {
            stepBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            stepBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            stepBean.setForeign_title_id(cursor.getString(cursor.getColumnIndex("foreign_title_id")));
            stepBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            stepBean.setStep(cursor.getString(cursor.getColumnIndex("step")));
            stepBean.setOperate(cursor.getString(cursor.getColumnIndex("operate")));
            stepBean.setImports(cursor.getString(cursor.getColumnIndex("imports")));
            cursor.close();
            //if (DEBUG) Log.d(msg, "The getStep() event: " + stepBean);
            return stepBean;
        }
        return stepBean;
    }

    /* 查询titles */
    public ArrayList<TitleBean> getTitles(Integer foreign_item_id) {
        /* 如果只对数据进行读取，建议使用此方法 */
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<TitleBean> titleBeanArrayList = new ArrayList<>();
        /* 得到游标 */
        Cursor cursor = db.rawQuery("select * from title where foreign_item_id = ? order by id asc",
                new String[]{foreign_item_id.toString()});
        while (cursor.moveToNext()) {
            TitleBean titleBean = new TitleBean();
            titleBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            titleBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            titleBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            titleBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            titleBeanArrayList.add(titleBean);
        }
        cursor.close();
        //if (DEBUG) Log.d(msg, "item : The getTitles() event: " + titleBeanArrayList);
        return titleBeanArrayList;
    }

    /* 查询title */
    public TitleBean getTitle(Integer foreign_item_id, Integer title_num) {
        SQLiteDatabase db = getReadableDatabase();
        TitleBean titleBean = new TitleBean();
        Cursor cursor = db.rawQuery("select * from title where foreign_item_id = ? and num = ?",
                new String[]{foreign_item_id.toString(), title_num.toString()});
        if (cursor.moveToFirst()) {
            titleBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            titleBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            titleBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            titleBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            cursor.close();
            //if (DEBUG) Log.d(msg, "The getTitle() event: " + titleBean);
            return titleBean;
        }
        return titleBean;
    }

    /* 查询items */
    public ArrayList<StepBean> getSteps(Integer foreign_item_id, Integer foreign_title_id) {
        /* 如果只对数据进行读取，建议使用此方法 */
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<StepBean> stepBeanArrayList = new ArrayList<>();
        /* 得到游标 */
        Cursor cursor = db.rawQuery("select * from step where foreign_item_id = ? and foreign_title_id = ? order by id asc",
                new String[]{foreign_item_id.toString(), foreign_title_id.toString()});
        while (cursor.moveToNext()) {
            StepBean stepBean = new StepBean();
            stepBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            stepBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            stepBean.setForeign_title_id(cursor.getString(cursor.getColumnIndex("foreign_title_id")));
            stepBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            stepBean.setStep(cursor.getString(cursor.getColumnIndex("step")));
            stepBean.setOperate(cursor.getString(cursor.getColumnIndex("operate")));
            stepBean.setImports(cursor.getString(cursor.getColumnIndex("imports")));
            stepBeanArrayList.add(stepBean);
        }
        cursor.close();
        //if (DEBUG) Log.d(msg, "item : The getSteps() event: " + stepBeanArrayList);
        return stepBeanArrayList;
    }

    /* 查询有异常的step */
    public StepBean getBadStep(Integer foreign_item_id, Integer foreign_title_id, String step_num) {
        SQLiteDatabase db = getReadableDatabase();
        StepBean stepBean = new StepBean();
        Cursor cursor = db.rawQuery("select * from step where foreign_item_id = ? and foreign_title_id = ? and num = ?",
                new String[]{foreign_item_id.toString(), foreign_title_id.toString(), step_num});
        if (cursor.moveToFirst()) {
            stepBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            stepBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            stepBean.setForeign_title_id(cursor.getString(cursor.getColumnIndex("foreign_title_id")));
            stepBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            stepBean.setStep(cursor.getString(cursor.getColumnIndex("step")));
            stepBean.setOperate(cursor.getString(cursor.getColumnIndex("operate")));
            stepBean.setImports(cursor.getString(cursor.getColumnIndex("imports")));
            cursor.close();
            return stepBean;
        }
        return stepBean;
    }

    /* 查询log bad_url step_num title_num item_num */
    public ArrayList<LogBean> getBadLogs(String work_id) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<LogBean> logBeanArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from log where bad_url != '' and work_id = ?  order by id asc", new String[]{work_id});
        while (cursor.moveToNext()) {
            LogBean logBean = new LogBean();
            logBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            logBean.setForeign_title_id(cursor.getString(cursor.getColumnIndex("foreign_title_id")));
            logBean.setForeign_step_id(cursor.getString(cursor.getColumnIndex("foreign_step_id")));
            logBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            logBean.setBad_url(cursor.getString(cursor.getColumnIndex("bad_url")));
            logBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            logBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            logBeanArrayList.add(logBean);
        }
        cursor.close();
        //if (DEBUG) Log.d(msg, "The getLog() event: " + logBeanArrayList);
        return logBeanArrayList;
    }

    /* 模糊查询Steps */
    public ArrayList<StepBean> getSteps(Integer foreign_item_id, Integer foreign_title_id, String keyword) {
        /* 如果只对数据进行读取，建议使用此方法 */
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<StepBean> stepBeanArrayList = new ArrayList<>();
        /* 得到游标 */
        keyword = "%" + keyword + "%";
        Cursor cursor = db.rawQuery("select * from step where foreign_item_id = ? and foreign_title_id = ? and (step like ? or operate like ? or imports like ?) order by id asc",
                new String[]{foreign_item_id.toString(), foreign_title_id.toString(), keyword, keyword, keyword});
        while (cursor.moveToNext()) {
            StepBean stepBean = new StepBean();
            stepBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            stepBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            stepBean.setForeign_title_id(cursor.getString(cursor.getColumnIndex("foreign_title_id")));
            stepBean.setNum(cursor.getString(cursor.getColumnIndex("num")));
            stepBean.setStep(cursor.getString(cursor.getColumnIndex("step")));
            stepBean.setOperate(cursor.getString(cursor.getColumnIndex("operate")));
            stepBean.setImports(cursor.getString(cursor.getColumnIndex("imports")));
            stepBeanArrayList.add(stepBean);
        }
        cursor.close();
        //if (DEBUG) Log.d(msg, "item : The getSteps() event: " + stepBeanArrayList);
        return stepBeanArrayList;
    }

    /* 查询record */
    public RecordBean getRecord(String work_id) {
        SQLiteDatabase db = getReadableDatabase();
        RecordBean recordBean = new RecordBean();
        Cursor cursor = db.rawQuery("select * from record where work_id = ?",
                new String[]{work_id});
        if (cursor.moveToFirst()) {
            recordBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            recordBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            recordBean.setDevice_number(cursor.getString(cursor.getColumnIndex("device_number")));
            recordBean.setOrder_number(cursor.getString(cursor.getColumnIndex("order_number")));
            recordBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            recordBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            recordBean.setIs_worked(cursor.getString(cursor.getColumnIndex("is_worked")));
            recordBean.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            recordBean.setState(cursor.getString(cursor.getColumnIndex("state")));
            cursor.close();
            if (DEBUG) Log.d(msg, "The getRecord() event: " + recordBean);
            return recordBean;
        }
        return recordBean;
    }


    /* 查询未完成record */
    public RecordBean getWorkingRecord(String work_id, String is_worked) {
        SQLiteDatabase db = getReadableDatabase();
        RecordBean recordBean = new RecordBean();
        Cursor cursor = db.rawQuery("select * from record where work_id = ? and is_worked = ?",
                new String[]{work_id, is_worked});
        if (cursor.moveToFirst()) {
            recordBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            recordBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            recordBean.setDevice_number(cursor.getString(cursor.getColumnIndex("device_number")));
            recordBean.setOrder_number(cursor.getString(cursor.getColumnIndex("order_number")));
            recordBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            recordBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            recordBean.setIs_worked(cursor.getString(cursor.getColumnIndex("is_worked")));
            recordBean.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            cursor.close();
            //if (DEBUG) Log.d(msg, "The getWorkingRecord() event: " + recordBean);
            return recordBean;
        }
        return recordBean;
    }

    /* 查询未完成的record */
    public RecordBean getWorkingRecord(Integer foreign_item_id, String is_worked, String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        RecordBean recordBean = new RecordBean();
        Cursor cursor = db.rawQuery("select * from record where foreign_item_id = ? and is_worked = ? and user_id = ?",
                new String[]{foreign_item_id.toString(), is_worked, user_id});
        if (cursor.moveToFirst()) {
            recordBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            recordBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            recordBean.setDevice_number(cursor.getString(cursor.getColumnIndex("device_number")));
            recordBean.setOrder_number(cursor.getString(cursor.getColumnIndex("order_number")));
            recordBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            recordBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            recordBean.setIs_worked(cursor.getString(cursor.getColumnIndex("is_worked")));
            recordBean.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            cursor.close();
            //if (DEBUG) Log.d(msg, "The getWorkingRecord() event: " + recordBean);
            return recordBean;
        }
        return recordBean;
    }

    /* 查询未完成records */
    public ArrayList<RecordBean> getWorkingRecords(Integer foreign_item_id, String is_worked) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecordBean> recordBeanArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record where foreign_item_id = ? and is_worked = ? order by timestamp desc",
                new String[]{foreign_item_id.toString(), is_worked});
        while (cursor.moveToNext()) {
            RecordBean recordBean = new RecordBean();
            recordBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            recordBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            recordBean.setDevice_number(cursor.getString(cursor.getColumnIndex("device_number")));
            recordBean.setOrder_number(cursor.getString(cursor.getColumnIndex("order_number")));
            recordBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            recordBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            recordBean.setIs_worked(cursor.getString(cursor.getColumnIndex("is_worked")));
            recordBean.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            recordBeanArrayList.add(recordBean);
        }
        cursor.close();
        if (DEBUG) Log.d(msg, "The getWorkingRecords() event: " + recordBeanArrayList);
        return recordBeanArrayList;
    }

    /* 查询已完成records */
    public ArrayList<RecordBean> getWorkedRecords(String is_worked) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecordBean> recordBeanArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record where is_worked = ? order by timestamp desc",
                new String[]{is_worked});
        while (cursor.moveToNext()) {
            RecordBean recordBean = new RecordBean();
            recordBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            recordBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            recordBean.setDevice_number(cursor.getString(cursor.getColumnIndex("device_number")));
            recordBean.setOrder_number(cursor.getString(cursor.getColumnIndex("order_number")));
            recordBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            recordBean.setIs_worked(cursor.getString(cursor.getColumnIndex("is_worked")));
            recordBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            recordBean.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            recordBeanArrayList.add(recordBean);
        }
        cursor.close();
        if (DEBUG) Log.d(msg, "The getWorkingRecords() event: " + recordBeanArrayList);
        return recordBeanArrayList;
    }

    /* 查询所有records */
    public ArrayList<RecordBean> getAllWorkingRecords() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RecordBean> recordBeanArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from record where is_worked = ? order by timestamp desc", null);
        while (cursor.moveToNext()) {
            RecordBean recordBean = new RecordBean();
            recordBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            recordBean.setForeign_item_id(cursor.getString(cursor.getColumnIndex("foreign_item_id")));
            recordBean.setDevice_number(cursor.getString(cursor.getColumnIndex("device_number")));
            recordBean.setOrder_number(cursor.getString(cursor.getColumnIndex("order_number")));
            recordBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            recordBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            recordBean.setIs_worked(cursor.getString(cursor.getColumnIndex("is_worked")));
            recordBean.setTimestamp(cursor.getString(cursor.getColumnIndex("timestamp")));
            recordBeanArrayList.add(recordBean);
        }
        cursor.close();
        if (DEBUG) Log.d(msg, "The getWorkingRecords() event: " + recordBeanArrayList);
        return recordBeanArrayList;
    }

    public int getimageCout(int item_num, int order) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from step where foreign_item_id = ? and id <= ? and step != '' and step != 'null'", new String[]{String.valueOf(item_num), String.valueOf(order)});
        cursor.moveToFirst();
        //if (DEBUG) Log.d(msg, "The getLogCout() event, count: " + cursor.getLong(0));
        return Integer.parseInt(String.valueOf(cursor.getLong(0)));
    }

    /* 查询logcount */
    public int getLogCout(String foreign_title_id, String foreign_step_id, String work_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from log where foreign_title_id = ? and foreign_step_id = ? and work_id = ?", new String[]{foreign_title_id, foreign_step_id, work_id});
        cursor.moveToFirst();
        //if (DEBUG) Log.d(msg, "The getLogCout() event, count: " + cursor.getLong(0));
        return Integer.parseInt(String.valueOf(cursor.getLong(0)));
    }

    /* 查询logcounts */
    public int getLogCouts(String work_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from log where work_id = ? and url != ''", new String[]{work_id});
        cursor.moveToFirst();
        //if (DEBUG) Log.d(msg, "The getLogCout() event, count: " + cursor.getLong(0));
        return Integer.parseInt(String.valueOf(cursor.getLong(0)));
    }

    /* 查询logbean */
    public LogBean getLog(String foreign_title_id, String foreign_step_id, String work_id) {
        SQLiteDatabase db = getReadableDatabase();
        LogBean logBean = new LogBean();
        Cursor cursor = db.rawQuery("select * from log where foreign_title_id = ? and foreign_step_id = ? and work_id = ? ",
                new String[]{foreign_title_id, foreign_step_id, work_id});
        if (cursor.moveToFirst()) {
            logBean.setBad_url(cursor.getString(cursor.getColumnIndex("bad_url")));
            logBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            cursor.close();
            if (DEBUG) Log.d(msg, "The getLog() event: " + logBean);
            return logBean;
        }
        return logBean;
    }

    /* 查询logs */
    public ArrayList<LogBean> getLogs(String work_id) {
        if (DEBUG) Log.d("遍历log",work_id);
        /* 如果只对数据进行读取，建议使用此方法 */
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<LogBean> logBeanArrayList = new ArrayList<>();
        /* 得到游标 */
        Cursor cursor = db.rawQuery("select * from log where work_id = ?", new String[]{work_id});
        while (cursor.moveToNext()) {
            LogBean logBean = new LogBean();
            logBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            logBean.setForeign_title_id(cursor.getString(cursor.getColumnIndex("foreign_title_id")));
            logBean.setForeign_step_id(cursor.getString(cursor.getColumnIndex("foreign_step_id")));
            logBean.setWork_id(cursor.getString(cursor.getColumnIndex("work_id")));
            logBean.setBad_url(cursor.getString(cursor.getColumnIndex("bad_url")));
            logBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            logBean.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
            logBeanArrayList.add(logBean);
        }
        cursor.close();
        if (DEBUG) Log.d(msg, "item : The getLogs() event: " + logBeanArrayList.size());
        return logBeanArrayList;
    }


    /* 新增log */
    public void insertLog(String foreign_title_id, String foreign_step_id, String work_id, String bad_url, String url, String user_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into log (foreign_title_id, foreign_step_id, work_id, bad_url, url,user_id) values(?, ?, ?, ?, ?,?)",
                new Object[]{foreign_title_id, foreign_step_id, work_id, bad_url, url, user_id});
        if (DEBUG) Log.d(msg, "The log save() event");
    }

    /* 更新操作日志 */
    public void updateurl(String foreign_title_id, String foreign_step_id, String work_id, String url) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update log set url = ? where foreign_title_id = ? and foreign_step_id = ? and work_id = ?",
                new Object[]{url, foreign_title_id, foreign_step_id, work_id});
        if (DEBUG) Log.d(msg, "The log updatelog() event");
    }

    public void updatebadurl(String foreign_title_id, String foreign_step_id, String work_id, String bad_url) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update log set bad_url = ? where foreign_title_id = ? and foreign_step_id = ? and work_id = ?",
                new Object[]{bad_url, foreign_title_id, foreign_step_id, work_id});
        if (DEBUG) Log.d(msg, "The log updatelog() event");
    }

    /* 新增record */
    public void insertRecord(String foreign_item_id, String device_number, String order_number, String work_id, String is_worked, String timestamp, String user_id, String state) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into record (foreign_item_id, device_number, order_number, work_id, is_worked, timestamp, user_id,state) values(?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{foreign_item_id, device_number, order_number, work_id, is_worked, timestamp, user_id, state});
        if (DEBUG) Log.d(msg, "The record save() event");
    }

    /* 更新record */
    public void updateRecordState(String is_worked, String work_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update record set is_worked = ? where work_id = ?",
                new Object[]{is_worked, work_id});

        if (DEBUG) Log.d(msg, "The record updateRecord() event");
    }

    /* 更新record机号 */
    public void updateRecordDevice(String device_number, String work_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update record set device_number = ? where work_id = ?",
                new Object[]{device_number, work_id});
    }

    /* 更新record令号 */
    public void updateRecordOrder(String order_number, String work_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update record set order_number = ? where work_id = ?",
                new Object[]{order_number, work_id});
    }

    /* 更新step_order */
    public void updateStepOrder(Integer order, Integer item_num, String user_id) {
        /* order item_num user_id */
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update step_tab set step_order = ? where id = ? and user_id=?", new String[]{order.toString(), item_num.toString(), user_id});
        if (DEBUG) Log.d(msg, "The log updateStepOrder() event");
}

    /* 获取step_order */
    public int getStepOrder(Integer item_num, String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        int step_order = 0;
        Cursor cursor = db.rawQuery("select * from step_tab where id = ? and user_id=?", new String[]{item_num.toString(), user_id});
        if (cursor.moveToFirst()) {
            step_order = cursor.getInt(cursor.getColumnIndex("step_order"));
            cursor.close();
            if (DEBUG) Log.d(msg, "The getLog() event: " + step_order);
            return step_order;
        }
        return step_order;
    }

    /* 关闭时销毁 */
    public void onDestory() {
        SQLiteDatabase db = getReadableDatabase();
        db.close();
    }

    public int getItemCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from item", null);
        cursor.moveToFirst();
        if (DEBUG) Log.d(msg, "The getCount() event, count: " + cursor.getLong(0));
        return Integer.parseInt(String.valueOf(cursor.getLong(0)));
    }

    /* 更新操作日志数据表 */
    public void updateLogTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS log");
        onCreate(db);
    }

    /* 更新操作流程数据表 */
    public void updateOperationTable(String init_json_string) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS step");
        INIT_JSON_STRING = init_json_string;
        onCreate(db);
    }

    /* 新增操作日志 */
    public void insertLog(String job_id, String register_id, String work_place, int item_id, String work_result, String images_url, String videos_url, String work_type, String operation, String work_id) {
        /* 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库 */
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into wis_ar_work_log (job_id, register_id, work_place, item_id, work_result, images_url, videos_url, work_type, operation, work_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{job_id, register_id, work_place, item_id, work_result, images_url, videos_url, work_type, operation, work_id});
        if (DEBUG) Log.d(msg, "The save() event");
    }

    /* 删除 */
    public void deletelog(Integer id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from log where id = ?", new Object[]{id.toString()});
        if (DEBUG) Log.d(msg, "The delete() event");
    }

    public int getLogCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from wis_ar_work_log", null);
        cursor.moveToFirst();
        if (DEBUG) Log.d(msg, "The getCount() event, count: " + cursor.getLong(0));
        return Integer.parseInt(String.valueOf(cursor.getLong(0)));
    }

    public int getOperationCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from wis_ar_workorder_standards", null);
        cursor.moveToFirst();
        if (DEBUG) Log.d(msg, "The getCount() event, count: " + cursor.getLong(0));
        return Integer.parseInt(String.valueOf(cursor.getLong(0)));
    }

    /* 需要传入db参数，在onCreate方法内两次调用同一db，会报错 */
    public int getDbOperationCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from wis_ar_workorder_standards", null);
        cursor.moveToFirst();
        if (DEBUG) Log.d(msg, "The getCount() event, count: " + cursor.getLong(0));
        return Integer.parseInt(String.valueOf(cursor.getLong(0)));
    }

    public ArrayList<String> getLogUrls() {
        ArrayList<String> arrayList = new ArrayList<String>();
        String LogImgUrls = "";
        String LogVideosUrls = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select images_url,videos_url from wis_ar_work_log", null);
        while (cursor.moveToNext()) {
            String images_url = cursor.getString(cursor.getColumnIndex("images_url"));
            String videos_url = cursor.getString(cursor.getColumnIndex("videos_url"));
            if (images_url != null && images_url.length() > 0)
                LogImgUrls = getUrls(LogImgUrls, images_url);
            if (videos_url != null && videos_url.length() > 0)
                LogVideosUrls = getUrls(LogVideosUrls, videos_url);
        }
        arrayList.add(0, LogImgUrls);
        arrayList.add(1, LogVideosUrls);
        if (DEBUG) Log.d("目录名称数组字符串：", arrayList.toString());
        return arrayList;
    }

    /* 获取所有的图片/录像文件名 */
    public String getUrls(String urls, String url) {
        if (urls.equals("")) {
            urls = url.substring(1, url.length());
        } else {
            urls += url;
        }
        return urls;
    }

    public UserBean getUser(String user_id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from users where user_id= ? ", new String[]{user_id});
        UserBean userBean = new UserBean();
        if (cursor.moveToNext()) {
            userBean.setUser_id(cursor.getColumnName(cursor.getColumnIndex("user_id")));
            userBean.setPassword(cursor.getColumnName(cursor.getColumnIndex("password")));
            userBean.setRole(cursor.getColumnName(cursor.getColumnIndex("role")));
            userBean.setRealname(cursor.getColumnName(cursor.getColumnIndex("realname")));
            userBean.setDepartment(cursor.getColumnName(cursor.getColumnIndex("department")));
            return userBean;
        } else {
            return null;
        }
    }

}
