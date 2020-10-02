package e.user.mistridada;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import e.user.mistridada.Common.Common;
import e.user.mistridada.Model.FCMResponse;
import e.user.mistridada.Model.Notification;
import e.user.mistridada.Model.Sender;
import e.user.mistridada.Model.Token;
import e.user.mistridada.Remote.IFCMService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderForm extends AppCompatActivity {

    EditText editTextPhone,editTextAddress;
    IFCMService mService;
    TextView txtamount;
    Button buttonAdd;
    private String totalAmount = "";
    String orderid="";
    String title = "We received Grocery Order";
    String adminid = "1pX1fUKTxTh2vlmSvN10cMu53Zj2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //totalAmount = getIntent().getStringExtra("Total Price");
        //Toast.makeText(this, "Total Price =  Rs. " + totalAmount, Toast.LENGTH_SHORT).show();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //txtamount = (TextView) findViewById(R.id.total);
        editTextPhone = (EditText) findViewById(R.id.refphone);
        editTextAddress = (EditText) findViewById(R.id.refadd);
        buttonAdd = (Button) findViewById(R.id.refbutton);

        updateFirebaseToken();
        mService = Common.getFCMService();

        //txtamount.setText(totalAmount);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String saveCurrentTime, saveCurrentDate;

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                saveCurrentDate = currentDate.format(calForDate.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calForDate.getTime());
                orderid= String.valueOf(System.currentTimeMillis());

                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("GroceryOrder").child(orderid);

                final HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("orderid", orderid);
                cartMap.put("name", Common.currentUser.getName());
                cartMap.put("total", totalAmount);
                cartMap.put("date", saveCurrentDate);
                cartMap.put("time", saveCurrentTime);
                cartMap.put("customer", FirebaseAuth.getInstance().getCurrentUser().getUid());
                cartMap.put("phone", Common.currentUser.getPhone());
                cartMap.put("phone2", editTextPhone.getText().toString().trim());
                cartMap.put("address", editTextAddress.getText().toString().trim());

                cartListRef
                        .updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    final DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Products");
                                    final DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("GroceryOrder").child(orderid).child("Products");
                                    fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if(databaseError !=  null){
                                                        Toast.makeText(OrderForm.this, "Failed!!", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    Toast.makeText(OrderForm.this, "OK", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(OrderForm.this, Done.class);
                                                                    intent.putExtra("Total Price", totalAmount);
                                                                    intent.putExtra("Orderid", orderid);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                        Toast.makeText(OrderForm.this, "Order placed successfully.", Toast.LENGTH_SHORT).show();
                                                        sendRequestToAdmin();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });                                }
                            }
                        });
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
                            Notification data = new Notification(title,orderid);
                            Sender content = new Sender(token.getToken(),data); //send this data to token

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if(response.body().success == 1) {
                                                Toast.makeText(OrderForm.this, "Done!", Toast.LENGTH_LONG).show();
                                            }
                                            else
                                                Toast.makeText(OrderForm.this, "Failed !", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
