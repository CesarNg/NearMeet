package com.hfad.nearmeet;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hfad.nearmeet.api.UserHelper;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment  implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    private List<Marker> markers;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("users");
    GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
    /**
     * Request code for location permission request.
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private boolean visible = false;

    private GoogleMap mMap;
    private Location current_location =  new Location("mainActivity");
    private ArrayList<String> idPeopleNear;
    GPS_Service gps;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mMapFragment.getMapAsync(this);

        markers = new ArrayList<>();
        idPeopleNear = new ArrayList<>();

        return view;
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

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
        gps = new GPS_Service(getActivity(),"15");
        getActivity().startService(new Intent(getActivity(),GPS_Service.class));

        if(gps.canGetLocation()){
            String ID = this.getCurrentUser().getUid();
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            current_location.setLatitude(latitude);
            current_location.setLongitude(longitude);

            GeoPoint localisation = new GeoPoint(latitude,longitude);
            geoFirestore.setLocation(ID, localisation);
            UserHelper.updateLocalisation(localisation, ID);

        }else{
            gps.showSettingsAlert();
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
    }


    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        if(gps.canGetLocation()){
            String ID = this.getCurrentUser().getUid();
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            current_location.setLatitude(latitude);
            current_location.setLongitude(longitude);
            GeoPoint localisation = new GeoPoint(latitude,longitude);
            UserHelper.updateLocalisation(localisation, ID);
        }else{
            gps.showSettingsAlert();
        }

        return false;
    }


    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    public void onStop()
    {
       // UserHelper.updateIsOnline(false, getCurrentUser().getUid());
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        //UserHelper.updateIsOnline(false, getCurrentUser().getUid());
        super.onDestroy();
    }

    @OnClick(R.id.visible_switch)
    public void switchClick()
    {
        visible=!visible;
        if (visible) {
            UserHelper.updateIsVisible(true,this.getCurrentUser().getUid());
            GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(current_location.getLatitude(), current_location.getLongitude()), 0.6);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String documentID, GeoPoint location) {
                    final String docID = documentID;
                    final GeoPoint locat = location;
                    db.collection("users")
                            .whereEqualTo("uid", documentID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            if (!docID.equals(getCurrentUser().getUid()) && document.get("isOnline").equals(true)) {
                                                Marker marker = mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(locat.getLatitude(), locat.getLongitude()))
                                                        .title(docID)
                                                );
                                                markers.add(marker);
                                                if (!idPeopleNear.contains(docID)) idPeopleNear.add(docID);
                                                System.out.println(String.format("Document %s, %s entered the search area at [%f,%f]", docID, getCurrentUser().getUid(), locat.getLatitude(), locat.getLongitude()));
                                            }
                                        }
                                    } else {
                                        Log.d("MainActivity", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                }

                @Override
                public void onKeyExited(String documentID) {
                    for (int i = 0; i<markers.size(); i++) {
                        if (markers.get(i).getTitle().equals(documentID))
                        {
                            markers.get(i).remove();
                        }
                    }
                    System.out.println(String.format("Document %s is no longer in the search area", documentID));
                }

                @Override
                public void onKeyMoved(String documentID, GeoPoint location) {
                    System.out.println(String.format("Document %s moved within the search area to [%f,%f]", documentID, location.getLatitude(), location.getLongitude()));
                }

                @Override
                public void onGeoQueryReady() {
                    System.out.println("All initial data has been loaded and events have been fired!");
                }

                @Override
                public void onGeoQueryError(Exception exception) {
                    System.err.println("There was an error with this query: " + exception.getLocalizedMessage());
                }
            });
        }
        else {
            mMap.clear();

            UserHelper.updateIsVisible(false,this.getCurrentUser().getUid());

        }
    }
    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @OnClick(R.id.list_people)
    public void showListPeople()
    {
        Intent intent = new Intent(this.getActivity(), ListPeopleActivity.class);
        intent.putStringArrayListExtra("PeopleNear",idPeopleNear);
        PendingIntent pendingIntent =
                TaskStackBuilder.create(this.getActivity())
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(intent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getActivity());
        builder.setContentIntent(pendingIntent);

        getActivity().startActivity(intent);
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }
}
