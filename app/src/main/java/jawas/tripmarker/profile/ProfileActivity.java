package jawas.tripmarker.profile;

import android.content.Intent;
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
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jawas.tripmarker.MapsActivity;
import jawas.tripmarker.R;
import jawas.tripmarker.helpers.FirebaseRef;
import jawas.tripmarker.helpers.UserId;
import jawas.tripmarker.pojos.User;

public class ProfileActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 2;
    private static String UID;
    private ViewPager pager;
    private CollectionPagerAdapter adapter;
   // public ArrayList<String> addedplaces;
    private ListView listlastadded ;
    private ArrayAdapter<String> adaptertolisview ;
    ArrayList<String> listofplaces;
    private int pom=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_profile);
        listofplaces=new ArrayList<String>();

        UID = UserId.getUID();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new CollectionPagerAdapter( getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);

        pager.setAdapter(adapter);
//TUTEJ
        Query queryRef = FirebaseRef.getDbContext().child("markers");

       queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();

                String usrid = String.valueOf(value.get("userId"));

                if(usrid.equals(UID)==true)
                {
                    if(listofplaces!=null)
                    listofplaces.add(String.valueOf(value.get("title")));
                }

                if(listofplaces.size()!=0 && pom==0)
                {   listlastadded=(ListView)ProfileActivity.this.findViewById(R.id.listViewlastadded);
                    adaptertolisview = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1,listofplaces);
                    listlastadded.setAdapter(adaptertolisview);
                    pom=1;}


            /*    if(listP.size()!=0)
                { adaptertolisview = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1,listP);
                listlastadded.setAdapter(adaptertolisview);}*/
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    /*    queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot snapshot) {



               for (DataSnapshot child: snapshot.getChildren()) {

                   String usrid=(String) child.child("userId").getValue();

                   if(usrid.equals(UID)==true) {
                       String place = (String) child.child("title").getValue();
                       listofplaces.add(place);
                   }
               }

               if(listofplaces.size()!=0)
               {   listlastadded=(ListView)ProfileActivity.this.findViewById(R.id.listViewlastadded);
                   adaptertolisview = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1,listofplaces);
                   listlastadded.setAdapter(adaptertolisview);}

           }
           @Override
           public void onCancelled(FirebaseError firebaseError) {
               Log.i("ERROR", "DataSnapshot error");
           }
       });
*/

        FirebaseRef.getDbContext().child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ((TextView) ProfileActivity.this.findViewById(R.id.name)).append(" "+snapshot.child("name").getValue());
                ((TextView)ProfileActivity.this.findViewById(R.id.age)).append(" "+snapshot.child("age").getValue());
                ((TextView)ProfileActivity.this.findViewById(R.id.gender)).append(" "+snapshot.child("gender").getValue());
                ((TextView)ProfileActivity.this.findViewById(R.id.homeplace)).append(" "+snapshot.child("homeplace").getValue());

                int gender = snapshot.child("gender").getValue() == "male" ? AdRequest.GENDER_MALE : AdRequest.GENDER_FEMALE;

                AdView banner = (AdView) findViewById( R.id.profileBanner);
                AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("0D5BB761E332D5A158D5C5AABBB949A2")
                        .setGender(gender).build();
                banner.loadAd(request);
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("ERROR", "DataSnapshot error");
            }
        });
    }

    public void addUser(String key, User user, Firebase context){
        Map<String, Object> post1 = new HashMap<String, Object>();
        post1.put(key, " ");
        context.child("users").updateChildren(post1);
        context.child("users").child(key).setValue(user);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.go_to_map:
                startActivity(new Intent(this, MapsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
