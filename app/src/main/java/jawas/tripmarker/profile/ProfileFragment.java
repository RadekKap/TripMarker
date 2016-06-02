package jawas.tripmarker.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Map;

import jawas.tripmarker.R;
import jawas.tripmarker.helpers.FirebaseRef;
import jawas.tripmarker.helpers.UserId;

public class ProfileFragment extends Fragment {


    private ListView listlastadded ;
    private ArrayAdapter<String> adaptertolisview ;
    ArrayList<String> listofplaces;
    private int pom=0;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        listofplaces = new ArrayList<>();
        Query queryRef = FirebaseRef.getDbContext().child("markers").orderByChild("userID").limitToLast(4);;


        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();

                String usrid = String.valueOf(value.get("userId"));

                if(usrid.equals(UserId.getUID()))
                {
                    if(listofplaces!=null)
                        listofplaces.add(String.valueOf(value.get("title")));
                }
                if(listofplaces!=null)
                    if(listofplaces.size()!=0 && pom==0)
                    {
                        listlastadded=(ListView)getActivity().findViewById(R.id.listViewlastadded);
                        adaptertolisview = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,listofplaces);
                        listlastadded.setAdapter(adaptertolisview);
                        pom=1;
                    }



            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        FirebaseRef.getDbContext().child("users").child(UserId.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ((TextView) getActivity().findViewById(R.id.name)).append(" " + snapshot.child("name").getValue());
                ((TextView) getActivity().findViewById(R.id.age)).append(" "+snapshot.child("age").getValue());
                ((TextView) getActivity().findViewById(R.id.gender)).append(" "+snapshot.child("gender").getValue());
                ((TextView) getActivity().findViewById(R.id.homeplace)).append(" "+snapshot.child("homeplace").getValue());

                int gender = snapshot.child("gender").getValue() == "male" ? AdRequest.GENDER_MALE : AdRequest.GENDER_FEMALE;

                AdView banner = (AdView) getActivity().findViewById(R.id.profileBanner);
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
        return view;
    }
}