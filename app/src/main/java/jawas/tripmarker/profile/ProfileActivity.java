package jawas.tripmarker.profile;

import android.content.Context;
import android.provider.Settings.Secure;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import jawas.tripmarker.R;

public class ProfileActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;
    private ViewPager pager;
    private CollectionPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new CollectionPagerAdapter( getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter( adapter );

        AdView banner = (AdView) findViewById( R.id.profileBanner);
        //TODO: Zmien numer urzadzenia testowego na swoj
        AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0D5BB761E332D5A158D5C5AABBB949A2").build();
        banner.loadAd(request);
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
