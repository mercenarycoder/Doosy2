package e.user.mistridada;

import android.annotation.TargetApi;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.DoozyInfo;
import e.user.mistridada.Model.Wallet;
import io.paperdb.Paper;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ViewPager v_flipper;
    private CardView mazdoor,mistri,mistri1,Restaurant, Grocery, Gifts, FruitVeg, ChickenStore;


    //New Update information
    CircleImageView imageAvatar;
    TextView txtRiderName;
    String labour;
    TextView offer,offerm,address_choosed;
    RelativeLayout alert_get_location;
    ImageView mbanner;
    StorageReference viewFlipper_images;
    DatabaseReference image_banner;
    //Declare firebase to upload avatar
    FirebaseStorage storage;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    String img_arr[];
    LatLng latLng;
    private Timer timer;
    int currentPage=0;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    Context mContext;
    @Override
    protected void onStart() {
        super.onStart();
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
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
                alertDialog.setMessage("For Smoothly Using Dossy its Mandatory");
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
                alertDialog.setMessage("For Smoothly Using Dossy its Mandatory");
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
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        preferences=Home.this.getApplicationContext()
                .getSharedPreferences("location_user",getApplicationContext().MODE_PRIVATE);
        editor=preferences.edit();

        if(preferences.getString("latitude","0.0").equals("0.0"))
        {
            final Dialog dialog = new Dialog(Home.this, 0);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.choose_one);
            Button close = (Button) dialog.findViewById(R.id.close_chooser);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(Home.this)
                            .setMessage("Please choose a location or else shops will not be visible to you..")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            });
            Button use_current = (Button) dialog.findViewById(R.id.use_current);
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
                                    startActivityForResult(intent, ALL_PERMISSIONS_RESULT);
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //   dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        } else {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                            alertDialog.setTitle("GPS is not Enabled!");
                            alertDialog.setMessage("Do you want to turn on GPS?");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, ALL_PERMISSIONS_RESULT);
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        }
                    } else {
                        giveCurrent();
                        first_check = true;
                        if (raat)
                            dialog.dismiss();
                    }
                }
            });
            Button choose_address = (Button) dialog.findViewById(R.id.use_address);
            choose_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Home.this, Welcome.class);
                    startActivityForResult(intent, 1);
                    first_check = true;
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        else
        {
            double latitude=Double.parseDouble(preferences.getString("latitude","27.13"));
            double longitude=Double.parseDouble(preferences.getString("longitude","34.13"));
            List<Address> addresses;
            try {
                Geocoder geocoder;
                geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if(addresses.size()>0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    address_choosed.setText(address.substring(0, 25));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
//shows a dialog at the start of app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new
                                String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
        }


        image_banner.child("menu").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String urls=dataSnapshot.getKey();
                String urls2= String.valueOf(dataSnapshot.getChildren());
                String url3= String.valueOf(dataSnapshot.getValue());
               // Toast.makeText(Home.this,url3,Toast.LENGTH_SHORT).show();
                img_arr=url3.split(",");
              //   Toast.makeText(Home.this,String.valueOf(img_arr.length),Toast.LENGTH_SHORT).show();
              //  for (int i =0; i < img_arr.length; i++){
                //    flipperImages(img_arr[i]);
                //}
                ViewPagerAdapter adapter=new ViewPagerAdapter(Home.this,img_arr);
                v_flipper.setAdapter(adapter);
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == img_arr.length) {
                            currentPage = 0;
                        }
                        v_flipper.setCurrentItem(currentPage++, true);
                    }
                };

                timer = new Timer(); // This will create a new Thread
                timer.schedule(new TimerTask() { // task to be scheduled
                    @Override
                    public void run() {
                        handler.post(Update);

                    }
                }, 5000,6000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    boolean raat=false;
    public void giveCurrent()
    {
        locationTrack = new LocationTrack(Home.this);


        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            if(longitude==0.0||latitude==0.0)
            {
                showDialog();
                raat=false;
            }
            editor.putString("latitude",String.valueOf(latitude));
            editor.putString("longitude",String.valueOf(longitude));
            editor.apply();
            editor.commit();
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
               if(addresses.size()>0) {
                   String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                   String city = addresses.get(0).getLocality();
                   String state = addresses.get(0).getAdminArea();
                   String country = addresses.get(0).getCountryName();
                   String postalCode = addresses.get(0).getPostalCode();
                   String knownName = addresses.get(0).getFeatureName();
                   address_choosed.setText(address.substring(0, 24));
               }
            } catch (IOException e) {
                e.printStackTrace();
            }

            raat=true;
         //   Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) +
           //         "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            System.out.println(preferences.getString("latitude","fuck")+"       "+
                    preferences.getString("logitude","fuck u")+"-------------------------------------");
        } else {

            locationTrack.showSettingsAlert();
        }
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
  if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
   showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
     new DialogInterface.OnClickListener() {
                                        @Override
  public void onClick(DialogInterface dialog, int which) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
  requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]),
          ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;}
    }

    boolean first_check=false;
    @Override
    protected void onPause() {
        super.onPause();
       if(first_check) {
          timer.cancel();
       }
       }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Home.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //locationTrack.stopListener();
    }
    public void showDialog()
    {
        final Dialog dialog=new Dialog(Home.this, 0);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.choose_one);
        Button close=(Button)dialog.findViewById(R.id.close_chooser);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first_check=true;
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
                    first_check = true;
                    dialog.dismiss();
                }
            }

        });
        Button choose_address=(Button)dialog.findViewById(R.id.use_address);
        choose_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,Welcome.class);
                startActivityForResult(intent,1);
                first_check=true;
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //some permissions will be displayed here
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        mContext=Home.this;
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray
                        (new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        //this is the dialog used to choose location of the user

        address_choosed=(TextView)findViewById(R.id.address_choosed);

        //this start dialog ends here
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        image_banner=FirebaseDatabase.getInstance().getReference("banner").child("Banner_urls");
        alert_get_location=(RelativeLayout) findViewById(R.id.alert_get_location);

        latLng=new LatLng(0,0);
        alert_get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        int images[]= {R.drawable.banner5, R.drawable.banner3};
        v_flipper = findViewById(R.id.v_flipper);

        //mbanner = (ImageView)findViewById(R.id.banner1);



        offer = (TextView)findViewById(R.id.doozyoffer);
        offerm = (TextView)findViewById(R.id.doozyoffer1);

        fetchprice();
        //defining Cards
        mazdoor = (CardView) findViewById(R.id.sendpack);
        mistri = (CardView) findViewById(R.id.ShopCard);
        mistri1 = (CardView) findViewById(R.id.mistri1);
        Restaurant = (CardView) findViewById(R.id.sendpacka);
        Grocery = (CardView) findViewById(R.id.ShopCardd);
        Gifts = (CardView) findViewById(R.id.ShopCardb);
        FruitVeg = (CardView) findViewById(R.id.ShopCarda);
        ChickenStore = (CardView) findViewById(R.id.ShopCardc);


        //Add Click Listener to the cards
        mazdoor.setOnClickListener(this);
        mistri.setOnClickListener(this);
        mistri1.setOnClickListener(this);
        Restaurant.setOnClickListener(this);
        Grocery.setOnClickListener(this);
        Gifts.setOnClickListener(this);
        FruitVeg.setOnClickListener(this);
        ChickenStore.setOnClickListener(this);


        //Init Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,About.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView txtName = (TextView)navigationHeaderView.findViewById(R.id.txtRiderName);
        CircleImageView imageAvatar = (CircleImageView)navigationHeaderView.findViewById(R.id.imageAvatar);

        // We can access from Common.currentUser because in main activity, after login, we have set this data
        txtName.setText(Common.currentUser.getName());

        //Load Avatar
        if(Common.currentUser.getAvatarUrl() != null
                && !TextUtils.isEmpty(Common.currentUser.getAvatarUrl())) {
            Picasso.with(this)
                    .load(Common.currentUser.getAvatarUrl())
                    .into(imageAvatar);
        }

    }


    private void fetchprice() {
        final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("DoozyInfo");
        doozy.child("-LlREybDHInYc4ycvYhq").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DoozyInfo driverUser = dataSnapshot.getValue(DoozyInfo.class);
                offer.setText(driverUser.getOffer1());
                offerm.setText(driverUser.getOffer2());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Home.this,Help.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_updateInformation)
        {
            showUpdateInformationDialog();
        }
        else if (id == R.id.nav_signOut)
        {
            signOut();
        }
        else if (id == R.id.nav_aboutUs)
        {
            aboutUs();
        }
        else if (id == R.id.nav_help)
        {
            startActivity(new Intent(Home.this,Help.class));
        }
        else if (id == R.id.nav_orders)
        {
            startActivity(new Intent(Home.this,Manlist.class));
        }
        /**else if (id == R.id.nav_orders_grocery)
        {
            startActivity(new Intent(Home.this,GroceryHistory.class));
        }**/
        else if (id == R.id.nav_share)
        {
            startActivity(new Intent(Home.this,Share.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void aboutUs() {
        Intent intent = new Intent(Home.this,About.class);
        startActivity(intent);
    }

    private void showUpdateInformationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("UPDATE INFORMATION");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_update_information,null);

        final MaterialEditText edtName = (MaterialEditText)layout_pwd.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText)layout_pwd.findViewById(R.id.edtPhone);
        final ImageView image_upload = (ImageView) layout_pwd.findViewById(R.id.image_upload);

        String phone = Paper.book().read(Common.phone_field);
        if(phone != null){
            edtPhone.setVisibility(View.INVISIBLE);
        }
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        alertDialog.setView(layout_pwd);

        //Set Button
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                String name = edtName.getText().toString();
                String phone = edtPhone.getText().toString();

                Map<String,Object> updateInfo = new HashMap<>();
                if(!TextUtils.isEmpty(name))
                    updateInfo.put("name",name);
                if(!TextUtils.isEmpty(phone))
                    updateInfo.put("phone",phone);

                DatabaseReference riderInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                riderInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(Home.this, "Information Updated !", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(Home.this, "Information Update failed !", Toast.LENGTH_SHORT).show();

                                waitingDialog.dismiss();
                            }
                        });

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture: "),Common.PICK_IMAGE_REQUEST);
    }

    //Ctrl+O


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(resultCode==1)
       {
          //Toast.makeText(Home.this,"Reached here",Toast.LENGTH_SHORT).show();
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

           try {
               addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
               String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
               String city = addresses.get(0).getLocality();
               String state = addresses.get(0).getAdminArea();
               String country = addresses.get(0).getCountryName();
               String postalCode = addresses.get(0).getPostalCode();
               String knownName = addresses.get(0).getFeatureName();
          //     Toast.makeText(Home.this,address,Toast.LENGTH_SHORT).show();
               address_choosed.setText(address.substring(0,24));
           } catch (IOException e) {
               e.printStackTrace();
           }

       }
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            Uri saveUri = data.getData();
            if(saveUri != null)
            {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading...");
                mDialog.show();

                String imageName = UUID.randomUUID().toString(); //Random name image upload
                final StorageReference imageFolder = storageReference.child("images/"+imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();

                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        //Update this Url to Avatar property of user
                                        //First, you need add avatar property on user model
                                        Map<String,Object> avatarUpdate = new HashMap<>();
                                        avatarUpdate.put("avatarUrl",uri.toString());

                                        DatabaseReference riderInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                                        riderInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(avatarUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                            Toast.makeText(Home.this, "Avatar Uploaded !", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(Home.this, "Upload Error !", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage("Uploaded"+progress+"%");
                            }
                        });
            }
        }
    }

    private void signOut() {
        //Reset Remeber Value
        Paper.init(this);
        Paper.book().destroy();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Home.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.sendpack:{
                i = new Intent(this, RequestForm.class);
            i.putExtra("hint1","Pickup from"); startActivity(i);
            break;
            }
            case R.id.ShopCard: i = new Intent(this, RequestForm.class);
            i.putExtra("hint1","Enter Shop Name"); startActivity(i); break;
            case R.id.mistri1: i = new Intent(this, RequestForm.class);
            i.putExtra("hint1","Pickup from"); startActivity(i); break;
            case R.id.sendpacka: {
                if (latLng.latitude != 0 && latLng.longitude != 0) {
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "Restaurants");
                    startActivity(i);
                } else {
                    Toast.makeText(Home.this, "Please Pick A Location", Toast.LENGTH_SHORT);
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "Restaurants");
                    startActivity(i);

                }
                break;
            }
            case R.id.ShopCardd: {
                if (latLng.latitude != 0 && latLng.longitude != 0) {
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "GroceryStores");
                    startActivity(i);
                }
                else {
                    Toast.makeText(Home.this, "Please Pick A Location", Toast.LENGTH_SHORT);
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "GroceryStores");
                    startActivity(i);
                }
                break;
            }
            case R.id.ShopCardb: {
                if (latLng.latitude != 0 && latLng.longitude != 0) {
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "Gifts");
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(Home.this, "Please Pick A Location", Toast.LENGTH_SHORT);
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "Gifts");
                    startActivity(i);
                }
                break;
            }
            case R.id.ShopCarda: {
                if (latLng.latitude != 0 && latLng.longitude != 0) {
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "FruitVeg");
                    startActivity(i);
                }
                else
                {
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "FruitVeg");
                    startActivity(i);
                    Toast.makeText(Home.this, "Please Pick A Location", Toast.LENGTH_SHORT);
                }
                break;
            }
            case R.id.ShopCardc: {
                if (latLng.latitude != 0 && latLng.longitude != 0) {
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "ChickenMeatShop");
                    startActivity(i);
                }
                else
                {
                    i = new Intent(this, StoreList.class);
                    i.putExtra("shoptype", "ChickenMeatShop");
                    startActivity(i);
                    Toast.makeText(Home.this, "Please Pick A Location", Toast.LENGTH_SHORT);
                }
                break;
            }
            default:break;
        }
    }
}
