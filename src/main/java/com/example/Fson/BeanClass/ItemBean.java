package com.example.Fson.BeanClass;

/**
 * Created by PC on 2017/11/10.
 */

public class ItemBean {
    public final static String ID = "id";
    public final static String NUM = "num";
    public final static String TITLE = "title";
    private int id;
    private String num;
    private String title;

    public ItemBean(){
        super();
    }

    public ItemBean(int id, String num, String title) {
        this.id = id;
        this.num = num;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getNum() {
        return num;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
