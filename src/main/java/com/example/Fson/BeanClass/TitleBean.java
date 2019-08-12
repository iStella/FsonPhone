package com.example.Fson.BeanClass;

/**
 * Created by PC on 2018/4/18.
 */

public class TitleBean {
    public final static String ID = "id";
    public final static String FOREIGN_ITEM_ID = "foreign_item_id";
    public final static String NUM = "num";
    public final static String TITLE = "title";
    private int id;
    private String foreign_item_id;
    private String ishead;
    private String num;
    private String title;

    public TitleBean(){
        super();
    }

    public TitleBean(int id, String foreign_item_id, String ishead, String num, String title) {
        this.id = id;
        this.foreign_item_id = foreign_item_id;
        this.ishead = ishead;
        this.num = num;
        this.title = title;
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

    public String getIshead() {
        return ishead;
    }

    public void setIshead(String ishead) {
        this.ishead = ishead;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
