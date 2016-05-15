package jawas.tripmarker.helpers;

import com.firebase.client.Firebase;

public class FirebaseRef {

    private static final Firebase fireRef = new Firebase("https://tripmarker.firebaseio.com/");

    public static Firebase getDbContext(){
        return fireRef;
    }
}