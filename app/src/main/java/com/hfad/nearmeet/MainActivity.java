package com.hfad.nearmeet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.imperiumlabs.geofirestore.*;

import java.util.ArrayList;
import java.util.List;

import com.hfad.nearmeet.api.UserHelper;


public class MainActivity extends BaseActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

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
    private List<Marker> markers;
    private ArrayList<String> idPeopleNear;

    private GoogleMap mMap;
    private Location current_location =  new Location("mainActivity");
    GPS_Service gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markers = new ArrayList<>();
        idPeopleNear = new ArrayList<>();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        //UserHelper.updateIsOnline(true, getCurrentUser().getUid());

    }

    @Override
    protected void onResume() {
        super.onResume();
        //UserHelper.updateIsOnline(true, getCurrentUser().getUid());
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
        gps = new GPS_Service(MainActivity.this,"5");
        startService(new Intent(MainActivity.this,GPS_Service.class));

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
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();

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
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onStop()
    {
        //UserHelper.updateIsOnline(false, getCurrentUser().getUid());
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        //UserHelper.updateIsOnline(false, getCurrentUser().getUid());
        super.onDestroy();

    }

    public void switchClick(android.view.View view)
    {
        visible=!visible;
        if (visible) {
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
                            markers.remove(i);
                            idPeopleNear.remove(i);
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
        }
    }


    public void showListPeople(android.view.View view)
    {
        //Intent intent = new Intent(this, ListPeopleFragment.class);
        //intent.putStringArrayListExtra("PeopleNear",idPeopleNear);
        //startActivity(intent);
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
