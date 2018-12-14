package com.hfad.nearmeet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hfad.nearmeet.Model.User;
import com.hfad.nearmeet.api.UserHelper;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListPeopleActivity extends AppCompatActivity  {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> idPeopleNear;
    private ArrayList<String> peoplesName;
    private ArrayList<ArrayList<String>> peopleInterests;
    private ArrayList<String> myinterests;
    private ArrayList<String> idPeopleFiltered;
    private ArrayList<String> namePeopleFiltered;
    private FirebaseUser user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final ArrayList<String> idPeopleNear = intent.getStringArrayListExtra("PeopleNear");
        final ArrayList<String> peoplesName = new ArrayList<>(idPeopleNear.size());
        myinterests = new ArrayList<>();
        peopleInterests = new ArrayList<>();
        idPeopleFiltered = new ArrayList<>();
        namePeopleFiltered = new ArrayList<>();



        /*FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("uid",FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for (int i = 0; i < 10; i++)
                                {
                                    if (document.get(String.valueOf(i)) != null) myinterests.add((String)document.get(String.valueOf(i)));
                                }
                            }
                        }
                    }
                });*/
        UserHelper.getUser().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    User currentUser = userSnapshot.getValue(User.class);
                    if (userSnapshot.getKey().equals(getCurrentUser().getUid())) {

                        myinterests = currentUser.getInterets();
                        MultiSpinner filtre = findViewById(R.id.multispinner);
                        filtre.setEntries(myinterests);
                    }
                    else
                    {
                        if (idPeopleNear.contains(userSnapshot.getKey()) && currentUser.getInterets() != null) peopleInterests.add(currentUser.getInterets());
                    }

                    //for (int i = 0; i < peopleInterests.size(); i++) Log.d("Testt size of element i", String.valueOf(peopleInterests.get(i)));
                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });




        for (int i=0; i<idPeopleNear.size(); i++)
        {
            db.collection("users")
                    .whereEqualTo("uid", idPeopleNear.get(i))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    peoplesName.add(document.getString("username"));
                                    if (document.get("interets") != null) peopleInterests.add((ArrayList<String>)document.get("interets"));
                                    //else peopleInterests.add(new ArrayList<String>());
                                }
                            }

                            final ArrayAdapter adapter = new ArrayAdapter(ListPeopleActivity.this, R.layout.simple_list_item,
                                    R.id.firstLine, peoplesName);

                            final ListView listview = findViewById(R.id.listview);
                            listview.setAdapter(adapter);

                            MultiSpinner filtre = findViewById(R.id.multispinner);
                            filtre.setMultiSpinnerListener(new MultiSpinner.MultiSpinnerListener() {
                                boolean filterEmpty = true;
                                @Override
                                public void onItemsSelected(boolean[] selected) {
                                    idPeopleFiltered = new ArrayList<>();
                                    for (int i=0; i<filtre.getEntries().length;i++)
                                    {
                                        if (selected[i]) {
                                            filterEmpty = false;
                                            for (int j = 0; j<peopleInterests.size(); j++)
                                            {
                                                if (peopleInterests.get(j).contains(filtre.getEntries()[i]) && !idPeopleFiltered.contains(idPeopleNear.get(j))) {
                                                    idPeopleFiltered.add(idPeopleNear.get(j));
                                                    namePeopleFiltered.add(peoplesName.get(j));

                                                }
                                            }
                                        }
                                    }
                                    ArrayAdapter adapterFiltered;
                                     if (!filterEmpty) adapterFiltered = new ArrayAdapter(ListPeopleActivity.this, R.layout.simple_list_item,
                                            R.id.firstLine, namePeopleFiltered);
                                    else adapterFiltered = new ArrayAdapter(ListPeopleActivity.this, R.layout.simple_list_item,
                                             R.id.firstLine, peoplesName);
                                    final ListView listview = findViewById(R.id.listview);
                                    listview.setAdapter(adapterFiltered);
                                    filterEmpty = true;
                                }
                            });
                        }
                    });
        }

        setContentView(R.layout.fragment_people);


        final ListView listview = findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String guy = (String) idPeopleNear.get(position);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListPeopleActivity.this);

                final TextView et = new TextView(ListPeopleActivity.this);
                et.setText("\n\n         Voulez-vous jouer avec cette personne ?\n");
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent I = new Intent(ListPeopleActivity.this,StartActivity.class);
                        I.putExtra("opponent",guy);
                        startActivity(I);
                    }
                });
                alertDialogBuilder.setCancelable(true).setNegativeButton("Return", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();


            }
        });

    }

    public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.simple_list_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values[position]);

            return rowView;
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();

    }
}
