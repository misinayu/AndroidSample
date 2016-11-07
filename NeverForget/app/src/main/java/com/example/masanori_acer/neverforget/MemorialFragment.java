package com.example.masanori_acer.neverforget;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemorialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemorialFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MemorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemorialFragment newInstance(String param1, String param2) {
        MemorialFragment fragment = new MemorialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = this.getActivity().getSharedPreferences("memorial", Context.MODE_PRIVATE);
        int wedding = pref.getInt("wedding", 0);
        int birthday = pref.getInt("birthday", 0);
        int birthday1 = pref.getInt("birthday1", 0);
        int birthday2 = pref.getInt("birthday2", 0);
        int birthday3 = pref.getInt("birthday3", 0);
        EditText etextWedding = (EditText)getView().findViewById(R.id.etextWedding);
        if (wedding != 0){
            etextWedding.setText(Integer.toString(wedding));
        }
        EditText etextBirthday = (EditText)getView().findViewById(R.id.etextBirthday);
        if (birthday != 0){
            etextBirthday.setText(Integer.toString(birthday));
        }
        EditText etextBirthday1 = (EditText)getView().findViewById(R.id.etextBirthday1);
        if (birthday1 != 0){
            etextBirthday1.setText(Integer.toString(birthday1));
        }
        EditText etextBirthday2 = (EditText)getView().findViewById(R.id.etextBirthday2);
        if (birthday2 != 0){
            etextBirthday2.setText(Integer.toString(birthday2));
        }
        EditText etextBirthday3 = (EditText)getView().findViewById(R.id.etextBirthday3);
        if (birthday3 != 0){
            etextBirthday3.setText(Integer.toString(birthday3));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        EditText etextWedding = (EditText)getView().findViewById(R.id.etextWedding);
        EditText etextBirthday = (EditText)getView().findViewById(R.id.etextBirthday);
        EditText etextBirthday1 = (EditText)getView().findViewById(R.id.etextBirthday1);
        EditText etextBirthday2 = (EditText)getView().findViewById(R.id.etextBirthday2);
        EditText etextBirthday3 = (EditText)getView().findViewById(R.id.etextBirthday3);

        int wedding;
        try {
            wedding = Integer.parseInt(etextWedding.getText().toString());
        }catch (NumberFormatException e){
            wedding = 0;
        }

        int birthday;
        try {
            birthday = Integer.parseInt(etextBirthday.getText().toString());
        }catch (NumberFormatException e){
            birthday = 0;
        }

        int birthday1;
        try {
            birthday1 = Integer.parseInt(etextBirthday1.getText().toString());
        }catch (NumberFormatException e){
            birthday1 = 0;
        }

        int birthday2;
        try {
            birthday2 = Integer.parseInt(etextBirthday2.getText().toString());
        }catch (NumberFormatException e){
            birthday2 = 0;
        }

        int birthday3;
        try {
            birthday3 = Integer.parseInt(etextBirthday3.getText().toString());
        }catch (NumberFormatException e){
            birthday3 = 0;
        }

        SharedPreferences pref = this.getActivity().getSharedPreferences("memorial", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("wedding", wedding);
        editor.putInt("birthday", birthday);
        editor.putInt("birthday1", birthday1);
        editor.putInt("birthday2", birthday2);
        editor.putInt("birthday3", birthday3);
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memorial, container, false);
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
