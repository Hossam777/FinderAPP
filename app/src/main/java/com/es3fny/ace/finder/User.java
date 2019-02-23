package com.es3fny.ace.finder;

import java.util.ArrayList;

public class User {
    public static User myuser = new User();
    private String userName ;
    private ArrayList<String> history;
    public static void setUserName(String userName){myuser.userName = userName;}
    public static void setHistory(ArrayList<String> history){myuser.history = history;}
    public static String getUser(){return myuser.userName;}
    public static ArrayList<String> gethistory(){return myuser.history;}
    public static void clearuser(){myuser = new User();}
}
