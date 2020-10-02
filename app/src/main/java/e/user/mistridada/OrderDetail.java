package e.user.mistridada;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.CreateOrder;
import e.user.mistridada.Model.Rider;

public class OrderDetail extends AppCompatActivity {

    TextView txt_pickup,txt_drop,txt_content,txt_charges,txt_phone,
            txt_name,txtdate,txttime,txt_Odetail,txtstatus;
    ImageView btn_call,btn_location;
    CircleImageView avatar_image;
    LinearLayout lay_track;
    LinearLayout bottomlay;

    private LatLng latLng;
    private LatLng latLng2;
    private static final String TAG = "OrderDetail";

    String driverid;
    String orderID;
    String phone;
    String status;
    DatabaseReference dbOD;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbOD = FirebaseDatabase.getInstance().getReference(Common.create_order);
        //Init View

        avatar_image = (CircleImageView)findViewById(R.id.driver_avatar);
        txt_pickup = (TextView)findViewById(R.id.pickupadddetail);
        txt_drop = (TextView)findViewById(R.id.deliveryadddetail);
        txt_content = (TextView)findViewById(R.id.packagecontentdetail);
        txt_charges = (TextView)findViewById(R.id.chargesdetail);
        txt_name = (TextView)findViewById(R.id.drivername);
        txtdate = (TextView)findViewById(R.id.txt_date);
        txttime = (TextView)findViewById(R.id.txt_time);
        txtstatus = (TextView)findViewById(R.id.txt_status);
        txt_Odetail = (TextView)findViewById(R.id.txt_dtl);
        bottomlay = (LinearLayout) findViewById(R.id.partnerlay);
        btn_call = (ImageView)findViewById(R.id.imgBtnCall);
        lay_track = (LinearLayout)findViewById(R.id.trackman);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneNumber();
            }
        });
        btn_location = (ImageView)findViewById(R.id.track);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrackOrder.class);

                Bundle c = new Bundle();
                c.putParcelable("pickupaddval", latLng);
                intent.putExtras(c);

                Bundle b = new Bundle();
                b.putParcelable("dropaddva1", latLng2);
                intent.putExtras(b);

                startActivity(intent);
            }
        });

        lay_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrackOrder.class);
                intent.putExtra("OID",orderID);
                intent.putExtra("trackman","yes");
                intent.putExtra("manId",driverid);
                Bundle c = new Bundle();
                c.putParcelable("pickupaddval", latLng);
                intent.putExtras(c);

                Bundle b = new Bundle();
                b.putParcelable("dropaddva1", latLng2);
                intent.putExtras(b);

                startActivity(intent);
            }
        });

        //get intent
        if(getIntent() !=null)
        {
            orderID = getIntent().getStringExtra("orderid");


        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        dbOD.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CreateOrder odetail = dataSnapshot.getValue(CreateOrder.class);
                driverid = odetail.getCustomerphone();
                status = odetail.getOrderstatus();
                txt_pickup.setText(odetail.getPickupaddress());
                txt_drop.setText(odetail.getDeliveryaddress());
                txt_content.setText(odetail.getPackagecontent());
                txt_charges.setText(odetail.getPickupcharges());
                txtdate.setText(odetail.getDate());
                txttime.setText(odetail.getTime());
                txtstatus.setText(odetail.getOrderstatus());
                if(odetail.getPackagedetail()!=null){
                    txt_Odetail.setText(odetail.getPackagedetail());
                }

                if(status.equals("searching")){

                    bottomlay.setVisibility(View.INVISIBLE);
                }
                else if(status.equals("cancelled")){

                    bottomlay.setVisibility(View.INVISIBLE);
                }
                else if(status.equals("completed")){
                    bottomlay.setVisibility(View.INVISIBLE);
                }
                else{
                    bottomlay.setVisibility(View.VISIBLE);
                    loaddriverinfo(driverid);
                }

                latLng = new LatLng(odetail.getPickuplat(),odetail.getPickuplon());
                latLng2 = new LatLng(odetail.getDroplat(),odetail.getDroplon());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void callPhoneNumber() {

         try
            {
                if(Build.VERSION.SDK_INT > 22)
                {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling

                        ActivityCompat.requestPermissions(OrderDetail.this, new String[]{android.Manifest.permission.CALL_PHONE}, 101);

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

    private void loaddriverinfo(String driverid) {
        FirebaseDatabase.getInstance()
                .getReference(Common.user_driver_tbl)
                .child(driverid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Rider driverUser = dataSnapshot.getValue(Rider.class);
                        if(driverUser.getName() != null
                                && !TextUtils.isEmpty(driverUser.getName())) {
                            txt_name.setText(driverUser.getName());
                        }
                        if(driverUser.getAvatarUrl() != null
                                && !TextUtils.isEmpty(driverUser.getAvatarUrl())) {
                            Picasso.with(getBaseContext())
                                    .load(driverUser.getAvatarUrl())
                                    .into(avatar_image);
                        }
                        phone=driverUser.getPhone();

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

}
