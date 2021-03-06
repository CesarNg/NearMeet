package com.hfad.nearmeet;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hfad.nearmeet.Model.Chat;
import com.hfad.nearmeet.api.ChatHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.friendsList)
    RecyclerView friendsList;
    @BindView(R.id.noFriendsText)
    TextView noUsersText;

    private ListFriendsAdapter listFriendsAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;

    private OnFragmentInteractionListener mListener;

    // STATIC DATA FOR CHAT (3)
    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";

    public FriendsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsListFragment newInstance(String param1, String param2) {
        FriendsListFragment fragment = new FriendsListFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this,view);

        this.configureRecyclerView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


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
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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

    private void configureRecyclerView(){
        //Track current chat name

        List<Chat> simpleViewModelList = new ArrayList<>();



        ChatHelper.getCHat().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {

                    for (DataSnapshot chatChildSnapchot : chatSnapshot.getChildren()){

                        Chat chat = chatChildSnapchot.getValue(Chat.class);

                        if(chat.getUidMember1().equals(getCurrentUser().getUid())||chat.getUidMember1().equals(getCurrentUser().getUid()))
                        simpleViewModelList.add(chat);
                    }


                }

                listFriendsAdapter = new ListFriendsAdapter(simpleViewModelList, getCurrentUser().getUid());
                friendsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                friendsList.setHasFixedSize(true);
                friendsList.setAdapter(listFriendsAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }


}

