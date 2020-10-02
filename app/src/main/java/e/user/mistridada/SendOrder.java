package e.user.mistridada;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.CreateOrder;
import e.user.mistridada.Model.DoozyInfo;
import e.user.mistridada.Model.FCMResponse;
import e.user.mistridada.Model.Notification;
import e.user.mistridada.Model.Rider;
import e.user.mistridada.Model.Sender;
import e.user.mistridada.Model.Token;
import e.user.mistridada.Model.Wallet;
import e.user.mistridada.Remote.IFCMService;
import e.user.mistridada.Remote.IGoogleAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendOrder extends AppCompatActivity {

    
    //Order button
    Button btnPickupRequest;
    private LatLng latLng;
    private LatLng latLng2;

    boolean isDriverFound=false;
    private String driverId="";
    private String orderId="";
    private String charges;
    private String basefare;
    private String total;

    DatabaseReference drivers;
    GeoFire geoFire;

    int radius = 1; //1km

    private static final int LIMIT = 7;
    
    //Send alert
    IFCMService mService;
    IGoogleAPI gService;

    DatabaseReference databaseorder;
    DatabaseReference doozyinfo;

    private String pickupadd1;
    private String dropadd1;
    private String content1;
    private String taskdtl;
    private Double distance1;
    private Integer subtotal;
    private Integer debitamount;
    private Integer walletamount;
    android.app.AlertDialog waitingDialog;

    String adminid = "1pX1fUKTxTh2vlmSvN10cMu53Zj2";

    TextView pickname,dropname,contentname,mobile,amount,disprice, notice,taskdetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_order);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseorder = FirebaseDatabase.getInstance().getReference(Common.create_order);
        fetchprice();
        waitingDialog = new SpotsDialog(SendOrder.this);




        pickname = (TextView)findViewById(R.id.pickupadd);
        dropname = (TextView)findViewById(R.id.deliveryadd);
        contentname = (TextView)findViewById(R.id.packagecontent);
        mobile = (TextView)findViewById(R.id.mobile);
        amount = (TextView)findViewById(R.id.charges);
        disprice = (TextView)findViewById(R.id.discountprice);
        notice = (TextView)findViewById(R.id.notice);
        taskdetail = (TextView)findViewById(R.id.txt_dtl);
        disprice.setVisibility(View.INVISIBLE);
        notice.setVisibility(View.INVISIBLE);

        mService = Common.getFCMService();
        gService = Common.getGoogleAPI();

        Intent intent = getIntent();
        latLng = intent.getParcelableExtra("pickupaddval");
        latLng2 = intent.getParcelableExtra("destinationLatlag");

        //Getting both the coordinates
        LatLng from = new LatLng(latLng.latitude,latLng.longitude);
        LatLng to = new LatLng(latLng2.latitude,latLng2.longitude);

        //Calculating the distance in meters
        distance1 = SphericalUtil.computeDistanceBetween(from, to);

        getDirection();


        if(getIntent() !=null) {
            pickupadd1 = getIntent().getStringExtra("pickupaddress");
            dropadd1 = getIntent().getStringExtra("deliveryaddress");
            content1 = getIntent().getStringExtra("packagecontents");
            taskdtl = getIntent().getStringExtra("taskdetail");

        }
        pickname.setText(pickupadd1);
        dropname.setText(dropadd1);
        contentname.setText(content1);
        taskdetail.setText(taskdtl);
        mobile.setText(Common.currentUser.getPhone());

        btnPickupRequest=(Button)findViewById(R.id.sendorder);
        btnPickupRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addOrder();
                if(!isDriverFound)
                   requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());

            }
        });


        updateFirebaseToken();


    }

    private void getDirection() {


        String requestApi = null;
        try{

            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+latLng.latitude+","+latLng.longitude+"&"+
                    "destination="+latLng2.latitude+","+latLng2.longitude+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("Mistridada",requestApi); //Print URL for debug

            gService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONArray routes = jsonObject.getJSONArray("routes");

                                //After get routes , just get first element of routes
                                JSONObject object = routes.getJSONObject(0);

                                //after get first element, we need get array with name 'legs'
                                JSONArray legs = object.getJSONArray("legs");

                                //and get first element of legs Array
                                JSONObject legsObject = legs.getJSONObject(0);

                                //Now, get distance
                                JSONObject distance = legsObject.getJSONObject("distance");
                                //pickname.setText(distance.getString("text"));

                                //get Time
                                JSONObject time = legsObject.getJSONObject("duration");
                                //dropname.setText(time.getString("text"));

                                //Get Address
                                String address = legsObject.getString("end_address");
                                //contentname.setText(address);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(SendOrder.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void fetchprice() {
       final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("DoozyInfo");
                doozy.child("-LlREybDHInYc4ycvYhq").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DoozyInfo driverUser = dataSnapshot.getValue(DoozyInfo.class);
                        //if(getIntent().getStringExtra("changecharge").equals("rest"));
                            charges=driverUser.getCost();
                            basefare=driverUser.getVersionname();

                            final double charge1 = Double.parseDouble(charges);
                            final double charge2 = Double.parseDouble(basefare);
                            final double km=distance1/1000;
                            final double total1= charge1*km;
                            final double totald=total1+charge2;
                            subtotal= (int)totald;
                            String ftotal =Integer.toString(subtotal);
                            total =Integer.toString(subtotal);
                            amount.setText(ftotal);
                            debitamount = 0;
                            usewallet();

                        //else if(getIntent().getStringExtra("changecharge").equals("restnot"));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void usewallet() {
        final DatabaseReference doozy = FirebaseDatabase.getInstance().getReference("Wallet");
        doozy.child(Common.currentUser.getPhone()).child("credits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null) {
                    String cost = dataSnapshot.getValue(String.class);
                    walletamount = Integer.parseInt(cost);
                    if (walletamount >= subtotal){
                        debitamount = walletamount-subtotal;
                        total ="0";
                        disprice.setVisibility(View.VISIBLE);
                        disprice.setText("0");
                        amount.setPaintFlags(amount.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                        notice.setVisibility(View.VISIBLE);
                        String amt = Integer.toString(subtotal);
                        notice.setText("₹ "+amt+ " will be used from doosy wallet.");
                    }
                    else {
                        final int lessamount = subtotal-walletamount;
                        debitamount = 0;
                        total =Integer.toString(lessamount);
                        amount.setPaintFlags(amount.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                        disprice.setVisibility(View.VISIBLE);
                        disprice.setText(total);
                        notice.setVisibility(View.VISIBLE);
                        String amt = Integer.toString(walletamount);
                        notice.setText("₹ "+amt+ " will be used from doosy wallet.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);
    }

    private void sendRequestToDriver(String driverId) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference(Common.token_tbl);

        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class); //Get token object from database with key

                            //Make raw payload- convert LatLng to json
                            String json_lat_lng = new Gson().toJson(new LatLng(latLng.latitude,latLng.longitude));
                            String order= orderId;
                            Notification data = new Notification(order,json_lat_lng); // send it to driver app and we will deserialise it again
                            Sender content = new Sender(token.getToken(),data); //send this data to token

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if(response.body().success == 1) {
                                                Toast.makeText(SendOrder.this, "Request sent!", Toast.LENGTH_LONG).show();
                                                //updatewallet();
                                                sendRequestToAdmin();
                                            }
                                            else
                                                Toast.makeText(SendOrder.this, "Failed !", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void updatewallet() {
        final DatabaseReference referdb = FirebaseDatabase.getInstance().getReference("Wallet");

        Wallet rider = new Wallet();
        rider.setCredits(Integer.toString(debitamount));

        referdb.child(Common.currentUser.getPhone())
                .setValue(rider)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SendOrder.this, "", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendOrder.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendRequestToAdmin() {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference(Common.token_tbl);

        tokens.orderByKey().equalTo(adminid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class); //Get token object from database with key

                            //Make raw payload- convert LatLng to json
                            String json_lat_lng = new Gson().toJson(new LatLng(latLng.latitude,latLng.longitude));
                            String order= "Customer want to send package";
                            Notification data = new Notification(order,json_lat_lng); // send it to driver app and we will deserialise it again
                            Sender content = new Sender(token.getToken(),data); //send this data to token

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if(response.body().success == 1) {
                                                Toast.makeText(SendOrder.this, "Done!", Toast.LENGTH_LONG).show();
                                            }
                                            else
                                                Toast.makeText(SendOrder.this, "!", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void requestPickupHere(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid,new GeoLocation(latLng.latitude,latLng.longitude));

        findDriver();
    }

    private void findDriver() {
        waitingDialog.show();
        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
        GeoFire gfDrivers = new GeoFire(drivers);

        GeoQuery geoQuery = gfDrivers.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),
                radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                //if found
                if(!isDriverFound)
                {
                    isDriverFound = true;
                    driverId = key;
                    addOrder();

                    //Toast.makeText(SendOrder.this, ""+key, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //if still not found Mistri, increase distance
                if(!isDriverFound && radius <LIMIT )
                {
                    radius++;
                    findDriver();
                }else  if (!isDriverFound && radius ==LIMIT){
                    waitingDialog.dismiss();
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(SendOrder.this);
                    dialog.setTitle("OHH!!");
                    dialog.setMessage("We couldn't find delivery partner at your pickup address. Please try to change pickup address.");
                    LayoutInflater inflater = LayoutInflater.from(SendOrder.this);
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            waitingDialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void addOrder() {
        final String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        waitingDialog.dismiss();
        final String pickupaddress = pickupadd1;
        final String deliveryaddress = dropadd1;
        final String packagecontent = content1;
        final String customerid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String customername = FirebaseInstanceId.getInstance().getToken();
        final String customerphone = driverId; //Replacing driverId with "searching"
        final String pickupcharges = total;
        final String orderstatus = "searching";
        final String passedby = "0";
        final String date = saveCurrentDate;
        final String time = saveCurrentTime;
        final String packagedetail = taskdtl;
        final Double pickuplat=latLng.latitude;
        final Double pickuplon=latLng.longitude;
        final Double droplat=latLng2.latitude;
        final Double droplon=latLng2.longitude;

        if(!TextUtils.isEmpty(pickupaddress) && total!=null){
            orderId = databaseorder.push().getKey();
            final String orderid = orderId;
            //fields should be in order as CreateOrder Constructor
            CreateOrder artist = new CreateOrder(pickupaddress, deliveryaddress, packagecontent, customerid, customername, customerphone,pickupcharges,orderstatus,passedby,date,time,packagedetail,orderid,pickuplat,pickuplon,droplat,droplon);

            databaseorder.child(orderId).setValue(artist);
            Toast.makeText(this, "Order Submitted!!", Toast.LENGTH_LONG).show();
            updatewallet();
            assignpartner();
            sendRequestToDriver(driverId);
            //sendRequestToAdmin();
            Intent intent = new Intent(SendOrder.this,Manlist.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "You should enter pickup address", Toast.LENGTH_LONG).show();
        }
    }

    private void assignpartner() {
        //Geo Fire
        /***drivers = FirebaseDatabase.getInstance().getReference(Common.searchpartner).child(driverId);
        geoFire = new GeoFire(drivers);
        geoFire.setLocation(orderId, new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Searching Partner...", Toast.LENGTH_LONG).show();

            }
        });***/
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference("OrderPending").child(driverId);
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("orderid", orderId);
        cartListRef
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Assigned", Toast.LENGTH_SHORT).show();
                            // Remove immediately & Keep Driver Offline when order sent So that other order is not sent to him
                            final DatabaseReference removedriver = FirebaseDatabase.getInstance().getReference(
                                    Common.driver_tbl).child(driverId);
                            removedriver.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    removedriver.removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
