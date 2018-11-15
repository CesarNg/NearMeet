package com.hfad.nearmeet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final ArrayList<String> idPeopleNear = intent.getStringArrayListExtra("PeopleNear");
        String[] peoplesID = new String[idPeopleNear.size()];
        final ArrayList<String> peoplesName = new ArrayList<>(idPeopleNear.size());

        for (int i=0; i<idPeopleNear.size(); i++)
        {
            peoplesID[i] = idPeopleNear.get(i);
            db.collection("users")
                    .whereEqualTo("uid", idPeopleNear.get(i))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("YOYOYO", document.getString("username"));
                                    peoplesName.add(document.getString("username"));
                                }
                            }
                        }
                    });
        }

        setContentView(R.layout.activity_listpeople);
        final ListView listview = findViewById(R.id.listview);

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_item,
                R.id.secondLine, idPeopleNear);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String guy = (String) parent.getItemAtPosition(position);
                Log.e("YOYOYO", peoplesName.get(position));


            }
        });
    }


    @Override
    public void onStart()
    {
        super.onStart();

    }
}
