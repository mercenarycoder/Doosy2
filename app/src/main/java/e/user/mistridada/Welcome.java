package e.user.mistridada;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.libraries.places.api.Places;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import e.user.mistridada.Common.Common;
import e.user.mistridada.Helper.CustomInfoWindow;
import e.user.mistridada.Model.Rider;
import e.user.mistridada.Remote.IFCMService;

public class Welcome extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    //Location
    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RES_REQUEST = 300193;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    private LatLng pickupLatLng;
    //private LatLng pickupLatLng2;
    private LatLng pickupLatLng3;

    //DatabaseReference ref;
    //GeoFire geoFire;
    PlacesClient placesClient;

    //Marker mUserMarker;
    //Marker mUserMarker2;
    Button viewAll;
    ImageView pickpin;
    private CardView place;

    //Request Button
    //Button btnPickupRequest;

    //boolean isDriverFound=false;
    //String driverId="";
    //int radius = 1; //1km
    int distance = 1; //3km
    private static final int LIMIT = 3;

    //Send Alert
    IFCMService mService;

    //Presence System
    //DatabaseReference driversAvailable;

    AutocompleteSupportFragment place_location;
    AutocompleteSupportFragment place_location2;
    //String mPlaceLocation;
    //String mPlaceLocation2;
    String dest_address="";
    LatLng latLng_destination;
    Context mContext;
    String pick_address="";
    private EditText adress_precision;
    private LocationTrack locationTrack;
    private ImageButton reposition;
    static int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count++;
        setContentView(R.layout.activity_welcome);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mContext=Welcome.this;
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        reposition=(ImageButton)findViewById(R.id.reposition);
        reposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(mContext, e.user.mistridada.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                        e.user.mistridada.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                       /* requestPermissions((String[]) permissionsToRequest.toArray(new
                                        String[permissionsToRequest
                                        .size()]),
                             ALL_PERMISSIONS_RESULT);
                */
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                        alertDialog.setTitle("GPS is not Enabled!");
                        alertDialog.setMessage("Do you want to turn on GPS?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                    else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                        alertDialog.setTitle("GPS is not Enabled!");
                        alertDialog.setMessage("Do you want to turn on GPS?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                }
                Welcome.super.recreate();
            }
        });
        Places.initialize(getApplicationContext(),getString(R.string.appkey));
        adress_precision=(EditText)findViewById(R.id.address_precision);

        placesClient = Places.createClient(this);

        final String[] pickupadd = {getIntent().getStringExtra("pickupaddress")};
        dest_address=getIntent().getStringExtra("dest_address");

        latLng_destination=new LatLng(0,0);
        latLng_destination=getIntent().getParcelableExtra("destinationLatlag");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //final String changecharge = getIntent().getStringExtra("changecharge");
        pickpin = findViewById(R.id.confirm_address_map_custom_marker);
        pickpin.setVisibility(View.INVISIBLE);

        pickupLatLng = new LatLng(0.0,0.0);
        //pickupLatLng2 = new LatLng(0.0,0.0);
        locationTrack = new LocationTrack(Welcome.this);
        double longi1=23.2599333,lati1=77.412615;

        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            if(latitude!=0.0&&longitude!=0.0) {
                longi1 = latitude;
                lati1 = longitude;
            }
         //   Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) +
           //         "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }

        pickupLatLng3 = new LatLng(longi1,lati1);


        mService = Common.getFCMService();

        viewAll = (Button) findViewById(R.id.btnviewall);

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, RequestForm.class);
                pick_address=adress_precision.getText().toString();
                pick_address +=" "+addressadd;
                if(pick_address.endsWith("India"))
                {
                    pick_address=pick_address.substring(0,pick_address.length()-37);
                }
                intent.putExtra("pick_parcel", pick_address);
                intent.putExtra("check","check");
               // intent.putExtra("dest_address",dest_address);
                //intent.putExtra("droplat1", pickupLatLng2.latitude);
                //intent.putExtra("droplon1", pickupLatLng2.longitude);
                //intent.putExtra("changecharge", changecharge);

                Bundle b = new Bundle();
                b.putParcelable("pickupaddval", pickupLatLng);
                intent.putExtras(b);
                setResult(1,intent);
                finish();
            }
        });


        place = (CardView) findViewById(R.id.dropview);
        place.setVisibility(View.GONE);
        place_location = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_location);
        place_location2 = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.place_location2);

        place_location.setPlaceFields(Collections.singletonList(Place.Field.LAT_LNG));

        //Event
        place_location.setCountry("IN");
        LatLng center = new LatLng(longi1,lati1);
        LatLng northside = SphericalUtil.computeOffset(center,100000,0);
        LatLng southside = SphericalUtil.computeOffset(center,100000,180);

        place_location.setLocationBias(RectangularBounds.newInstance(
                southside,northside
        ));



        place_location.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //mPlaceLocation = place.getAddress().toString();
                pickupLatLng = place.getLatLng();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15.5f));
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        //Event
        /**place_location2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //mPlaceLocation2 = place.getAddress().toString();
                pickupLatLng2 = place.getLatLng();
                //remove old marker
                if(mUserMarker2!=null){
                    mUserMarker2.remove();
                }

                //Add marker at new Location
                mUserMarker2 = mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker())
                        .title("Drop location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15.0f));
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });**/
        setUpLocation();

    }

    //Press Ctrl+O


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                super.recreate();
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();

                    }

                }
                break;
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
            //Request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE
            },MY_PERMISSION_REQUEST_CODE);
        }
        else
        {
            if(checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();

            }
        }
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    protected void onResume() {
     //   super.recreate();
        super.onResume();
    }

    private void loadAllAvailableDriver(final LatLng location) {

        //First, we need delete all available marker on map(include our location maker and available driver)
        mMap.clear();
        //After that, just add our loaction again
        mMap.addMarker(new MarkerOptions().position(location)
                        .title("You"));


        //Load All available labour in distance 3km
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
        GeoFire gf = new GeoFire(driverLocation);

        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(location.latitude,location.longitude),distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                //Use key to get email from table users
                //Table users is table when driver register account and update information
                //Just open your Driver to check this table name
                FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //Because Rider and User is same properties
                                    //So we can use Rider model to get User here
                                    Rider rider = dataSnapshot.getValue(Rider.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(distance <=LIMIT) //distance just find for 3km
                {
                    distance++;
                    loadAllAvailableDriver(location);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode!= ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;

        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng3,15.5f));

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                pickupLatLng=mMap.getCameraPosition().target;
                getaddress();
            }
        });
    }
String addressadd;
    private void getaddress() {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(pickupLatLng.latitude,
                    pickupLatLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {



                 addressadd = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                //String cityadd = addresses.get(0).getLocality();
                //String stateadd = addresses.get(0).getAdminArea();
                //String countryadd = addresses.get(0).getCountryName();
                //String postalCodeadd = addresses.get(0).getPostalCode();
                //String knownNameadd = addresses.get(0).getFeatureName(); // Only if available else return NULL

                place_location.setText(addressadd);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        displayLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
