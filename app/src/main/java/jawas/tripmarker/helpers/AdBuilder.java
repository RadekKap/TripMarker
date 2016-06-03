package jawas.tripmarker.helpers;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;

import java.util.Date;

public class AdBuilder {

    public static AdRequest.Builder addKeywords( AdRequest.Builder builder, String[] keywords){
        for( String keyword : keywords ){
            builder.addKeyword( keyword );
        }
        return builder;
    }

    public static AdRequest.Builder addTargetProperties(AdRequest.Builder builder, Location location , Date birthday, String gender){
        if (location != null) {
            builder.setLocation(location);
        }

        if ( birthday != null ) {
            builder.setBirthday(birthday);
        }

        if( gender != null ){
            if (gender.equals("male")) {
                builder.setGender(AdRequest.GENDER_MALE);
            }
            else if (gender.equals("female")) {
                builder.setGender(AdRequest.GENDER_FEMALE);
            }
        }
        return builder;
    }
}
