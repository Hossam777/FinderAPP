package com.es3fny.ace.finder;

public class User {
    public static User myuser;
    private String userName ;
    private String [] history;
    public static void setUserName(String userName){myuser.userName = userName;}
    public static void setHistory(String[] history){myuser.history = history;}
    public static String getStringString(){return myuser.userName;}
    public static String[] gethistory(){return myuser.history;}
}
