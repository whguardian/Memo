package com.whguardian.android.memo.ListModule;

import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whguardian.android.memo.R;

/**
 * Created by whguardian_control on 16/04/04.
 */
public class ContactsListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Contacts");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent,
                             Bundle saveInstanceState) {
        View v = layoutInflater.inflate(R.layout.fragment_list, parent, false);

        return v;
    }
}
