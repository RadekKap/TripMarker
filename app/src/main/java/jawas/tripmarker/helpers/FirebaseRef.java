package jawas.tripmarker.helpers;

import com.firebase.client.Firebase;

public class FirebaseRef {

    static private Firebase fireRef;

    static public Firebase getDbContext(){
        return fireRef;
    }

    static{
        fireRef = new Firebase("https://tripmarker.firebaseio.com/");
    }
}