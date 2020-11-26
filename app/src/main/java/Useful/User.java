package Useful;

public class User {
    private static String UID;
    private static String name;
    private static String regId;

    public static void setName(String n) {
        name = n;
    }
    public static void setRegId(String r) {
        regId = r;
    }
    public static void setUID(String uid) {
        UID = uid;
    }
    public static String getName() {return name;}
    public static String getRegId() {return regId;}
    public static String getUID() {return UID;}
}
