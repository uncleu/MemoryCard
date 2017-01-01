package com.memorycard.android.memorycardapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import utilities.DataBaseManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainCustomSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainCustomSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainCustomSettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private DataBaseManager dataBaseManager;
    private EditText editText;
    private Context context;

    public MainCustomSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainCustomSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainCustomSettingsFragment newInstance(String param1, String param2) {
        MainCustomSettingsFragment fragment = new MainCustomSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        dataBaseManager = DataBaseManager.getDbManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_main_custom_settings, container, false);

        Button validateBtn = (Button)v.findViewById(R.id.validate_button);
        editText = (EditText)v.findViewById(R.id.new_tab_name);

        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardsGroup cg = new CardsGroup();
                String tab_name = editText.getText().toString();
                if(editText == null||"".equals(tab_name)){
                    Toast toast = Toast.makeText(context,
                            "cards group name can't be null",
                            Toast.LENGTH_SHORT);
                    toast.show();

                }
                else{
                    cg.setName(tab_name);
                    //update grouplist
                    dataBaseManager.createNewCardsGroupTab(cg);

                }
                Intent intent=new Intent(getActivity(), MainCustomSettingsActivity.class);
                startActivity(intent);
            }
        } );

        Button cancelBtn = (Button)v.findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MainCustomSettingsActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
