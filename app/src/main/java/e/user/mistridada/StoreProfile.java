package e.user.mistridada;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.Store_Model;

public class StoreProfile extends AppCompatActivity implements Animator.AnimatorListener, Animation.AnimationListener {

    EditText  editTextTask, editTextName2, editTextPhone, editTextAddress;
    EditText editTextDestination;
    Button buttonAdd;
    Spinner spinnerService;
    private String storetype, phone, merchant, address, cuisine;
    private String merID;
    TextView textAddress, textMerchant, textCuisine;
    DatabaseReference dbOD;
    ImageView imageView;
    LinearLayout linearLayout;
    private LatLng pickupLatLng3;
    private static final String TAG = "StoreProfile";
    TextView view_menu2;
    int count=0;
    ArrayList<String> image_url;
    boolean check_it=false;
    int[] j;
    private LatLng destinationLatlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        j = new int[]{0};
        image_url=new ArrayList<>();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        destinationLatlag=new LatLng(0,0);
        //get intent
        pickupLatLng3=new LatLng(0, 0);
        if(getIntent() !=null)
        {
            merID = getIntent().getStringExtra("merchantkey");
        }

        storetype = getIntent().getStringExtra("shoptype");

        dbOD = FirebaseDatabase.getInstance().getReference("Bhopal").child(storetype);
        editTextDestination = (EditText) findViewById(R.id.edittxtAdd);
        editTextDestination.setInputType(InputType.TYPE_NULL);
        editTextDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StoreProfile.this,Welcome1.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,2);
            }
        });
        editTextTask = (EditText) findViewById(R.id.txttaskDetail);
        textAddress = (TextView)findViewById(R.id.txtmerchantadd);
        textMerchant = (TextView)findViewById(R.id.txtmerchant);
        textCuisine = (TextView)findViewById(R.id.txtcuisine);
        imageView = (ImageView) findViewById(R.id.store_image_pro);
        linearLayout = (LinearLayout) findViewById(R.id.callask);
        view_menu2=(TextView)findViewById(R.id.view_menu2);
        //editTextName2 = (EditText) findViewById(R.id.edtseName2);
        //editTextAddress = (EditText) findViewById(R.id.edtseAddress);
        buttonAdd = (Button) findViewById(R.id.nextact);
        //spinnerService = (Spinner) findViewById(R.id.spinnerservice);
        //editTextName1.setVisibility(View.GONE);
        //editTextName.setHint(hint);
        view_menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!image_url.get(0).equals("null")) {
                    j[0]=0;
                    final Dialog dialog = new Dialog(StoreProfile.this, 0);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.webview_qrscanner);
                    final ImageButton previous_button = (ImageButton) dialog.findViewById(R.id.previous_button);
                    final ImageButton next_button = (ImageButton) dialog.findViewById(R.id.next_button);
                    final ImageButton zoom_in=(ImageButton)dialog.findViewById(R.id.zoom_in);
                    final ImageButton zoom_out=(ImageButton)dialog.findViewById(R.id.zoom_out);
                    Button close_button = (Button) dialog.findViewById(R.id.close_button12);
                    Context context=getApplicationContext();
                    final Animation animation_in=AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                    final Animation animation_out=AnimationUtils.loadAnimation(context, R.anim.zoom_out);
                    animation_in.setAnimationListener(StoreProfile.this);
                    final ImageView menu_view = (ImageView) dialog.findViewById(R.id.menu_view);
                    zoom_in.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           menu_view.startAnimation(animation_in);
                        }
                    });
                    zoom_out.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menu_view.startAnimation(animation_out);
                        }
                    });
                    final float[] xCoOrdinate = new float[1];
                    final float[] yCoOrdinate = new float[1];
                    menu_view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {
                            switch (event.getActionMasked()) {
                                case MotionEvent.ACTION_DOWN:
                                    xCoOrdinate[0] = view.getX() - event.getRawX();
                                    yCoOrdinate[0] = view.getY() - event.getRawY();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    view.animate().x(event.getRawX() + xCoOrdinate[0]).y
                                            (event.getRawY() + yCoOrdinate[0]).setDuration(0).start();
                                    break;
                                default:
                                    return false;
                            }
                            return true;
                        }
                    });
                    final TextView menu_not_valid = (TextView) dialog.findViewById(R.id.menu_not_valid);
                    Picasso.with(getBaseContext())
                            .load(image_url.get(0))
                            .error(R.drawable.ic_profile)
                            .into(menu_view);
                    //j[0]++;
                    if(image_url.size()==1)
                    {
                     next_button.setVisibility(View.INVISIBLE);
                     next_button.setEnabled(false);
                        previous_button.setVisibility(View.INVISIBLE);
                        previous_button.setEnabled(false);
                    }
                    menu_not_valid.setText("1/"+image_url.size());
                    next_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(image_url.size()==1)
                            {
                                next_button.setVisibility(View.INVISIBLE);
                                next_button.setEnabled(false);
                            }
                            j[0]++;
                            if (j[0]>=image_url.size()) {
                          next_button.setVisibility(View.INVISIBLE);
                          next_button.setEnabled(false);
        Toast.makeText(StoreProfile.this,"You Reached End Of Menu List",Toast.LENGTH_SHORT).show();
                            } else {
                                Picasso.with(getBaseContext())
                                        .load(image_url.get(j[0]))
                                        .error(R.drawable.ic_profile)
                                        .into(menu_view);
                                if(j[0]>0)
                                {
                                    previous_button.setEnabled(true);
                                    previous_button.setVisibility(View.VISIBLE);
                                }

                                menu_not_valid.setText((j[0]+1)+"/"+image_url.size());
                            }
                        }
                    });
                    close_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    previous_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(image_url.size()==1)
                            {
                                previous_button.setVisibility(View.INVISIBLE);
                                previous_button.setEnabled(false);
                            }
                            j[0]--;
                            if (j[0] < 0) {
                            previous_button.setVisibility(View.INVISIBLE);
                            previous_button.setEnabled(false);
                                //j[0] = 0;
              Toast.makeText(StoreProfile.this,"Go forward",Toast.LENGTH_SHORT).show();

                            } else {
                                Picasso.with(getBaseContext())
                                        .load(image_url.get(j[0]))
                                        .error(R.drawable.ic_profile)
                                        .into(menu_view);
                                if(j[0]<image_url.size()-1)
                                {
                                    next_button.setVisibility(View.VISIBLE);
                                    next_button.setEnabled(true);
                                }
                                menu_not_valid.setText((j[0]+1)+"/"+image_url.size());
                            }
                        }
                    });
                    dialog.show();
                }
                else
                {
   Toast.makeText(StoreProfile.this,"No Menu To Show",Toast.LENGTH_SHORT).show();
                }
                }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_it) {
                    if(checkDistance()){
                    Intent intent = new Intent(StoreProfile.this, SendOrder.class);
                    intent.putExtra("pickupaddress", merchant + ": " + address);
                    intent.putExtra("deliveryaddress", editTextDestination.getText().toString().trim());
                    intent.putExtra("packagecontents", storetype);
                    intent.putExtra("taskdetail", editTextTask.getText().toString().trim());
                    Bundle b = new Bundle();
                    b.putParcelable("pickupaddval", pickupLatLng3);
                    intent.putExtras(b);
                    Bundle c = new Bundle();
                    c.putParcelable("destinationLatlag", destinationLatlag);
                    intent.putExtras(c);

                    //intent.putExtra("changecharge", "restnot");
                    startActivity(intent);}
                    else
                    {

                    }
                    //return;
                }
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneNumber();
            }
        });

    }
    public boolean checkDistance()
    {
        if(SphericalUtil.computeDistanceBetween(pickupLatLng3,destinationLatlag) <3){
            Toast.makeText(StoreProfile.this, "Pickup location and drop location is " +
                    "very close or same. Please change!!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(SphericalUtil.computeDistanceBetween(pickupLatLng3,destinationLatlag) >40000){
            Toast.makeText(StoreProfile.this, "Distance is too long!!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == 2) {
                destinationLatlag = data.getParcelableExtra("destinationLatlag");
                editTextDestination.setText(data.getStringExtra("dest_address"));
                check_it = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbOD.child(merID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Store_Model model = dataSnapshot.getValue(Store_Model.class);

                textAddress.setText(model.getAddress());
                textMerchant.setText(model.getName());
                textCuisine.setText(model.getType());

                merchant = model.getName();
                address = model.getAddress();
                cuisine = model.getType();
                double  lati=23.00;
                double longi=33.5;
                if(!model.getLatitude().equals(""))
                {
                    lati=model.getLatitude();
                }
                if(!model.getLongitude().equals(""))
                {
                    longi=model.getLongitude();
                }
                pickupLatLng3 = new LatLng(lati,longi);

                if(model.getPhone()!=null){
                    phone = model.getPhone();
                }

                if(model.getImage() != null
                        && !TextUtils.isEmpty(model.getImage())) {
                    Picasso.with(getBaseContext())
                            .load(model.getImage())
                            .into(imageView);
                }
                String images_menu="null";
                if(model.getImage()!=null
                &&!TextUtils.isEmpty(model.getMenu()))
                {
                images_menu=model.getMenu();
                }
                System.out.println(images_menu);
                String arr_menus[]=images_menu.split(",");
                for(int i=0;i<arr_menus.length;i++)
                {
                   image_url.add(arr_menus[i]);
                }
                if(images_menu.equals("null"))
                {
                    image_url.add("null");
                }
                System.out.println(images_menu+"---------------------------------.-...................");
               //image_url.add(images_menu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
            else
            {
                Log.e(TAG, "Permission not Granted");
            }
        }
    }

    private void callPhoneNumber() {

        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(StoreProfile.this, new String[]{android.Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
