package jawas.tripmarker.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;

import jawas.tripmarker.R;
import jawas.tripmarker.profile.pojos.User;

public class ProfileActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;
    private static String UID;
    private ViewPager pager;
    private CollectionPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_profile);

        UID = getIntent().getStringExtra((String) getResources().getText(R.string.uid_var));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new CollectionPagerAdapter( getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter( adapter );

        AdView banner = (AdView) findViewById( R.id.profileBanner);
        //TODO: Zmien numer urzadzenia testowego na swoj
        AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A57130D4686E461E45488E4878E17114").build();
        banner.loadAd(request);





    }

    public void addUser(String key, User user, Firebase context){
        Map<String, Object> post1 = new HashMap<String, Object>();
        post1.put(key, " ");
        context.child("users").updateChildren(post1);
        context.child("users").child(key).setValue(user);
    }

    public class CollectionPagerAdapter extends FragmentPagerAdapter{

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = position==0 ? (ProfileFragment.newInstance()):(VisitedPlacesFragment.newInstance());
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
