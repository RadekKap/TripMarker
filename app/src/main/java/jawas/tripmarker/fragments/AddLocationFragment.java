package jawas.tripmarker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jawas.tripmarker.R;

public class AddLocationFragment extends Fragment {

    public static AddLocationFragment newInstance() {
        AddLocationFragment fragment = new AddLocationFragment();
        return fragment;
    }

    public AddLocationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_location, container, false);
    }
}
