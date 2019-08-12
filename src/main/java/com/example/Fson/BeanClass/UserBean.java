package com.example.Fson.BeanClass;

/**
 * Created by lmq on 2019/7/24.
 */

public class UserBean {

    public final static String USER_ID= "user_id";
    public final static String PASSWORD = "password";
    public final static String ROLE = "role";
    public final static String REALNAME = "realname";
    public final static String DEPARTMENT = "department";
    private String user_id;
    private String password;
    private String role;
    private String realname;
    private String department;

    public UserBean() {
        super();
    }

    public UserBean(String user_id, String password, String role, String realname, String department) {
        this.user_id = user_id;
        this.password = password;
        this.role = role;
        this.realname = realname;
        this.department = department;
    }

    public static String getUserId() {
        return USER_ID;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static String getROLE() {
        return ROLE;
    }

    public static String getREALNAME() {
        return REALNAME;
    }

    public static String getDEPARTMENT() {
        return DEPARTMENT;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
