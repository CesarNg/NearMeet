package com.hfad.nearmeet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                    peoplesName.add(document.getString("username"));
                                }
                            }
                        }
                    });
        }

        setContentView(R.layout.fragment_people);
        final ListView listview = findViewById(R.id.listview);

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_item,
                R.id.firstLine, peoplesName);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String guy = (String) parent.getItemAtPosition(position);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListPeopleActivity.this);

                final TextView et = new TextView(ListPeopleActivity.this);
                et.setText("\n\n         Voulez-vous jouer avec cette personne ?\n");
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Intent I = new Intent(this,MainActivity.class);
                        //I.putExtra("opponent",guy);
                        //startActivity(I);
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
