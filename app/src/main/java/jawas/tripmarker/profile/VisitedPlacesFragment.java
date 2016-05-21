package jawas.tripmarker.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.lang.Object;
import jawas.tripmarker.R;
import jawas.tripmarker.helpers.FirebaseRef;
import jawas.tripmarker.helpers.UserId;

public class VisitedPlacesFragment extends Fragment {

    private static String UID;
    private ListView list ;
    private MyListAdapter adapter ;
    public ArrayList<String> places;
    public int pom=0;
    FloatingActionButton btnAdd;


    public static VisitedPlacesFragment newInstance() {
        VisitedPlacesFragment fragment = new VisitedPlacesFragment();
        return fragment;
    }

    public VisitedPlacesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());

        UID = UserId.getUID();
       // database.child("Vp").setValue(places1);


    }

    public void getlist()
    {
        FirebaseRef.getDbContext().child("users").child(UID).child("VP").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot != null) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                    };
                    if(snapshot.getValue(t)!=null)
                    { places = snapshot.getValue(t);

                  /*  adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, places);
                    list.setAdapter(adapter);*/

                        list.setAdapter(new MyListAdapter(getActivity(), R.layout.list_item, places));

                        int i=list.getChildCount();
                        Log.i("ERROR", "DataSnapshot error " +i);
                    }

                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("ERROR", "DataSnapshot error");
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.fragment_visited_places, container, false);

        list = (ListView)view.findViewById(R.id.listView);
        btnAdd = (FloatingActionButton) view.findViewById(R.id.imageButton);
        final EditText placetextField = (EditText) view.findViewById(R.id.placeTextField);

        getlist();
        if (places==null) {
            places = new ArrayList<String>();
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (placetextField.getText().toString().equals("") == false) {


                    places.add(placetextField.getText().toString());
                 /*   adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, places);
                    list.setAdapter(adapter);

                    */
                  //  list.deferNotifyDataSetChanged();
                  //  list.forceLayout();
                    list.setAdapter(new MyListAdapter(getActivity(),R.layout.list_item,places) );


                    FirebaseRef.getDbContext().child("users").child(UID).child("VP").setValue(places);
                    Toast.makeText(getActivity().getApplicationContext(), "You have added " + placetextField.getText().toString() + " to your places", Toast.LENGTH_LONG).show();
                    placetextField.setText("");


                    // adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You have to type place", Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }

    private class MyListAdapter extends ArrayAdapter<String>{
        private int layout;
        private final Context context;
        private final List<String> values;
        public MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout=resource;
            this.context=context;
            values=objects;
        }

        @Override
        public View getView(final int position,View convertView, ViewGroup parent){
          Viewholder mainviewhold=null;
           if(convertView==null){
               LayoutInflater inflater=LayoutInflater.from(getContext());
               convertView=inflater.inflate(layout,parent,false);
               Viewholder viewhold=new Viewholder();
               viewhold.imagebutt=(FloatingActionButton)convertView.findViewById(R.id.imageButtonlistitem);
               viewhold.title=(TextView)convertView.findViewById(R.id.textViewtolist);
               viewhold.title.setText(values.get(position));
               viewhold.imagebutt.setOnClickListener(new View.OnClickListener() {

                   @Override
                   public void onClick(View v) {
                      // Toast.makeText(getActivity().getApplicationContext(), "You have to type place viewhold " + position, Toast.LENGTH_LONG).show();
                      String temp=values.get(position);
                       places.remove(values.get(position));

                       list.setAdapter(new MyListAdapter(getActivity(), R.layout.list_item,places) );

                       FirebaseRef.getDbContext().child("users").child(UID).child("VP").setValue(places);
                       Toast.makeText(getActivity().getApplicationContext(), "You have removed " + temp + " from your places", Toast.LENGTH_LONG).show();
                   }
               });
                convertView.setTag(viewhold);
           }else
           {
               mainviewhold=(Viewholder)convertView.getTag();
               mainviewhold.title.setText(values.get(position));
               mainviewhold.imagebutt.setOnClickListener(new View.OnClickListener() {

                   @Override
                   public void onClick(View v) {
                     //  Toast.makeText(getActivity().getApplicationContext(), "You have to type place mainviewhold " + position, Toast.LENGTH_LONG).show();
                       String temp=values.get(position);
                       places.remove(values.get(position));

                       list.setAdapter(new MyListAdapter(getActivity(),R.layout.list_item,places) );

                       FirebaseRef.getDbContext().child("users").child(UID).child("VP").setValue(places);
                       Toast.makeText(getActivity().getApplicationContext(), "You have removed " + temp + " from your places", Toast.LENGTH_LONG).show();
                   }
               });

           }



            return convertView;
        }
    }

    public class Viewholder{
        FloatingActionButton imagebutt;
        TextView title;
    }
}
