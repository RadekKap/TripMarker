package jawas.tripmarker.helpers;

public class UserId {

    public static String getUID() {
        return UID;
    }

    public static void setUID(String UID) {
        UserId.UID = UID;
    }

    private static String UID;
}
