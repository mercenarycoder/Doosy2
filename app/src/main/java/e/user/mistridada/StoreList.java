package e.user.mistridada;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.Store_Model;
import e.user.mistridada.ViewHolder.StoreViewHolder;

public class StoreList extends AppCompatActivity
{

    private DatabaseReference ProductsRef,shop_ref;
    private RecyclerView recyclerView;
    private String id = "";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    RecyclerView.LayoutManager layoutManager;
    TextView shops_loader;
Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_layout);
        mContext=StoreList.this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbars);
        setSupportActionBar(myToolbar);
        shops_loader=(TextView)findViewById(R.id.shops_loader);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        id = getIntent().getStringExtra("shoptype");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Bhopal");
        shop_ref=FirebaseDatabase.getInstance().getReference().child("ShopLocations");
        recyclerView = findViewById(R.id.recycler_stores);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        shops_loader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
showDialog();
            }
        });
        shops_loader.setVisibility(View.INVISIBLE);
        shops_loader.setEnabled(false);

    }
    public void showDialog()
    {
        final Dialog dialog=new Dialog(StoreList.this, 0);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.choose_one);
        Button close=(Button)dialog.findViewById(R.id.close_chooser);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button use_current=(Button)dialog.findViewById(R.id.use_current);
        use_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                                startActivityForResult(intent,ALL_PERMISSIONS_RESULT);
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
                                startActivityForResult(intent,ALL_PERMISSIONS_RESULT);
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
                else {
                    giveCurrent();
                    dialog.dismiss();
                }
            }

        });
        Button choose_address=(Button)dialog.findViewById(R.id.use_address);
        choose_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StoreList.this,Welcome.class);
                startActivityForResult(intent,1);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1)
        {
            //Toast.makeText(Home.this,"Reached here",Toast.LENGTH_SHORT).show();
            LocationTrack locationTrack = new LocationTrack(StoreList.this);
            SharedPreferences preferences=   StoreList.this.getApplicationContext()
                    .getSharedPreferences("location_user",getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor  editor=preferences.edit();
            LatLng latLng;
            latLng=data.getParcelableExtra("pickupaddval");
            editor.putString("latitude",String.valueOf(latLng.latitude));
            editor.putString("longitude",String.valueOf(latLng.longitude));
            editor.apply();
            editor.commit();
            System.out.println(preferences.getString("latitude","fuck")+"       "+
                    preferences.getString("logitude","fuck u")+"-------------------------------------");
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
           super.recreate();
        }
    }


    public void giveCurrent()
    {

       LocationTrack locationTrack = new LocationTrack(StoreList.this);
      SharedPreferences preferences=   StoreList.this.getApplicationContext()
              .getSharedPreferences("location_user",getApplicationContext().MODE_PRIVATE);
      SharedPreferences.Editor  editor=preferences.edit();

        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            if(longitude==0.0||latitude==0.0)
            {
                showDialog();
            }
            editor.putString("latitude",String.valueOf(latitude));
            editor.putString("longitude",String.valueOf(longitude));
            editor.apply();
            editor.commit();
            Geocoder geocoder;
           super.recreate();
        } else {

            locationTrack.showSettingsAlert();
        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences=getApplicationContext().getSharedPreferences("location_user",
                getApplicationContext().MODE_PRIVATE);

        String latitude=preferences.getString("latitude","33.099");
        final double lati=Double.parseDouble(latitude);
        String longitude=preferences.getString("longitude","23.099");
        final double longi=Double.parseDouble(longitude);
        System.out.println(lati+"             "+longi+"---------------------------------------");
        final ArrayList <Store_Model> list=new ArrayList<>();
        ProductsRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postshot:dataSnapshot.getChildren()) {
                    Store_Model model = postshot.getValue(Store_Model.class);
                    System.out.println(distance(19.0728300,72.8826100 , model.getLatitude(), model.getLongitude()));
                    if ((distance(lati, longi, model.getLatitude(), model.getLongitude())) <= 15.0) {
                        list.add(model);
                    }
                    else
                    {
                      //  list.add(model);
                    }
                }
                if(list.size()==0)
                {
                    shops_loader.setVisibility(View.VISIBLE);
                    shops_loader.setEnabled(true);

                }
                else {
                    dhasboardmainadapter2 adapter = new dhasboardmainadapter2(list, StoreList.this, id);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*
        final FirebaseRecyclerOptions<Store_Model> options =
                new FirebaseRecyclerOptions.Builder<Store_Model>()
                        .setQuery(ProductsRef, Store_Model.class)
                        .build();


        FirebaseRecyclerAdapter<Store_Model, StoreViewHolder> adapter =
                new FirebaseRecyclerAdapter<Store_Model, StoreViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final StoreViewHolder holder,
                                                    int position, @NonNull final Store_Model model)
                    {
                        holder.txtStoreName.setText(model.getName());
                        holder.txtStoreAddress.setText(model.getAddress());
                        holder.txtStoreFoods.setText(model.getType());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(StoreList.this, StoreProfile.class);
                                intent.putExtra("merchantkey", model.getMerchant());
                                intent.putExtra("shoptype", id);
                                startActivity(intent);
                            }
                        });

                        if(model.getImage() != null
                                && !TextUtils.isEmpty(model.getImage())) {
                            Picasso.with(getBaseContext())
                                    .load(model.getImage())
                                    .into(holder.imageView);
                        }


                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public int getItemViewType(int position) {
                        return position;
                    }

                    @NonNull
                    @Override
                    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_listing_layout,
                                parent, false);
                        StoreViewHolder holder = new StoreViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
