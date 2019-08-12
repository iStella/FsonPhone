package com.example.Fson.BeanClass;

/**
 * Created by PC on 2017/11/10.
 */

public class RecordBean {
    public final static String ID = "id";
    public final static String FOREIGN_ITEM_ID = "foreign_item_id";
    public final static String DEVICE_NUMBER = "device_number";
    public final static String ORDER_NUMBER = "order_number";
    public final static String WORK_ID = "work_id";
    public final static String USER_ID = "user_id";
    public final static String TIMESTAMP = "timestamp";
    public final static String IS_WORKED = "is_worked";
    public final static String STATE = "state";
    private int id;
    private String foreign_item_id;
    private String device_number;
    private String order_number;
    private String work_id;
    private String user_id;
    private String is_worked;
    private String timestamp;
    private String state;

    public RecordBean() {
        super();
    }

    public RecordBean(int id, String foreign_item_id, String device_number, String order_number, String work_id, String user_id, String is_worked, String timestamp, String state) {
        this.id = id;
        this.foreign_item_id = foreign_item_id;
        this.device_number = device_number;
        this.order_number = order_number;
        this.work_id = work_id;
        this.user_id = user_id;
        this.is_worked = is_worked;
        this.timestamp = timestamp;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getForeign_item_id() {
        return foreign_item_id;
    }

    public void setForeign_item_id(String foreign_item_id) {
        this.foreign_item_id = foreign_item_id;
    }

    public String getDevice_number() {
        return device_number;
    }

    public void setDevice_number(String device_number) {
        this.device_number = device_number;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getWork_id() {
        return work_id;
    }

    public void setWork_id(String work_id) {
        this.work_id = work_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_worked() {
        return is_worked;
    }

    public void setIs_worked(String is_worked) {
        this.is_worked = is_worked;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
