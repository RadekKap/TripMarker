package jawas.tripmarker.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

        Firebase database = new Firebase("https://tripmarker.firebaseio.com/");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new CollectionPagerAdapter( getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter( adapter );

        AdView banner = (AdView) findViewById( R.id.profileBanner);

        AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0D5BB761E332D5A158D5C5AABBB949A2").build();
        banner.loadAd(request);


        database.child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ((TextView)ProfileActivity.this.findViewById(R.id.name)).append(" "+snapshot.child("name").getValue());
                ((TextView)ProfileActivity.this.findViewById(R.id.age)).append(" "+snapshot.child("age").getValue());
                ((TextView)ProfileActivity.this.findViewById(R.id.gender)).append(" "+snapshot.child("gender").getValue());
                ((TextView)ProfileActivity.this.findViewById(R.id.homeplace)).append(" "+snapshot.child("homeplace").getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("ERROR", "DataSnapshot error");
            }
        });
    }

    public void addUser(String key, User user, Firebase context){
        context.child("users").child(key).setValue(user);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_actions, menu);
        return super.onCreateOptionsMenu(menu);
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
