package com.self_back.utils;
public class Constant {
    public static final String API_URL = "https://api.aiproxy.io/v1/chat/completions"; //我这里使用的是aiproxy的
    public static String API_KEY = "*****************************"; //换成自己的
    public static int SUCCESS = 200;
    public static int ERROR = 400;
    public static String pass_end = "dhgdf";
    public static String SECRET_KEY = "dbdgb";
    public static long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
//  本机部署版
    public static String AVATAR_PATH = "E:\\selfPan_Upload\\avatars\\";
    public static String FILE_PATH = "E:\\selfPan_Upload\\uploads\\";
    public static String SHARE_BASE_URL = "http://localhost:8081/share/";
    public static String IMG_END = ".png";



    public static int ISDIR = 2;
    public static int ISFOLODER = 1;
    public static int ISFILE = 0;

    public static int UNDEL = 2;
    public static int INREC = 1;
    public static int DEL = 0;

    public static final String ZERO_STR = "0";

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final Integer LENGTH_30 = 30;

    public static final Integer LENGTH_10 = 10;

    public static final Integer LENGTH_20 = 20;

    public static final Integer LENGTH_5 = 5;

    public static final Integer LENGTH_15 = 15;

    public static final Integer LENGTH_150 = 150;

    public static final Integer LENGTH_50 = 50;


}
