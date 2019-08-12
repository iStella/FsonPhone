package com.example.Fson.BeanClass;

/**
 * Created by PC on 2017/11/10.
 */

public class LogBean {
    public final static String ID = "id";
    public final static String FOREIGN_TITLE_ID = "foreign_title_id";
    public final static String FOREIGN_STEP_ID = "foreign_step_id";
    public final static String WORK_ID = "work_id";
    public final static String BAD_URL = "bad_url";
    public final static String URL = "url";
    public final static String USER_ID="user_id";
    private int id;
    private String foreign_title_id;
    private String foreign_step_id;
    private String work_id;
    private String bad_url;
    private String url;
    private String user_id;

    public LogBean(){
        super();
    }

    public LogBean(int id, String foreign_title_id, String foreign_step_id, String work_id, String bad_url, String url, String user_id) {
        this.id = id;
        this.foreign_title_id = foreign_title_id;
        this.foreign_step_id = foreign_step_id;
        this.work_id = work_id;
        this.bad_url = bad_url;
        this.url = url;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getForeign_title_id() {
        return foreign_title_id;
    }

    public void setForeign_title_id(String foreign_title_id) {
        this.foreign_title_id = foreign_title_id;
    }

    public String getForeign_step_id() {
        return foreign_step_id;
    }

    public void setForeign_step_id(String foreign_step_id) {
        this.foreign_step_id = foreign_step_id;
    }

    public String getWork_id() {
        return work_id;
    }

    public void setWork_id(String work_id) {
        this.work_id = work_id;
    }

    public String getBad_url() {
        return bad_url;
    }

    public void setBad_url(String bad_url) {
        this.bad_url = bad_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
