package com.example.Fson.BeanClass;

/**
 * Created by PC on 2017/11/10.
 */

public class StepBean {
    public final static String ID = "id";
    public final static String FOREIGN_ITEM_ID = "foreign_item_id";
    public final static String FOREIGN_TITLE_ID = "foreign_title_id";
    public final static String NUM = "num";
    public final static String STEP = "step";
    public final static String OPERATE = "operate";
    public final static String IMPORTS = "imports";
    private int id;
    private String foreign_item_id;
    private String foreign_title_id;
    private String num;
    private String step;
    private String operate;
    private String imports;

    public StepBean(){
        super();
    }

    public StepBean(int id, String foreign_item_id, String foreign_title_id, String num, String step, String operate, String imports) {
        this.id = id;
        this.foreign_item_id = foreign_item_id;
        this.foreign_title_id = foreign_title_id;
        this.num = num;
        this.step = step;
        this.operate = operate;
        this.imports = imports;
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

    public String getForeign_title_id() {
        return foreign_title_id;
    }

    public void setForeign_title_id(String foreign_title_id) {
        this.foreign_title_id = foreign_title_id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getImports() {
        return imports;
    }

    public void setImports(String imports) {
        this.imports = imports;
    }
}
