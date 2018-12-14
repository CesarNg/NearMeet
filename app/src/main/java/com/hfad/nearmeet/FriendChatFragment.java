package com.hfad.nearmeet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.hfad.nearmeet.Model.Message;
import com.hfad.nearmeet.Model.User;
import com.hfad.nearmeet.api.MessageHelper;
import com.hfad.nearmeet.api.UserHelper;
import com.hfad.nearmeet.friend_chat.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendChatFragment extends Fragment implements ChatAdapter.Listener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.fragment_friend_chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_friend_chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;
    @BindView(R.id.fragment_friend_chat_message_edit_text)
    TextInputEditText editTextMessage;
    @BindView(R.id.fragment_chat_icon)
    ImageView imageViewPreview;
    @BindView(R.id.name_userReceiver)
    TextView userReceiverName;


    private ChatAdapter chatAdapter;
    private Context context;


    // TODO: Rename and change types of parameters
    private String chatUID;
    private String uidReceiver;

    private OnFragmentInteractionListener mListener;

    // STATIC DATA FOR CHAT (3)
    private static final String CHAT_NAME_ANDROID = "android";


    public FriendChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendChatFragment newInstance(String param1, String param2) {
        FriendChatFragment fragment = new FriendChatFragment();
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
            chatUID = getArguments().getString(ARG_PARAM1);
            uidReceiver = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_chat, container, false);
        ButterKnife.bind(this,view);
        this.textViewRecyclerViewEmpty.setVisibility(View.GONE);
        this.configureRecyclerView(CHAT_NAME_ANDROID);


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
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

    @OnClick(R.id.fragment_friend_chat_send_button)
    public void onClickSendMessage() {

        // 1 - Check if text field is not empty and current user properly downloaded from Firestore
        if (!TextUtils.isEmpty(editTextMessage.getText())) {
            // 2 - Create a new Message to Firestore
            MessageHelper.createMessageForChat(editTextMessage.getText().toString(), getCurrentUser().getUid(),uidReceiver,chatUID).addOnFailureListener(this.onFailureListener());
            // 3 - Reset text field
            this.editTextMessage.setText("");



        }


    }


    @Override
    public void onDataChanged() {
        textViewRecyclerViewEmpty.setVisibility(chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
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

    public OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }


    private void configureRecyclerView(String chatName){
        //Track current chat name

        MessageHelper.getAllMessageForChat().addValueEventListener(new ValueEventListener() {

           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               List<Message> simpleViewModelList = new ArrayList<>();
               for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {

                   if(messageSnapshot.getKey().equals(chatUID)){

                       for (DataSnapshot messageChildSnapshot : messageSnapshot.getChildren()){

                           Message message = messageChildSnapshot.getValue(Message.class);
                           simpleViewModelList.add(message);
                       }
                   }

               }

               chatAdapter = new ChatAdapter(simpleViewModelList, getCurrentUser().getUid());
               recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
               recyclerView.setHasFixedSize(true);
               recyclerView.setAdapter(chatAdapter);
               recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        UserHelper.getUser().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userData : dataSnapshot.getChildren()){

                    if(uidReceiver.equals(userData.getKey())){

                        User user = userData.getValue(User.class);
                        userReceiverName.setText(user.getUsername());

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }
}
