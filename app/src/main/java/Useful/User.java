package Useful;

import android.net.Uri;

public class User {
    private static String UID;
    private static String name;
    private static String regId;
    private static String profilePic;

    public static void setName(String n) {
        name = n;
    }
    public static void setRegId(String r) {
        regId = r;
    }
    public static void setUID(String uid) {
        UID = uid;
    }
    public static void setProfilePic(String img) { profilePic = img; }
    public static String getName() {return name;}
    public static String getRegId() {return regId;}
    public static String getUID() {return UID;}
    public static String getProfilePic() {return profilePic;}
}
